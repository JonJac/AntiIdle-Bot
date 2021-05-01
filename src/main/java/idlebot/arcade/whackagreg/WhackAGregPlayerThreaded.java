package idlebot.arcade.whackagreg;

import idlebot.Game;
import ml.GregClassifier;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static idlebot.arcade.whackagreg.WhackAGregConstants.*;
import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;
import static ml.ModelUtil.predict;

public class WhackAGregPlayerThreaded {
    private Robot robot;
    private Map<Rectangle, Long> lastClickedMap = new HashMap<>();
    private final int timeSinceLastClickMs = 70;
    private Game game;

    public WhackAGregPlayerThreaded(Game game) {
        this.game = game;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void playAGameOfGreg(Rectangle antiIdleRect, int column, ScreenShotContainer screenShotContainer) {
        MultiLayerNetwork model = loadModelFromDisk();

        {
            game.clickWithinGameWithUiUpdateDelay(BEGIN_GAME_BUTTON_X, BEGIN_GAME_BUTTON_Y);
            game.waitMs(3500);
        }

        int loopCount = 0;
        long totalTime = 0;

        NativeImageLoader loader = new NativeImageLoader(40, 40);

        final List<Object> addObjectToStopLoop = new ArrayList<>();
        while (addObjectToStopLoop.size() < 5) {
            long totalStart = System.nanoTime();

            for (int y = 0; y < WhackAGregConstants.SQUARE_Y_AMOUNT; y++) {
                int x1 = column * WhackAGregConstants.SQUARE_WIDTH; //antiIdleRect.x +
                int y1 = y * WhackAGregConstants.SQUARE_HEIGHT; //antiIdleRect.y +

                loopCount = loopCount + 1;

                BufferedImage square = screenShotContainer.gameScreenShot.getSubimage(x1, y1, WhackAGregConstants.SQUARE_WIDTH, WhackAGregConstants.SQUARE_HEIGHT);

                BufferedImage blackAndWhite = new BufferedImage(square.getWidth(), square.getHeight(), TYPE_BYTE_BINARY);
                blackAndWhite.createGraphics().drawImage(square, 0, 0, null);

                if (check(WhackAGregConstants.xyGoodGreg, blackAndWhite) || check(WhackAGregConstants.xyAwesome, blackAndWhite) || check(xyMultiplier, blackAndWhite)) {
                    Rectangle gregRect = new Rectangle(
                            antiIdleRect.x + x1,
                            antiIdleRect.y + y1,
                            WhackAGregConstants.SQUARE_WIDTH, WhackAGregConstants.SQUARE_HEIGHT);
                    if (lastClickedMap.get(gregRect) == null || lastClickedMap.get(gregRect) < System.currentTimeMillis() - timeSinceLastClickMs) {
                        game.clickWithinGame(WhackAGregConstants.RELATIVE_TOP_LEFT_X + x1 + WhackAGregConstants.SQUARE_WIDTH / 2, WhackAGregConstants.RELATIVE_TOP_LEFT_Y + y1 + WhackAGregConstants.SQUARE_HEIGHT / 2);
                        lastClickedMap.put(gregRect, System.currentTimeMillis());
                    }
                }

/*
                try {
                    int prediction = predict(model, loader, square);

                    if(prediction != 1 && prediction != 6){

                        if(prediction == 4 && !check(WhackAGregConstants.xyMultiplier, blackAndWhite) ){
                            System.out.println("Missmatch, prediction was : " + prediction);
                            ImageIO.write(blackAndWhite, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));
                        } else if(prediction != 4 && check(WhackAGregConstants.xyMultiplier, blackAndWhite)){
                            System.out.println("I thought prediction " + prediction + " was a multiplier");
                        }

                        String prefix = "-";
                        switch (prediction){
                            case 0:
                                prefix = "BadG";
                                break;
                            case 2:
                                prefix = "goodAwe";
                                break;
                            case 3:
                                prefix = "goodG";
                                break;
                            case 4:
                                prefix = "multiplier";
                                break;
                            case 5:
                                prefix = "coin";
                                break;
                            case 7:
                                prefix = "badAwe";
                        }
                        //ImageIO.write(blackAndWhite, "bmp", new File("images/" + prefix + "-" + UUID.randomUUID() + ".bmp"));
                    }

                    if (prediction == 6) { //are we in menu?
                        System.out.println("Menu detected");
                        addObjectToStopLoop.add(Boolean.TRUE);
                    }

                    if (GregClassifier.stuffToClick.contains(prediction)) {
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
*/
            }


            totalTime = totalTime + (System.nanoTime() - totalStart);
        }

        System.out.println("Column: " + column + ", ms/square: " + (totalTime / 1_000_000) / loopCount);

/*        {
            game.waitMs(2000);
            game.clickWithinGameWithUiUpdateDelay(260, 395);
        }*/
    }

    private boolean check(WhackAGregConstants.XyTriple[] xyGoodGreg, BufferedImage blackAndWhite) {
        boolean result = true;
        for (WhackAGregConstants.XyTriple triple : xyGoodGreg) {
            if (blackAndWhite.getRGB(triple.x - 1, triple.y - 1) != triple.color) {
                result = false;
                //System.out.println(triple + ", was " + blackAndWhite.getRGB(triple.x - 1,triple.y - 1));
            }
        }

        return result;
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
