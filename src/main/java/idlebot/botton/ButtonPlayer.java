package idlebot.botton;

import idlebot.Game;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static idlebot.botton.ButtonConstants.*;

public class ButtonPlayer {
    static final double TOL = 0.0000001;
    static final int maxPerfectsInARow = 6;
    private static int clickRegistryFactor = 10; //not all clicks are registered by the flash programme


    private Game game;

    public ButtonPlayer(Game game) {
        this.game = game;
    }

    public void play() throws IOException {
        shopMultiplersAndRewards();
        clickTimes(2000);
    }

    private void clickTimes(int clicks) throws IOException {
        Point[] lastClicks = new Point[20];
        BufferedImage buttonScreenshot;
        int potentialPerfectsInARow = 0;

        for (int k = 0; k < clicks * clickRegistryFactor; k++) { //Game does not r
            buttonScreenshot = game.screenShot(CIRCLE_AREA);
            Point point = getCircleCenter(buttonScreenshot);
            if (legalPoint(point)) {//not sure why +2 :P Maybe rounding
                if (potentialPerfectsInARow >= maxPerfectsInARow - 1) {
                    game.clickWithinGame(point.x - 2, point.y + 2); //avoid perfect click
                    potentialPerfectsInARow = 0;
                } else if (goingLeft(lastClicks)) {
                    game.clickWithinGame(point.x - 1, point.y + 1);
                } else {
                    game.clickWithinGame(point.x + 2, point.y + 1);
                }

                System.arraycopy(lastClicks, 0, lastClicks, 1, lastClicks.length - 1);

                potentialPerfectsInARow++;
                lastClicks[0] = point;

                if (allElementsAreTheSame(lastClicks)) {
                    System.out.println("button broken: " + Arrays.toString(lastClicks));
                }
            } else {
                System.out.println("false point: " + point);
            }
        }
    }

    private void shopMultiplersAndRewards() {
        clickShop();
        game.waitMs(20);
        //click2xReward();
        game.waitMs(20);
        for (int i = 0; i < 10; i++) {
            clickMultiplier();
            game.waitMs(20);
        }
        clickShop();
        game.waitMs(20);
    }

    private void clickMultiplier() {
        game.clickWithinGame(SHOP_MULTIPLIER_X, SHOP_MULTIPLIER_Y);
    }

    private void click2xReward() {
        game.clickWithinGame(SHOP_2X_X, SHOP_2X_Y);
    }

    private void clickShop() {
        game.clickWithinGame(SHOP_X, SHOP_Y);
    }

    private boolean legalPoint(Point point) {
        return (point.x > CIRCLE_AREA_X && point.x <= CIRCLE_AREA_X + CIRCLE_AREA_WIDTH && point.y > CIRCLE_AREA_Y && point.y <= CIRCLE_AREA_Y + CIRCLE_AREA_HEIGHT + 2)
                || (point.x == 98 && point.y == 451);
    }

    private boolean allElementsAreTheSame(Point[] lastClicks) {
        Point lastClick = lastClicks[0];
        for (int i = 1; i < lastClicks.length - 1; i++) {
            if (!lastClick.equals(lastClicks[i])) {
                return false;
            }
        }

        return true;
    }

    private boolean goingLeft(Point[] lastClicks) {
        if (lastClicks[lastClicks.length - 1] == null) {
            return false;
        }
        return lastClicks[0].x < lastClicks[lastClicks.length - 1].x;
    }

    private Point getCircleCenter(BufferedImage buttonScreenshot) throws IOException {
        ArrayList<Point> pointList = new ArrayList<>();

        int color;
        int bottom = buttonScreenshot.getHeight() - 1;

        for (int i = 0; i < buttonScreenshot.getWidth(); i++) {
            color = buttonScreenshot.getRGB(i, bottom);
            if (color == 0xFF990000 || color == 0xFF000099) { //red circle border
                pointList.add(new Point(CIRCLE_AREA.x + i, CIRCLE_AREA.y + bottom));
                break;
            }
        }

        for (int i = buttonScreenshot.getWidth() - 1; i > 0; i--) {
            color = buttonScreenshot.getRGB(i, bottom);
            if (color == 0xFF990000 || color == 0xFF000099) { //red circle border
                pointList.add(new Point(CIRCLE_AREA.x + i, CIRCLE_AREA.y + bottom));
                break;
            }
        }

        outer:
        for (int y = 0; y < buttonScreenshot.getHeight(); y++) {
            for (int x = 0; x < buttonScreenshot.getWidth(); x++) {
                color = buttonScreenshot.getRGB(x, y);
                if (color == 0xFF990000 || color == 0xFF000099) { //red circle border
                    pointList.add(new Point(CIRCLE_AREA.x + x, CIRCLE_AREA.y + y));
                    break outer;
                }
            }
        }


        if (pointList.size() < 3) {
            System.out.println("Oh shit");
            //ImageIO.write(buttonScreenshot, "bmp", new File("images/buttonBug" + UUID.randomUUID() + ".bmp"));
            return new Point(10, 34); //click top left corner
            //throw new RuntimeException("No circle found");
        }

        if (pointList.get(1).x - pointList.get(0).x > CIRCLE_RADIUS * 2 + 5) {
            return new Point(98, 451); //repair
        }

        try {
            Point point = circleFromPoints(pointList.get(0), pointList.get(1), pointList.get(2));
            return point;
        } catch (IllegalArgumentException e) {
            //ImageIO.write(buttonScreenshot, "bmp", new File("images/buttonBug" + UUID.randomUUID() + ".bmp"));
            System.out.println(pointList);
            return new Point(10, 34); //click top left corner
        }
    }

    public static Point circleFromPoints(final Point p1, final Point p2, final Point p3) {
        final double offset = Math.pow(p2.x, 2) + Math.pow(p2.y, 2);
        final double bc = (Math.pow(p1.x, 2) + Math.pow(p1.y, 2) - offset) / 2.0;
        final double cd = (offset - Math.pow(p3.x, 2) - Math.pow(p3.y, 2)) / 2.0;
        final double det = (p1.x - p2.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p2.y);

        if (Math.abs(det) < TOL) {
            throw new IllegalArgumentException("Yeah, lazy.");
        }

        final double idet = 1 / det;

        final double centerx = (bc * (p2.y - p3.y) - cd * (p1.y - p2.y)) * idet;
        final double centery = (cd * (p1.x - p2.x) - bc * (p2.x - p3.x)) * idet;

        return new Point((int) centerx, (int) centery);
    }

}
