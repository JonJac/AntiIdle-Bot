package idlebot.arcade.whackagreg;

public class WhackAGregConstants {
    public static int RELATIVE_TOP_LEFT_X = 29;
    public static int RELATIVE_TOP_LEFT_Y = 171;

    public static int SQUARE_HEIGHT = 40;
    public static int SQUARE_WIDTH = 40;

    public static int SQUARE_X_AMOUNT = 12;
    public static int SQUARE_Y_AMOUNT = 6;

    public static int BEGIN_GAME_BUTTON_X = 70;
    public static int BEGIN_GAME_BUTTON_Y = 375;

    static int black = -16777216;
    static int white = -1;

    static XyTriple[] xyGoodGreg = new XyTriple[]{
            new XyTriple(21, 23, white),
            new XyTriple(14, 11, white),
            new XyTriple(16, 35, white),
            new XyTriple(37, 37, white),
            new XyTriple(21, 26, white),
            new XyTriple(20, 22, black)
    };

    static XyTriple[] xyAwesome = new XyTriple[]{
            new XyTriple(28, 13, black),
            new XyTriple(10, 17, white),
            new XyTriple(6, 15, white),
            new XyTriple(17, 34, white),
            new XyTriple(35, 22, white),
            new XyTriple(21, 39, black),
            new XyTriple(23, 13, white),
            new XyTriple(25, 33, white),
            new XyTriple(35, 27, white),
    };

    static XyTriple[] xyMultiplier = new XyTriple[]{
            new XyTriple(22, 22, white),
            new XyTriple(25, 24, white),
            new XyTriple(25, 20, white),
            new XyTriple(27, 17, black),
            new XyTriple(26, 26, black),
            new XyTriple(13, 26, black),
            new XyTriple(9, 17, black),
    };

/*    static XyTriple[] xyMultiplier = new XyTriple[]{
            new XyTriple(22, 22, white),
            new XyTriple(25, 24, white),
            new XyTriple(25, 20, white),
            new XyTriple(20, 25, white),
            new XyTriple(23, 21, black),
            new XyTriple(23, 24, black),
            new XyTriple(20, 24, black),
            new XyTriple(26, 21, black),
    };*/

    static XyTriple[] xyMenu = new XyTriple[]{
            new XyTriple(19, 2, white),
            new XyTriple(19, 4, black),
            new XyTriple(19, 5, white),
    };

    static class XyTriple {
        int x, y, color;

        public XyTriple(int x, int y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        @Override
        public String toString() {
            return "XyTriple{" +
                    "x=" + x +
                    ", y=" + y +
                    ", color=" + color +
                    '}';
        }
    }
}
