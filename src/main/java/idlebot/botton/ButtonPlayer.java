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
    private int pointEachWay = 20;

    public ButtonPlayer(Game game) {
        this.game = game;
    }

    public void play() throws IOException {
        BufferedImage buttonScreenshot = game.screenShot(CIRCLE_AREA);
        //ImageIO.write(buttonScreenshot, "bmp", new File("button.bmp"));

        for (int i = 0; i < 5000; i++) {
            buttonScreenshot = game.screenShot(CIRCLE_AREA);
            Point point = getCircleCenter(buttonScreenshot);
            if (point.x > CIRCLE_AREA_X && point.x < CIRCLE_AREA_X + CIRCLE_AREA_WIDTH && point.y > CIRCLE_AREA_Y && point.y < CIRCLE_AREA_Y + CIRCLE_AREA_HEIGHT) {
                game.clickWithinGame(point.x, point.y);
            } else {
                System.out.println("false point: " + point);
                //ImageIO.write(buttonScreenshot, "bmp", new File("images/buttonBug" + i + ".bmp"));
            }
        }

        //BufferedImage bufferedImage = replace(buttonScreenshot);
        //ImageIO.write(bufferedImage, "bmp", new File("button2.bmp"));
    }

    private Point getCircleCenter(BufferedImage buttonScreenshot) throws IOException {
        ArrayList<Point> pointList = new ArrayList<>();

        int color;
        outer:
        for (int i = 0; i < buttonScreenshot.getWidth(); i++) {
            for (int j = 0; j < buttonScreenshot.getHeight(); j++) {
                color = buttonScreenshot.getRGB(i, j);
                if (color == 0xFF990000) { //red circle border
                    pointList.add(new Point(CIRCLE_AREA.x + i, CIRCLE_AREA.y + j));
                    if (pointList.size() >= pointEachWay) {
                        break outer;
                    }
                }
            }
        }

        outer:
        for (int i = buttonScreenshot.getWidth() - 1; i > 0; i--) {
            for (int j = buttonScreenshot.getHeight() - 1; j > 0; j--) {
                color = buttonScreenshot.getRGB(i, j);
                if (color == 0xFF990000) { //red circle border
                    pointList.add(new Point(CIRCLE_AREA.x + i, CIRCLE_AREA.y + j));
                    if (pointList.size() >= pointEachWay) {
                        break outer;
                    }
                }
            }
        }

        //Does not work
        if (pointList.size() > 400) {
            //System.out.println("Button broke");
            throw new RuntimeException("Button broke");
        }

        if (pointList.size() < 3) {
            System.out.println("Oh shit");
            ImageIO.write(buttonScreenshot, "bmp", new File("images/buttonBug" + UUID.randomUUID() + ".bmp"));
            return new Point(10, 34); //click top left corner
            //throw new RuntimeException("Button broke");
        }

        ArrayList<Point> centerList = new ArrayList<>();
        while (true) {
            try {
                Collections.shuffle(pointList);
                Point point = circleFromPoints(pointList.get(0), pointList.get(1), pointList.get(2));
                centerList.add(point);
                if (centerList.size() >= 10) {
                    //System.out.println(centerList);
                    Map<Integer, Integer> xMap = new HashMap<>();
                    Map<Integer, Integer> yMap = new HashMap<>();
                    for (Point point1 : centerList) {
                        xMap.computeIfPresent(point1.x, (key, val) -> val + 1);
                        xMap.putIfAbsent(point1.x, 1);

                        yMap.computeIfPresent(point1.y, (key, val) -> val + 1);
                        yMap.putIfAbsent(point1.y, 1);
                    }

                    Integer bestX = maxUsingIteration(xMap);
                    Integer bestY = maxUsingIteration(yMap);

                    return new Point(bestX, bestY);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("unlucky points. Fix :P");
            }
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
