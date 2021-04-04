package idlebot.arcade.avoidance;

import idlebot.Game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import static idlebot.arcade.avoidance.AvoidanceConstants.searchGridDistX;
import static idlebot.arcade.avoidance.AvoidanceConstants.searchGridDistY;
import static idlebot.arcade.whackagreg.WhackAGregConstants.BEGIN_GAME_BUTTON_X;
import static idlebot.arcade.whackagreg.WhackAGregConstants.BEGIN_GAME_BUTTON_Y;

public class AvoidancePlayer {
    private Game game;
    private Random rng = new Random();
    private int anInt = AvoidanceConstants.gameWindow.width - AvoidanceConstants.padding * 2;
    private LinkedBlockingQueue<PointOrder> pointOrders = new LinkedBlockingQueue<>();

    public AvoidancePlayer(Game game) {
        this.game = game;
    }

    public void play() {
        int lastX = AvoidanceConstants.padding;
        int lastY = AvoidanceConstants.padding;

        game.clickWithinGame(BEGIN_GAME_BUTTON_X, BEGIN_GAME_BUTTON_Y);
        game.waitMs(3000);

        int count = -1;
        BufferedImage bufferedImage = null;
        outer:
        while (bufferedImage == null || backButtonVisible(bufferedImage)) {
            count++;
            game.waitMs(10);
            bufferedImage = game.screenShot(AvoidanceConstants.gameWindow);
            //writeImage(count, bufferedImage);

            SafePoint currentSafePoint = isSafe(lastX, lastY, bufferedImage);
            if (currentSafePoint.isSafe()) {
                if (bufferedImage.getRGB(lastX, lastY) != AvoidanceConstants.backgroundColor) {
                    //System.out.println("Saw color: " + String.format("0x%08X", bufferedImage.getRGB(lastX,lastY)));
                }
                continue;
            }

            SafePoint bestPointSoFar = currentSafePoint;

            for (int y = AvoidanceConstants.padding; y < AvoidanceConstants.gameWindow.height - AvoidanceConstants.padding * 2; y = y + searchGridDistY) {
                for (int x = AvoidanceConstants.padding; x < anInt; x = x + searchGridDistX) {
                    //System.out.println("checking: " + x + "," +y);
                    currentSafePoint = isSafe(x, y, bufferedImage);
                    if (currentSafePoint.isSafe()) {
                        moveMouseWithinGameArea(x, y);
                        lastX = x;
                        lastY = y;

                        continue outer;
                    } else {
                        if (currentSafePoint.clear > bestPointSoFar.clear) {
                            bestPointSoFar = currentSafePoint;
                        }
                    }
                }
            }

/*
            for (int i = 0; i < 20; i++){
                int potentialX = rng.nextInt(AvoidanceConstants.gameWindow.width - AvoidanceConstants.padding*2) + AvoidanceConstants.padding;
                int potentialY = rng.nextInt(AvoidanceConstants.gameWindow.height - AvoidanceConstants.padding*2) + AvoidanceConstants.padding;
                if(isSafe(potentialX, potentialY, bufferedImage)){
                    moveMouseWithinGameArea(potentialX,potentialY);
                    lastX = potentialX;
                    lastY = potentialY;

                    if(bufferedImage.getRGB(lastX,lastY) != AvoidanceConstants.backgroundColor){
                        System.out.println("Saw color: " + String.format("0x%08X", bufferedImage.getRGB(lastX,lastY)));
                    }
                    continue outer;
                }
            }
*/

            moveMouseWithinGameArea(currentSafePoint.x, currentSafePoint.y);
            System.out.println("Could not find completely clear spot. Too point: " + currentSafePoint);
            if (currentSafePoint.clear < 10) {
                game.pressCtrl();
            }
        }
    }

    public void playParallel() {
        int lastX = AvoidanceConstants.padding;
        int lastY = AvoidanceConstants.padding;

        game.clickWithinGame(BEGIN_GAME_BUTTON_X, BEGIN_GAME_BUTTON_Y);
        game.waitMs(3000);

        int count = 0;

        BufferedImage bufferedImage = null;
        outer:
        while (bufferedImage == null || backButtonVisible(bufferedImage)) {
            count++;
            bufferedImage = game.screenShot(AvoidanceConstants.gameWindow);

            SafePoint currentSafePoint = isSafe(lastX, lastY, bufferedImage);
            if (currentSafePoint.isSafe()) {
                continue;
            }

            SafePoint bestPointSoFar = currentSafePoint;

            for (int y = AvoidanceConstants.padding; y < AvoidanceConstants.gameWindow.height - AvoidanceConstants.padding * 2; y = y + searchGridDistY) {
                for (int x = AvoidanceConstants.padding; x < anInt; x = x + searchGridDistX) {

                    pointOrders.put(new PointOrder(x, y, bufferedImage));
                    currentSafePoint = isSafe(x, y, bufferedImage);
                    if (currentSafePoint.isSafe()) {
                        moveMouseWithinGameArea(x, y);
                        lastX = x;
                        lastY = y;

                        continue outer;
                    } else {
                        if (currentSafePoint.clear > bestPointSoFar.clear) {
                            bestPointSoFar = currentSafePoint;
                        }
                    }
                }
            }

            moveMouseWithinGameArea(currentSafePoint.x, currentSafePoint.y);
            System.out.println("Could not find completely clear spot. Too point: " + currentSafePoint);
            if (currentSafePoint.clear < 10) {
                game.pressCtrl();
            }
        }

    }

