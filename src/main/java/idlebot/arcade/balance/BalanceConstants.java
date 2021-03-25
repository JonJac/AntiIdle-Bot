package idlebot.arcade.balance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static idlebot.arcade.balance.BalanceSquare.*;

public class BalanceConstants {
    public static final int NEXT_SQUARE_X = 99;
    public static final int NEXT_SQUARE_Y = 220;

    public static final int LANE_1_X = 209;
    public static final int LANE_2_X = 256;
    public static final int LANE_3_X = 306;

    public static final int LANE_WINDOW_Y = 120;
    public static final int LANE_WINDOW_HEIGHT = 310;

    public static final int START_BUTTON_X = 67;
    public static final int START_BUTTON_Y = 370;

    public static final List<Integer> RED_SQUARE_COLOR_NEXT = List.of(0xFFFF6D60, 0xFFFF6666);
    public static final List<Integer> GREEN_SQUARE_COLOR_NEXT = List.of(0xFF6DFF60, 0xFF66FF60, 0xFF66FF66);
    public static final List<Integer> YELLOW_SQUARE_COLOR_NEXT = List.of(0xFFFFFF60, 0xFFFFFF66);
    public static final List<Integer> UNKNOWN_SQUARE_COLOR_NEXT = List.of(0xFF8A8A7D, 0xFF848484);
    public static final List<Integer> PURPLE_SQUARE_COLOR_NEXT = List.of(0xFFA76DF2, 0xFFA366FF);
    public static final List<Integer> MENU_COLOR_NEXT = List.of(0xFF3D3D30);


    public static final List<Integer> RED_SQUARE_COLOR_DETECT = List.of(0xFFFF4A3D, 0xFFFF493C, 0xFFFF3E31, 0xFFFF3D30, 0xFFFF2518, 0xFFFF1A1A);
    public static final List<Integer> GREEN_SQUARE_COLOR_DETECT = List.of(0xFF7CFF6F, 0xFF44FF37, 0xFF1AFF1A);
    public static final List<Integer> YELLOW_SQUARE_COLOR_DETECT = List.of(0xFFFFFF3D, 0xFFFFFF18, 0xFFFFFF1A);
    public static final List<Integer> PURPLE_SQUARE_COLOR_DETECT = List.of(0xFFAC75FF);
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
}
