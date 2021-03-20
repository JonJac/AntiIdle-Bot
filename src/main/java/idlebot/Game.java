package idlebot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Game {
    private Rectangle antiIdleRect;
    private final Object lock = new Object();
    private Robot robot;

    public Game(Rectangle antiIdleRect) throws AWTException {
        this.antiIdleRect = antiIdleRect;
        this.robot = new Robot();
    }

    public void clickWithinGame(int x, int y) {
        synchronized (lock) {
            robot.mouseMove(antiIdleRect.x + x, antiIdleRect.y +y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }

    public void screenshotGame(String filename) throws IOException {
        BufferedImage image = robot.createScreenCapture(antiIdleRect);
        ImageIO.write(image, "bmp", new File(filename));
    }
}
