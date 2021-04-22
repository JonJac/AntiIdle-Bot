package idlebot.arcade.mmrx;

import idlebot.Game;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static idlebot.arcade.mmrx.MmrXConstants.*;

public class MmrXPlayer {
    private Game game;

    public MmrXPlayer(Game game) {
        this.game = game;
    }

    public void play() {
        game.waitMs(3200);

        int count = 0;

        int reds = 0;
        int blues = 0;
        int greens = 0;


        while (true) {
            count++;
            BufferedImage bufferedImage = game.screenShot(0, lanesY, 516, 10);
            for (int laneX : lanesX) {
                int rgb = bufferedImage.getRGB(laneX, 0);
                int rgbSave = bufferedImage.getRGB(laneX, 9);

                if (rgb == MENU) {
                    //return;
                } else if (RED.contains(rgb) || RED.contains(rgbSave)) {
                    reds++;
                } else if (GREEN.contains(rgb) || GREEN.contains(rgbSave)) {
                    greens++;
                } else if (BLUE.contains(rgb) || BLUE.contains(rgbSave)) {
                    blues++;
                }
            }

            if (reds + greens + blues > 0) {

                if (reds > 0) {
                    game.holdLeftArrowKey();
                    if (reds > 1) {
                        game.holdKey(KeyEvent.VK_A);
                        System.out.println("A");
                    }
                }

                if (greens > 0) {
                    game.holdDownArrowKey();
                    if (greens > 1) {
                        game.holdKey(KeyEvent.VK_S);
                        System.out.println("S");
                    }
                }
                if (blues > 0) {
                    game.holdRightArrowKey();
                    if (blues > 1) {
                        game.holdKey(KeyEvent.VK_D);
                        System.out.println("D");
                    }
                }
                game.waitMs(20);

                if (reds > 0) {
                    game.releaseLeftArrowKey();
                    if (reds > 1) {
                        game.releaseKey(KeyEvent.VK_A);
                    }
                }

                if (greens > 0) {
                    game.releaseDownArrowKey();
                    if (greens > 1) {
                        game.releaseKey(KeyEvent.VK_S);
                    }
                }
                if (blues > 0) {
                    game.releaseRightArrowKey();
                    if (blues > 1) {
                        game.releaseKey(KeyEvent.VK_D);
                    }
                }

                reds = 0;
                blues = 0;
                greens = 0;

            }
        }

    }

    private void print(String s, Set<Integer> redSet) {
        System.out.print(s);
        redSet.forEach(i -> System.out.print(" ," + String.format("0x%08X", i)));
        System.out.println();
    }
}
