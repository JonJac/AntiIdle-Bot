package idlebot.arcade.avoidance;

import java.awt.*;
import java.util.Set;

public class AvoidanceConstants {
    static final Rectangle gameWindow = new Rectangle(17, 122, 495, 324);
    static final int searchLength = 30;
    static final int padding = 70;
    static final int searchGridDistY = (gameWindow.height - 2 * padding) / 4 - 2;
    static final int searchGridDistX = (gameWindow.width - 2 * padding) / 4 - 2;
    static final int dangerColor = 0xFFD81609;
    public static final java.util.Set<Integer> BAD_COLORS = Set.of(0xFFD81609, 0xFFB11F12, 0xFF50382B, 0xFF633326, 0xFF8A291C, 0xFF5A3528);
    static final int backgroundColor = 0xFF3D3D30;

    static final int BACK_BUTTON_X = 288;
    static final int BACK_BUTTON_Y = 376;
    static final int BACK_BUTTON_COLOR = 0xFFB6B6E7;

    public static final java.util.Set<Integer> MULTIPLIER_COLORS = Set.of(0xFF696925, 0xFF6D6C24, 0xFF463A2D, 0xFF777721);
}
