package idlebot.arcade.whackagreg;

import idlebot.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenShotContainer {
    BufferedImage gameScreenShot;
    private boolean run;

    public void startScreenCapturing(Game game, Rectangle rectangle) {
        run = true;
        while (run) {
            game.screenShot(rectangle);
        }
    }

    public void stop() {
        run = false;
    }
}
