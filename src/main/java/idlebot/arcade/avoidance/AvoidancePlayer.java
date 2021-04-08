package idlebot.arcade.avoidance;

import idlebot.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static idlebot.arcade.avoidance.AvoidanceConstants.searchGridDistX;
import static idlebot.arcade.avoidance.AvoidanceConstants.searchGridDistY;
import static idlebot.arcade.whackagreg.WhackAGregConstants.BEGIN_GAME_BUTTON_X;
import static idlebot.arcade.whackagreg.WhackAGregConstants.BEGIN_GAME_BUTTON_Y;

public class AvoidancePlayer {
    private Game game;
    private int anInt = AvoidanceConstants.gameWindow.width - AvoidanceConstants.padding * 2;

    public AvoidancePlayer(Game game) {
        this.game = game;
    }

    public void play() {
        game.clickWithinGame(BEGIN_GAME_BUTTON_X, BEGIN_GAME_BUTTON_Y);
        game.waitMs(3000);

        internalPlay();


        game.waitMs(2000);
        game.clickWithinGame(260, 395);
        game.waitMs(100);

    }

    private void internalPlay() {
        int lastX = AvoidanceConstants.padding;
        int lastY = AvoidanceConstants.padding;


        int count = -1;
        BufferedImage bufferedImage = null;
        outer:
        while (bufferedImage == null || backButtonVisible(bufferedImage)) {
            count++;
            //System.out.println("Round " + count);
            //game.waitMs(10);
            bufferedImage = game.screenShot(AvoidanceConstants.gameWindow);
            //writeImage(count, bufferedImage);

            SafePoint currentSafePoint = isSafe(lastX, lastY, bufferedImage);
            if (currentSafePoint.isSafe()) {
                if (bufferedImage.getRGB(lastX, lastY) != AvoidanceConstants.backgroundColor) {
                    System.out.println("Saw color: " + String.format("0x%08X", bufferedImage.getRGB(lastX, lastY)));
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


            moveMouseWithinGameArea(bestPointSoFar.x, bestPointSoFar.y);
            System.out.println("Could not find completely clear spot. Too point: " + bestPointSoFar);
            writeImage(count, bufferedImage);
            if (bestPointSoFar.clear < 20) {
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
        int x, y, round;
        BufferedImage image;
        int pointCount;

        public PointOrder(int x, int y, int round, BufferedImage image, int pointCount) {
            this.x = x;
            this.y = y;
            this.round = round;
            this.image = image;
            this.pointCount = pointCount;
        }
    }

    private class SafePoint {
        int x, y, clear;
        boolean safe;
        String origen;
        int round = -1;

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
        int clear = Integer.MAX_VALUE;

        for (int i = x - AvoidanceConstants.searchLength; i < x + AvoidanceConstants.searchLength; i++) {
            //System.out.println(uuid  + "-search-i,y=" + i+ "," + y);
            checkUnknownColor(i, y, bufferedImage);
            if (isWrongColor(bufferedImage, i, y)) {
                int abs = Math.abs(i - x);
                if (abs < clear) {
                    clear = abs;
                }
                //return new SafePoint(x, y, Math.abs(i - x), false, "search-X");
            }
        }
        for (int i = y - AvoidanceConstants.searchLength + 1; i < y + AvoidanceConstants.searchLength; i++) {
            //System.out.println(uuid  + "-search-x,i=" + x+ "," + i);
            checkUnknownColor(x, i, bufferedImage);
            if (isWrongColor(bufferedImage, x, i)) {
                int abs = Math.abs(i - y);
                if (abs < clear) {
                    clear = abs;
                }
                //return new SafePoint(x, y, abs, false, "search-Y");
            }
        }

        //diagonal
        int diagSearch = AvoidanceConstants.searchLength;
        for (int i = 0; i < diagSearch; i++) {
            checkUnknownColor(x + i, y + i, bufferedImage);
            if (isWrongColor(bufferedImage, x + i, y + i)) { //down  right
                if (i < clear)
                    clear = i;
                //return new SafePoint(x, y, i, false, "search-down right");
            }

            checkUnknownColor(x - i, y - i, bufferedImage);
            if (isWrongColor(bufferedImage, x - i, y - i)) { //up left
                if (i < clear)
                    clear = i;
                //return new SafePoint(x, y, i, false, "search-up left");
            }

            checkUnknownColor(x - i, y + i, bufferedImage);
            if (isWrongColor(bufferedImage, x - i, y + i)) { //down left
                if (i < clear)
                    clear = i;
                //return new SafePoint(x, y, i, false, "search-down left");
            }

            checkUnknownColor(x + i, y - i, bufferedImage);
            if (isWrongColor(bufferedImage, x + i, y - i)) { //up right
                if (i < clear)
                    clear = i;
                //return new SafePoint(x, y, i, false, "search-up right");
            }
        }
        if (clear != Integer.MAX_VALUE) {
            //System.out.println("searched x,y=" + x + "," + y + " and found clear: " + clear);
            return new SafePoint(x, y, clear, false, "complete-search");
        }
        return new SafePoint(x, y, AvoidanceConstants.searchLength, true);
    }

    private boolean isWrongColor(BufferedImage bufferedImage, int x, int y) {
        //return bufferedImage.getRGB(x, y) == AvoidanceConstants.dangerColor;
        return AvoidanceConstants.BAD_COLORS.contains(bufferedImage.getRGB(x, y));
    }

    private void checkUnknownColor(int x, int y, BufferedImage bufferedImage) {
/*        if(bufferedImage.getRGB(x,y) != AvoidanceConstants.backgroundColor &&
                !AvoidanceConstants.BAD_COLORS.contains(bufferedImage.getRGB(x,y)) &&
                !AvoidanceConstants.MULTIPLIER_COLORS.contains(bufferedImage.getRGB(x,y))
        ){
            System.out.println("-Saw color: " + String.format("0x%08X", bufferedImage.getRGB(x,y)));
        }*/
    }

    private void moveMouseWithinGameArea(int x, int y) {
        //game.holdShiftDown();
        game.moveMouseWithinGame(
                AvoidanceConstants.gameWindow.x + x,
                AvoidanceConstants.gameWindow.y + y);
        //game.waitMs(1);
        //game.releaseShift();
    }


}
