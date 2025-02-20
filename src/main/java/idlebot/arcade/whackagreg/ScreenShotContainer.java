package idlebot.arcade.whackagreg;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

public class ScreenShotContainer {
    BufferedImage gameScreenShot;
    private boolean run;
    private Robot robot;
    public AtomicInteger clickCount = new AtomicInteger();

    public ScreenShotContainer() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void startScreenCapturing(Rectangle rectangle) {
        run = true;
        int count = 0;
        long time = System.nanoTime();
        while (run) {
            count++;
            gameScreenShot = robot.createScreenCapture(rectangle);
        }
        System.out.println("Screen shot FPS: " + 1000.0 / (((System.nanoTime() - time) / 1_000_000) / count));
    }

    public void stop() {
        run = false;
    }

    public boolean isCapturing() {
        return run;
    }
}
