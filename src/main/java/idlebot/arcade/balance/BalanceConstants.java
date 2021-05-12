package idlebot.arcade.balance;

import idlebot.XyTriple;
import idlebot.arcade.whackagreg.WhackAGregConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static idlebot.arcade.balance.BalanceSquare.*;

public class BalanceConstants {
    public static final int NEXT_SQUARE_X = 99;
    public static final int NEXT_SQUARE_Y = 220;

    public static final int LANE_1_X = 206;
    public static final int LANE_2_X = 256;
    public static final int LANE_3_X = 306;

    public static final int LANE_WINDOW_Y = 120;
    public static final int LANE_WINDOW_HEIGHT = 310;

    public static final int START_BUTTON_X = 67;
    public static final int START_BUTTON_Y = 370;

    public static final List<Integer> RED_SQUARE_COLOR_NEXT = List.of(0xFFFF6D60, 0xFFFF6666, 0xFFFE6463);
    public static final List<Integer> GREEN_SQUARE_COLOR_NEXT = List.of(0xFF6DFF60, 0xFF66FF60, 0xFF66FF66, 0xFF67FA63);
    public static final List<Integer> YELLOW_SQUARE_COLOR_NEXT = List.of(0xFFFFFF60, 0xFFFFFF66, 0xFFFFFA63);
    public static final List<Integer> UNKNOWN_SQUARE_COLOR_NEXT = List.of(0xFF8A8A7D, 0xFF848484, 0xFF848180);
    public static final List<Integer> PURPLE_SQUARE_COLOR_NEXT = List.of(0xFFA76DF2, 0xFFA366FF, 0xFFA364F8);
    public static final List<Integer> MENU_COLOR_NEXT = List.of(0xFF3D3D30);

    //-220392
    public static final List<Integer> RED_SQUARE_COLOR_DETECT = List.of(0xFFFF4A3D, 0xFFFF493C, 0xFFFF3E31, 0xFFFF3D30, 0xFFFF2518, 0xFFF57F7F, 0xFFF03737,
            0xFFFF1A1A, 0xFFF98484, 0xFFF53B3B, 0xFFF58578, 0xFFF04033, 0xFFF98B7E, 0xFFF54538, 0xFFF98585, 0xFFF53C3C);
    public static final List<Integer> GREEN_SQUARE_COLOR_DETECT = List.of(0xFF7CFF6F, 0xFF44FF37, 0xFF1AFF1A, 0xFF85F578, 0xFF41F034, 0xFF45F538, 0xFF8BF97E, 0xFF85F985, 0xFF3CF53C, 0xFF7FF57F, 0xFF37F037, 0xFFF9F984);
    public static final List<Integer> YELLOW_SQUARE_COLOR_DETECT = List.of(0xFFFFFF3D, 0xFFFFFF18, 0xFFFFFF1A, 0xFFF9F97D, 0xFFF5F538, 0xFFF5F578, 0xFFF0F034, 0xFFF5F57F, 0xFFF0F037, 0xFFF5F53B);
    public static final List<Integer> PURPLE_SQUARE_COLOR_DETECT = List.of(0xFFAC75FF); //Unknown cannot be purple, algorithm should consider.
    public static final List<Integer> MENU_COLOR_DETECT = List.of(0xFF3D3D30);
    public static final List<Integer> EMPTY_COLOR_DETECT = List.of(0xFFFFFFFF, 0xFFFFFFF2);

    public static final int maxSquaresInLanes = 8;

    public static final Map<Integer, BalanceSquare> rgbDetectMap;

    static {
        rgbDetectMap = new HashMap<>();
        RED_SQUARE_COLOR_DETECT.forEach(integer -> rgbDetectMap.put(integer, Red));
        GREEN_SQUARE_COLOR_DETECT.forEach(integer -> rgbDetectMap.put(integer, Green));
        YELLOW_SQUARE_COLOR_DETECT.forEach(integer -> rgbDetectMap.put(integer, Yellow));
        PURPLE_SQUARE_COLOR_DETECT.forEach(integer -> rgbDetectMap.put(integer, Purple));
        MENU_COLOR_DETECT.forEach(integer -> rgbDetectMap.put(integer, MenuAfterEndedGame));

        for (int i = 0xFFFFFFDD; i < 0xFFFFFFFF; i++) {
            rgbDetectMap.put(i, NoBlock);
        }
        EMPTY_COLOR_DETECT.forEach(integer -> rgbDetectMap.put(integer, NoBlock));
    }


    public static final Map<Integer, BalanceSquare> rgbNextMap;

    static {
        rgbNextMap = new HashMap<>();
        RED_SQUARE_COLOR_NEXT.forEach(integer -> rgbNextMap.put(integer, Red));
        GREEN_SQUARE_COLOR_NEXT.forEach(integer -> rgbNextMap.put(integer, Green));
        YELLOW_SQUARE_COLOR_NEXT.forEach(integer -> rgbNextMap.put(integer, Yellow));
        UNKNOWN_SQUARE_COLOR_NEXT.forEach(integer -> rgbNextMap.put(integer, Unknown));
        PURPLE_SQUARE_COLOR_NEXT.forEach(integer -> rgbNextMap.put(integer, Purple));
        MENU_COLOR_NEXT.forEach(integer -> rgbNextMap.put(integer, MenuAfterEndedGame));
    }


    static final Map<Integer, Integer> laneMap;

    static {
        laneMap = new HashMap<>();
        laneMap.put(LANE_1_X, 0);
        laneMap.put(LANE_2_X, 1);
        laneMap.put(LANE_3_X, 2);
    }

    static int black = -16777216;
    static int white = -1;

    static XyTriple[] xyNextYellow = new XyTriple[]{
            new XyTriple(4, 5, white),
            new XyTriple(4, 29, white),
            new XyTriple(29, 4, white),
            new XyTriple(29, 29, white),
            new XyTriple(17, 19, white),
            new XyTriple(33, 21, black),
    };

    static XyTriple[] xyNextUnknown = new XyTriple[]{
            new XyTriple(11, 10, white),
            new XyTriple(11, 23, white),
            new XyTriple(23, 20, white),
            new XyTriple(7, 8, black),
            new XyTriple(25, 12, black),
            new XyTriple(15, 26, black),
    };

    static XyTriple[] xyNextRed = new XyTriple[]{
            new XyTriple(5, 6, black),
            new XyTriple(29, 6, black),
            new XyTriple(4, 27, black),
            new XyTriple(17, 19, white),
            new XyTriple(8, 14, white),
            new XyTriple(25, 16, white),
    };

    static XyTriple[] xyNextPurple = new XyTriple[]{
            new XyTriple(5, 6, white),
            new XyTriple(28, 6, white),
            new XyTriple(11, 26, black),
            new XyTriple(17, 19, white),
    };

    static XyTriple[] xyNextGreen = new XyTriple[]{
            new XyTriple(12, 8, black),
            new XyTriple(28, 30, white),
            new XyTriple(7, 28, white),
            new XyTriple(17, 19, white),
    };
}
