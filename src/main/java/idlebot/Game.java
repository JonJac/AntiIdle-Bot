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

    public Game(Rectangle antiIdleRect) throws AWTException {
        this.antiIdleRect = antiIdleRect;
        this.robot = new Robot();
    }

    public void clickWithinGame(int x, int y) {
        if(x < 9 || y < 33){
            System.out.println("it was attempted to click outside of game. x=" + x +", y=" + y);
            return;
        }
        synchronized (lock) {
            robot.mouseMove(antiIdleRect.x + x, antiIdleRect.y +y);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }

    public void holdShiftDown(){
        robot.keyPress(KeyEvent.VK_SHIFT);
    }

    public void releaseShift(){
        robot.keyRelease(KeyEvent.VK_SHIFT);
    }

    public void moveMouseWithinGame(int x, int y) {
        if(x < 9 || y < 33){
            System.out.println("it was attempted to click outside of game. x=" + x +", y=" + y);
            return;
        }
        synchronized (lock) {
            robot.mouseMove(antiIdleRect.x + x, antiIdleRect.y +y);
        }
    }

    public BufferedImage screenShot(Rectangle rectangle) throws IOException {
        return robot.createScreenCapture(new Rectangle(antiIdleRect.x + rectangle.x, antiIdleRect.y + rectangle.y,rectangle.width,rectangle.height));
    }

    public void screenshotGame(String filename) throws IOException {
        BufferedImage image = robot.createScreenCapture(antiIdleRect);
        ImageIO.write(image, "bmp", new File(filename));
    }
}
