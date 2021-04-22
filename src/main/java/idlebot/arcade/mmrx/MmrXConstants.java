package idlebot.arcade.mmrx;

import java.util.List;

public class MmrXConstants {
    public static int[] lanesX = new int[]{70, 120, 170, 220, 270, 320, 370, 420, 470};
    public static int lanesY = 365;

    public static List<Integer> GREEN = List.of(0xFF0DCA00, 0xFF3CFA2F, 0xFF34F227, 0xFF3CFA2F, 0xFF32F025, 0xFF00C800, 0xFF32FA32, 0xFF2AF22A, 0xFF28F028);
    public static List<Integer> RED = List.of(0xFFCA0D00, 0xFFD23A2D, 0xFFCC3427, 0xFFCA3225, 0xFFC80000, 0xFFD03030, 0xFFCC2C2C, 0xFFC82828);
    public static List<Integer> BLUE = List.of(0xFF0D6BBD, 0xFF3C9BED, 0xFF3493E5, 0xFF3291E3, 0xFF0064C8, 0xFF3296FA, 0xFF2A8EF2, 0xFF288CF0);
    public static List<Integer> BACKGROUND = List.of(0xFF2E2E21, 0xFF323225, 0xFF3D3D30, 0xFF6B6B5E, 0xFF49493C); //NOTICE 0xFF49493C is the line going down

    public static int MENU = 0xFF333333;
    public static int MENU2 = 0xFF3D3D30;

}
