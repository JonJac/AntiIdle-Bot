package idlebot.arcade.whackagreg;

import idlebot.Game;
import idlebot.XyTriple;
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

import static idlebot.arcade.whackagreg.WhackAGregConstants.*;
import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;
import static java.lang.Thread.MAX_PRIORITY;
import static ml.ModelUtil.predict;

public class WhackAGregPlayerThreaded {
    private final int timeSinceLastClickMs = 100;
    private Game game;

    public WhackAGregPlayerThreaded(Game game) {
        this.game = game;
    }

    public void playWAG() {
        playWAG(Integer.MAX_VALUE);
    }

    public void playWAG(int limit) {
        Rectangle antiIdleRect = game.getAntiIdleRect();

        ScreenShotContainer screenShotContainer = new ScreenShotContainer();
        Thread screenThread = new Thread(() -> screenShotContainer.startScreenCapturing(new Rectangle(antiIdleRect.x + WhackAGregConstants.RELATIVE_TOP_LEFT_X, antiIdleRect.y + WhackAGregConstants.RELATIVE_TOP_LEFT_Y, WhackAGregConstants.SQUARE_WIDTH * WhackAGregConstants.SQUARE_X_AMOUNT, WhackAGregConstants.SQUARE_HEIGHT * WhackAGregConstants.SQUARE_Y_AMOUNT)));
        screenThread.setPriority(MAX_PRIORITY);
        screenThread.start();

        WhackAGregPlayerThreaded whackAGregPlayer = new WhackAGregPlayerThreaded(game);
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            final int column = i;
            threads.add(new Thread(() -> whackAGregPlayer.internalPlayAGameOfGreg(column, screenShotContainer, limit)));
        }
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //click back
        game.waitMs(2000);
        game.clickWithinGameWithUiUpdateDelay(260, 395);
    }

    public void internalPlayAGameOfGreg(int column, ScreenShotContainer screenShotContainer, int limit) {
        //MultiLayerNetwork model = loadModelFromDisk();
        NativeImageLoader loader = new NativeImageLoader(40, 40);

        {
            game.clickWithinGameWithUiUpdateDelay(BEGIN_GAME_BUTTON_X, BEGIN_GAME_BUTTON_Y);
            game.waitMs(3500);
        }

        Map<Integer, Long> lastClickedMap = new HashMap<>();
        BufferedImage blackAndWhite = new BufferedImage(WhackAGregConstants.SQUARE_WIDTH, WhackAGregConstants.SQUARE_HEIGHT, TYPE_BYTE_BINARY);
        int loopCount = 0;
        long totalTime = 0;

        while (screenShotContainer.isCapturing()) {
            long totalStart = System.nanoTime();

            for (int y = 0; y < WhackAGregConstants.SQUARE_Y_AMOUNT; y++) {
                int x1 = column * WhackAGregConstants.SQUARE_WIDTH; //antiIdleRect.x +
                int y1 = y * WhackAGregConstants.SQUARE_HEIGHT; //antiIdleRect.y +

                loopCount = loopCount + 1;

                BufferedImage square = screenShotContainer.gameScreenShot.getSubimage(x1, y1, WhackAGregConstants.SQUARE_WIDTH, WhackAGregConstants.SQUARE_HEIGHT);
                blackAndWhite.createGraphics().drawImage(square, 0, 0, null);

                //MenuCheck
                if (column == 0 && y == 5 && check(xyMenu, blackAndWhite)) {
                    System.out.println("menu detected in column 1");
                    screenShotContainer.stop();
                    break;
                }

                //The real implementation
                if (check(WhackAGregConstants.xyGoodGreg, blackAndWhite) || check(WhackAGregConstants.xyAwesome, blackAndWhite) || check(xyMultiplier, blackAndWhite)) {
                    if (lastClickedMap.get(y) == null || lastClickedMap.get(y) < System.currentTimeMillis() - timeSinceLastClickMs) {
                        game.clickWithinGame(WhackAGregConstants.RELATIVE_TOP_LEFT_X + x1 + WhackAGregConstants.SQUARE_WIDTH / 2, WhackAGregConstants.RELATIVE_TOP_LEFT_Y + y1 + WhackAGregConstants.SQUARE_HEIGHT / 2);
                        lastClickedMap.put(y, System.currentTimeMillis());
                        suicideIfAboveClickLimit(limit, screenShotContainer, x1, y1);
                    }
                }

                //oldPrediction(model, loader, lastClickedMap, blackAndWhite, y, x1, y1, square);
            }

            totalTime = totalTime + (System.nanoTime() - totalStart);
        }

        System.out.println("Column: " + column + ", ms/square: " + (totalTime / 1_000_000) / loopCount);
    }

    private void suicideIfAboveClickLimit(int limit, ScreenShotContainer screenShotContainer, int x1, int y1) {
        if (screenShotContainer.clickCount.incrementAndGet() > limit) {
            game.clickWithinGame(WhackAGregConstants.RELATIVE_TOP_LEFT_X + x1 + WhackAGregConstants.SQUARE_WIDTH / 2, WhackAGregConstants.RELATIVE_TOP_LEFT_Y + y1 + WhackAGregConstants.SQUARE_HEIGHT / 2);
            game.clickWithinGame(WhackAGregConstants.RELATIVE_TOP_LEFT_X + x1 + WhackAGregConstants.SQUARE_WIDTH / 2, WhackAGregConstants.RELATIVE_TOP_LEFT_Y + y1 + WhackAGregConstants.SQUARE_HEIGHT / 2);
            game.clickWithinGame(WhackAGregConstants.RELATIVE_TOP_LEFT_X + x1 + WhackAGregConstants.SQUARE_WIDTH / 2, WhackAGregConstants.RELATIVE_TOP_LEFT_Y + y1 + WhackAGregConstants.SQUARE_HEIGHT / 2);
            game.clickWithinGame(WhackAGregConstants.RELATIVE_TOP_LEFT_X + x1 + WhackAGregConstants.SQUARE_WIDTH / 2, WhackAGregConstants.RELATIVE_TOP_LEFT_Y + y1 + WhackAGregConstants.SQUARE_HEIGHT / 2);
            game.clickWithinGame(WhackAGregConstants.RELATIVE_TOP_LEFT_X + x1 + WhackAGregConstants.SQUARE_WIDTH / 2, WhackAGregConstants.RELATIVE_TOP_LEFT_Y + y1 + WhackAGregConstants.SQUARE_HEIGHT / 2);
        }
    }

    private void oldPrediction(MultiLayerNetwork model, NativeImageLoader loader, Map<Integer, Long> lastClickedMap, BufferedImage blackAndWhite, int y, int x1, int y1, BufferedImage square) {
        try {
            int prediction = predict(model, loader, square);

            if (prediction != 1 && prediction != 6) {
                if (prediction == 2 && !check(xyAwesome, blackAndWhite)) {
                    System.out.println("Missmatch, prediction was : " + prediction);
                    ImageIO.write(blackAndWhite, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));
                }
                if (prediction == 3 && !check(xyGoodGreg, blackAndWhite)) {
                    System.out.println("Missmatch, prediction was : " + prediction);
                    ImageIO.write(blackAndWhite, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));
                }
                if (prediction == 4 && !check(xyMultiplier, blackAndWhite)) {
                    System.out.println("Missmatch, prediction was : " + prediction);
                    ImageIO.write(blackAndWhite, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));
                } else if (prediction != 2 && check(xyAwesome, blackAndWhite)) {
                    System.out.println("I thought prediction " + prediction + " was a awesome face");
                } else if (prediction != 3 && check(xyGoodGreg, blackAndWhite)) {
                    System.out.println("I thought prediction " + prediction + " was a good greg");
                } else if (prediction != 4 && check(xyMultiplier, blackAndWhite)) {
                    System.out.println("I thought prediction " + prediction + " was a multiplier");
                }
            }

            if (GregClassifier.stuffToClick.contains(prediction)) {
                if (lastClickedMap.get(y) == null || lastClickedMap.get(y) < System.currentTimeMillis() - timeSinceLastClickMs) {
                    game.clickWithinGame(WhackAGregConstants.RELATIVE_TOP_LEFT_X + x1 + WhackAGregConstants.SQUARE_WIDTH / 2, WhackAGregConstants.RELATIVE_TOP_LEFT_Y + y1 + WhackAGregConstants.SQUARE_HEIGHT / 2);
                    lastClickedMap.put(y, System.currentTimeMillis());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean check(XyTriple[] xyGoodGreg, BufferedImage blackAndWhite) {
        boolean result = true;
        for (XyTriple triple : xyGoodGreg) {
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
