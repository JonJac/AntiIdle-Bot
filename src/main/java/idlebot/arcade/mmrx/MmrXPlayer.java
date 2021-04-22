package idlebot.arcade.mmrx;

import idlebot.Game;

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

    public void play(Game game2, Game game3) {
        game.waitMs(3200);
/*        Set<Integer> reds = new HashSet<>();
        Set<Integer> greens = new HashSet<>();
        Set<Integer> blues = new HashSet<>();*/

        ExecutorService executorService = Executors.newFixedThreadPool(2);


        Future<?> submit = executorService.submit(makeRunnable(lanesY, game2));
        Future<?> submit1 = executorService.submit(makeRunnable(lanesY + 10, game3));

        try {
            submit.get();
            submit1.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    private Runnable makeRunnable(final int y, Game game) {
        return () -> {
            int count = 0;

            int reds = 0;
            int blues = 0;
            int greens = 0;

            Set<Integer> redSet = new HashSet<>();
            Set<Integer> blueSet = new HashSet<>();
            Set<Integer> greenSet = new HashSet<>();

            while (true) {
                count++;
                //BufferedImage topLine = game.screenShot(0, 200, 516, 1);
                BufferedImage bufferedImage = game.screenShot(0, y, 516, 1);
                for (int laneX : lanesX) {
                    int rgb = bufferedImage.getRGB(laneX, 0);
                    //int rgb2 = bufferedImage.getRGB(laneX, 2);

                    if (rgb == MENU || rgb == MENU2) {
                        return;
                    } else if (RED.contains(rgb)) {
                        //System.out.println("left");
                        reds++;
                    } else if (GREEN.contains(rgb)) {
                        //System.out.println("down");
                        greens++;
                    } else if (BLUE.contains(rgb)) {
                        //System.out.println("right");
                        blues++;
                    }
                }

                while (reds > 0 || blues > 0 || greens > 0) {
                    if (reds > 0) {
                        game.holdLeftArrowKey();
                    }
                    if (greens > 0) {
                        game.holdDownArrowKey();
                    }
                    if (blues > 0) {
                        game.holdRightArrowKey();
                    }
                    game.waitMs(16);

                    if (reds > 0) {
                        game.releaseLeftArrowKey();
                        reds--;
                    }
                    if (greens > 0) {
                        game.releaseDownArrowKey();
                        greens--;
                    }
                    if (blues > 0) {
                        game.releaseRightArrowKey();
                        blues--;
                    }

/*               if(reds > 0 || blues > 0 || greens > 0){
                    game.waitMs(1);
                    System.out.println("waiting extra");
                }*/
                    break;
                }
                //game.waitMs(10);

/*            int loopCount = 0;
            for (int laneX : lanesX){
                loopCount++;
                int rgb = topLine.getRGB(laneX, 0);

                if(BACKGROUND.contains(rgb)){
                    //ignore
                } else if (loopCount % 3 == 1){
                    redSet.add(rgb);
                } else if (loopCount % 3 == 2){
                    greenSet.add(rgb);
                } else if (loopCount % 3 == 0){
                    blueSet.add(rgb);
                }
            }

            if(count % 500 == 0){
                print("red: ", redSet);
                print("green: ", greenSet);
                print("blue: ", blueSet);
            }*/
            }
        };
    }

    private void print(String s, Set<Integer> redSet) {
        System.out.print(s);
        redSet.forEach(i -> System.out.print(" ," + String.format("0x%08X", i)));
        System.out.println();
    }
}
