package idlebot.botton;

import idlebot.Game;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static idlebot.botton.ButtonConstants.*;

public class ButtonPlayer {
    static final double TOL = 0.0000001;

    private Game game;

    public ButtonPlayer(Game game) {
        this.game = game;
    }

    public void play() throws IOException {
        BufferedImage buttonScreenshot = game.screenShot(CIRCLE_AREA);
        //ImageIO.write(buttonScreenshot, "bmp", new File("button.bmp"));
        Point[] lastClicks = new Point[10];

        for (int k = 0; k < 15000; k++) {
            buttonScreenshot = game.screenShot(CIRCLE_AREA);
            Point point = getCircleCenter(buttonScreenshot);
            if (point.x > CIRCLE_AREA_X && point.x <= CIRCLE_AREA_X + CIRCLE_AREA_WIDTH && point.y > CIRCLE_AREA_Y && point.y <= CIRCLE_AREA_Y + CIRCLE_AREA_HEIGHT + 2) {//not sure why +2 :P Maybe rounding

                if (goingLeft(lastClicks)) {
                    game.clickWithinGame(point.x - 1, point.y + 1);
                    //game.clickWithinGame(point.x - 1, point.y + 1);
                } else {
                    game.clickWithinGame(point.x + 2, point.y + 1);
                    //game.clickWithinGame(point.x + 2, point.y + 1);
                }

                for (int i = lastClicks.length - 1; i > 0; i--) {
                    Point lastClick = lastClicks[i - 1];
                    lastClicks[i] = lastClick;
                }

                lastClicks[0] = point;

                if (allElementsAreTheSame(lastClicks)) {
                    System.out.println("button broken: " + Arrays.toString(lastClicks));
                    break;
                }
            } else {
                if (point.x > CIRCLE_AREA_X && point.x <= CIRCLE_AREA_X + CIRCLE_AREA_WIDTH && point.y > CIRCLE_AREA_Y && point.y <= CIRCLE_AREA_Y + CIRCLE_AREA_HEIGHT + 3) {
                    System.out.println("false point: " + point);
                }
                System.out.println("false point: " + point);
                break;
                //ImageIO.write(buttonScreenshot, "bmp", new File("images/buttonBug" + i + ".bmp"));
            }
        }

        System.out.println("loop done");
    }

    private boolean allElementsAreTheSame(Point[] lastClicks) {
        Point lastClick = lastClicks[0];
        for (int i = 1; i < lastClicks.length - 1; i++){
            if(!lastClick.equals(lastClicks[i])){
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
            if (color == 0xFF990000) { //red circle border
                pointList.add(new Point(CIRCLE_AREA.x + i, CIRCLE_AREA.y + bottom));
                break;
            }
        }

        for (int i = buttonScreenshot.getWidth() - 1; i > 0; i--) {
            color = buttonScreenshot.getRGB(i, bottom);
            if (color == 0xFF990000) { //red circle border
                pointList.add(new Point(CIRCLE_AREA.x + i, CIRCLE_AREA.y + bottom));
                break;
            }
        }

        outer:
        for (int y = 0; y < buttonScreenshot.getHeight(); y++) {
            for (int x = 0; x < buttonScreenshot.getWidth(); x++) {
                color = buttonScreenshot.getRGB(x, y);
                if (color == 0xFF990000) { //red circle border
                    pointList.add(new Point(CIRCLE_AREA.x + x, CIRCLE_AREA.y + y));
                        break outer;
                }
            }
        }


        if (pointList.size() < 3) {
            System.out.println("Oh shit");
            ImageIO.write(buttonScreenshot, "bmp", new File("images/buttonBug" + UUID.randomUUID() + ".bmp"));
            return new Point(10, 34); //click top left corner
            //throw new RuntimeException("Button broke");
        }


        try {
            Point point = circleFromPoints(pointList.get(0), pointList.get(1), pointList.get(2));
            return point;
        } catch (IllegalArgumentException e) {
            ImageIO.write(buttonScreenshot, "bmp", new File("images/buttonBug" + UUID.randomUUID() + ".bmp"));
            System.out.println(pointList);
            return new Point(10, 34); //click top left corner
        }
    }

    public <K, V extends Comparable<V>> K maxUsingIteration(Map<K, V> map) {
        Map.Entry<K, V> maxEntry = null;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (maxEntry == null || entry.getValue()
                    .compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        return maxEntry.getKey();
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
