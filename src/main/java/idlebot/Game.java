package idlebot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Game {
    private Rectangle antiIdleRect;
    private final Object lock = new Object();
    private Robot robot;
    private static final int keyPressDelayMs = 40;

    public Game(Rectangle antiIdleRect) throws AWTException {
        this.antiIdleRect = antiIdleRect;
        this.robot = new Robot();
    }

    public void clickWithinGame(Point p) {
        clickWithinGame(p.x, p.y);
    }

    public void clickWithinGame(int x, int y) {
        if (x < 9 || y < 33) {
            System.out.println("it was attempted to click outside of game. x=" + x + ", y=" + y);
            return;
        }
        synchronized (lock) {
            robot.mouseMove(antiIdleRect.x + x, antiIdleRect.y + y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }

    public void clickWithinGameWithUiUpdateDelay(int x, int y) {
        clickWithinGame(x, y);
        waitMs(60);
    }

    public void holdLeftArrowKey() {
        robot.keyPress(KeyEvent.VK_LEFT);
    }

    public void holdRightArrowKey() {
        robot.keyPress(KeyEvent.VK_RIGHT);
    }

    public void holdDownArrowKey() {
        robot.keyPress(KeyEvent.VK_DOWN);
    }

    public void releaseLeftArrowKey() {
        robot.keyRelease(KeyEvent.VK_LEFT);
    }

    public void releaseRightArrowKey() {
        robot.keyRelease(KeyEvent.VK_RIGHT);
    }

    public void releaseDownArrowKey() {
        robot.keyRelease(KeyEvent.VK_DOWN);
    }

    public void pressLeftArrowKey() {
        robot.keyPress(KeyEvent.VK_LEFT);
        waitMs(keyPressDelayMs);
        robot.keyRelease(KeyEvent.VK_LEFT);
    }

    public void pressRightArrowKey() {
        robot.keyPress(KeyEvent.VK_RIGHT);
        waitMs(keyPressDelayMs);
        robot.keyRelease(KeyEvent.VK_RIGHT);
    }

    public void pressDownArrowKey() {
        robot.keyPress(KeyEvent.VK_DOWN);
        waitMs(keyPressDelayMs);
        robot.keyRelease(KeyEvent.VK_DOWN);
    }

    public void pressSpace() {
        robot.keyPress(KeyEvent.VK_SPACE);
        waitMs(keyPressDelayMs);
        robot.keyRelease(KeyEvent.VK_SPACE);
    }

    public void pressCtrl() {
        robot.keyPress(KeyEvent.VK_CONTROL);
        waitMs(keyPressDelayMs);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public void pressKey(int event) {
        robot.keyPress(event);
        waitMs(keyPressDelayMs);
        robot.keyRelease(event);
    }

    public void holdKey(int event) {
        robot.keyPress(event);
    }

    public void releaseKey(int event) {
        robot.keyRelease(event);
    }

    public void holdShiftDown() {
        robot.keyPress(KeyEvent.VK_SHIFT);
    }

    public void releaseShift() {
        robot.keyRelease(KeyEvent.VK_SHIFT);
    }

    public void moveMouseWithinGame(int x, int y) {
        if (x < 9 || y < 33) {
            System.out.println("it was attempted to click outside of game. x=" + x + ", y=" + y);
            return;
        }
        synchronized (lock) {
            robot.mouseMove(antiIdleRect.x + x, antiIdleRect.y + y);
        }
    }

    public BufferedImage screenShot(int x, int y, int width, int height) {
        return screenShot(new Rectangle(x, y, width, height));
    }

    public BufferedImage screenShotFullGame() {
        return screenShot(new Rectangle(0, 0, antiIdleRect.width, antiIdleRect.height));
    }

    public BufferedImage screenShot(Rectangle rectangle) {
        return robot.createScreenCapture(new Rectangle(antiIdleRect.x + rectangle.x, antiIdleRect.y + rectangle.y, rectangle.width, rectangle.height));
    }

    public BufferedImage screenShot(Rectangle rectangle, Robot robot) {
        return robot.createScreenCapture(new Rectangle(antiIdleRect.x + rectangle.x, antiIdleRect.y + rectangle.y, rectangle.width, rectangle.height));
    }

    public void screenshotGame(String filename) throws IOException {
        BufferedImage image = robot.createScreenCapture(antiIdleRect);
        ImageIO.write(image, "bmp", new File(filename));
    }

    public void waitMs(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