    private void writeImage(int count, BufferedImage bufferedImage) {
        try {
            ImageIO.write(bufferedImage, "bmp", new File("images/" + count + ".bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean backButtonVisible(BufferedImage bufferedImage) {
        return bufferedImage.getRGB(AvoidanceConstants.BACK_BUTTON_X - AvoidanceConstants.gameWindow.x, AvoidanceConstants.BACK_BUTTON_Y - AvoidanceConstants.gameWindow.y) != AvoidanceConstants.BACK_BUTTON_COLOR;
    }

    private class PointOrder {
        int x, y;
        BufferedImage image;

        public PointOrder(int x, int y, BufferedImage image) {
            this.x = x;
            this.y = y;
            this.image = image;
        }
    }

    private class SafePoint {
        int x, y, clear;
        boolean safe;
        String origen;

        public SafePoint(int x, int y, int clear, boolean safe, String origen) {
            this.x = x;
            this.y = y;
            this.clear = clear;
            this.safe = safe;
            this.origen = origen;
        }

        public SafePoint(int x, int y, int clear, boolean safe) {
            this.x = x;
            this.y = y;
            this.clear = clear;
            this.safe = safe;
        }

        public boolean isSafe() {
            return safe;
        }

        @Override
        public String toString() {
            return "SafePoint{" +
                    "x=" + x +
                    ", y=" + y +
                    ", clear=" + clear +
                    ", safe=" + safe +
                    ", origen='" + origen + '\'' +
                    '}';
        }
    }

    /**
     * Checks the following pattern. Length specified in constants.
     * #
     * #
     * #####
     * #
     * #
     */
    private SafePoint isSafe(int x, int y, BufferedImage bufferedImage) {
        //UUID uuid = UUID.randomUUID();
        //System.out.println(uuid  + "-original-x,y=" + x+ "," + y);

        for (int i = x - AvoidanceConstants.searchLength; i < x + AvoidanceConstants.searchLength; i++) {
            //System.out.println(uuid  + "-search-i,y=" + i+ "," + y);
            checkUnknownColor(i, y, bufferedImage);
            if (isWrongColor(bufferedImage, i, y)) {
                return new SafePoint(x, y, Math.abs(i - x), false, "search-X");
            }
        }
        for (int i = y - AvoidanceConstants.searchLength + 1; i < y + AvoidanceConstants.searchLength; i++) {
            //System.out.println(uuid  + "-search-x,i=" + x+ "," + i);
            checkUnknownColor(x, i, bufferedImage);
            if (isWrongColor(bufferedImage, x, i)) {
                return new SafePoint(x, y, Math.abs(i - y), false, "search-Y");
            }
        }

        //diagonal
        int diagSearch = AvoidanceConstants.searchLength;
        for (int i = 0; i < diagSearch; i++) {
            checkUnknownColor(x + i, y + i, bufferedImage);
            if (isWrongColor(bufferedImage, x + i, y + i)) { //down  right
                return new SafePoint(x, y, i, false, "search-down right");
            }

            checkUnknownColor(x - i, y - i, bufferedImage);
            if (isWrongColor(bufferedImage, x - i, y - i)) { //up left
                return new SafePoint(x, y, i, false, "search-up left");
            }

            checkUnknownColor(x - i, y + i, bufferedImage);
            if (isWrongColor(bufferedImage, x - i, y + i)) { //down left
                return new SafePoint(x, y, i, false, "search-down left");
            }

            checkUnknownColor(x + i, y - i, bufferedImage);
            if (isWrongColor(bufferedImage, x + i, y - i)) { //up right
                return new SafePoint(x, y, i, false, "search-up right");
            }
        }
        return new SafePoint(x, y, AvoidanceConstants.searchLength, true);
    }

    private boolean isWrongColor(BufferedImage bufferedImage, int x, int y) {
        return AvoidanceConstants.BAD_COLORS.contains(bufferedImage.getRGB(x, y));
    }

    private void checkUnknownColor(int x, int y, BufferedImage bufferedImage) {
/*        if(bufferedImage.getRGB(x,y) != AvoidanceConstants.backgroundColor &&
                !AvoidanceConstants.BAD_COLORS.contains(bufferedImage.getRGB(x,y)) //&&
                //!AvoidanceConstants.MULTIPLIER_COLORS.contains(bufferedImage.getRGB(x,y))
        ){

            //System.out.println("Saw color: " + String.format("0x%08X", bufferedImage.getRGB(x,y)));
        }*/
    }

    private void moveMouseWithinGameArea(int x, int y) {
        game.moveMouseWithinGame(
                AvoidanceConstants.gameWindow.x + x,
                AvoidanceConstants.gameWindow.y + y);
    }


}
