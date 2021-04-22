package idlebot.arcade.mmrx;

import java.util.List;

public class MmrXConstants {
    public static int[] lanesX = new int[]{70, 120, 170, 220, 270, 320, 370, 420, 470};
    public static int lanesY = 366;

    public static List<Integer> GREEN = List.of(0xFF0DCA00, 0xFF3CFA2F, 0xFF34F227, 0xFF3CFA2F, 0xFF32F025);
    public static List<Integer> RED = List.of(0xFFCA0D00, 0xFFD23A2D, 0xFFCC3427, 0xFFCA3225);
    public static List<Integer> BLUE = List.of(0xFF0D6BBD, 0xFF3C9BED, 0xFF3493E5, 0xFF3291E3);
    public static List<Integer> BACKGROUND = List.of(0xFF2E2E21, 0xFF323225, 0xFF3D3D30, 0xFF6B6B5E, 0xFF49493C); //NOTICE 0xFF49493C is the line going down

    public static int MENU = 0xFF333333;
    public static int MENU2 = 0xFF3D3D30;

}
