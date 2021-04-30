package idlebot.arcade.whackagreg;

import idlebot.Game;
import ml.GregClassifier;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static idlebot.arcade.whackagreg.WhackAGregConstants.BEGIN_GAME_BUTTON_X;
import static idlebot.arcade.whackagreg.WhackAGregConstants.BEGIN_GAME_BUTTON_Y;
import static ml.ModelUtil.predict;

public class WhackAGregPlayerThreaded {
    private Robot robot;
    private Map<Rectangle, Long> lastClickedMap = new HashMap<>();
    private final int timeSinceLastClickMs = 100;
    private Game game;

    public WhackAGregPlayerThreaded(Game game) {
        this.game = game;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void playAGameOfGreg(Rectangle antiIdleRect, int column) {
        MultiLayerNetwork model = loadModelFromDisk();

        {
            game.clickWithinGameWithUiUpdateDelay(BEGIN_GAME_BUTTON_X, BEGIN_GAME_BUTTON_Y);
            game.waitMs(3500);
        }

        int loopCount = 0;
        long screenDumpTime = 0;
        long predictionTime = 0;
        long totalTime = 0;

        NativeImageLoader loader = new NativeImageLoader(40, 40);

        final List<Object> addObjectToStopLoop = new ArrayList<>();
        while (addObjectToStopLoop.size() < 5) {
            long totalStart = System.nanoTime();

            loopCount = loopCount + 1;

            long screenDumpStart = System.nanoTime();
            BufferedImage capture = robot.createScreenCapture(new Rectangle(antiIdleRect.x + WhackAGregConstants.RELATIVE_TOP_LEFT_X + column * WhackAGregConstants.SQUARE_WIDTH, antiIdleRect.y + WhackAGregConstants.RELATIVE_TOP_LEFT_Y, WhackAGregConstants.SQUARE_WIDTH * 12, WhackAGregConstants.SQUARE_HEIGHT * WhackAGregConstants.SQUARE_Y_AMOUNT));
            screenDumpTime = screenDumpTime + (System.nanoTime() - screenDumpStart);

            for (int y = 0; y < WhackAGregConstants.SQUARE_Y_AMOUNT; y++) {
                int x1 = column * WhackAGregConstants.SQUARE_WIDTH; //antiIdleRect.x +
                int y1 = y * WhackAGregConstants.SQUARE_HEIGHT; //antiIdleRect.y +

                long predictStart = System.nanoTime();
                BufferedImage square = capture.getSubimage(0, y1, WhackAGregConstants.SQUARE_WIDTH, WhackAGregConstants.SQUARE_HEIGHT);
                try {
                    int prediction = predict(model, loader, square);

                    predictionTime = predictionTime + (System.nanoTime() - predictStart);

                    if (prediction == 6) { //are we in menu?

                        System.out.println("Menu detected");
                        addObjectToStopLoop.add(Boolean.TRUE);
                    }

                    if (GregClassifier.stuffToClick.contains(prediction)) {
                        System.out.println("GOOD PREDICT");

                        Rectangle gregRect = new Rectangle(
                                antiIdleRect.x + x1,
                                antiIdleRect.y + y1,
                                WhackAGregConstants.SQUARE_WIDTH, WhackAGregConstants.SQUARE_HEIGHT);
                        if (lastClickedMap.get(gregRect) == null || lastClickedMap.get(gregRect) < System.currentTimeMillis() - timeSinceLastClickMs) {
                            game.clickWithinGame(WhackAGregConstants.RELATIVE_TOP_LEFT_X + x1 + WhackAGregConstants.SQUARE_WIDTH / 2, WhackAGregConstants.RELATIVE_TOP_LEFT_Y + y1 + WhackAGregConstants.SQUARE_HEIGHT / 2);
                            lastClickedMap.put(gregRect, System.currentTimeMillis());
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            totalTime = totalTime + (System.nanoTime() - totalStart);
        }

        System.out.println("Column: " + column + ", total ms: " + (totalTime / 1_000_000) / loopCount + ", screenDump: " + (screenDumpTime / 1_000_000) / loopCount + ", predict: " + (predictionTime / 1_000_000) / loopCount);

/*        {
            game.waitMs(2000);
            game.clickWithinGameWithUiUpdateDelay(260, 395);
        }*/
    }

    private MultiLayerNetwork loadModelFromDisk() {
        try {
            return ModelSerializer.restoreMultiLayerNetwork("greg.model");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
