package idlebot.arcade.balance;

import idlebot.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

import static idlebot.arcade.balance.BalanceConstants.*;
import static idlebot.arcade.balance.BalanceSquare.*;

public class BalancePlayer {
    private Game game;
    private int waitAfterKeyPressMs = 25;
    private int waitBeforeDetectingMs = 0;

    public BalancePlayer(Game game) {
        this.game = game;
    }

    public void playBalance() {
        clickStart();
        waitMs(3500);

        Stack<BalanceSquare> lane1 = new Stack<>();
        Stack<BalanceSquare> lane2 = new Stack<>();
        Stack<BalanceSquare> lane3 = new Stack<>();
        List<Stack<BalanceSquare>> lanes = List.of(lane1, lane2, lane3);

        //Problem: How to determine what the unknown sqaure was?
        while (true) {
            BalanceSquare nextSquare = getNextSquare();
            System.out.println("next square: " + nextSquare);
            if (nextSquare == null) {
                saveDebugImageOfGame();
                break;
            }

            if (nextSquare == MenuAfterEndedGame) {
                System.out.println("Menu detected");
                break;
            }

            if (lane1.size() > 0 && lane1.peek() == nextSquare) {
                playLane1(lane1, nextSquare);
            } else if (lane2.size() > 0 && lane2.peek() == nextSquare) {
                playLane2(lane2, nextSquare);
            } else if (lane3.size() > 0 && lane3.peek() == nextSquare) {
                playLane3(lane3, nextSquare);
            } else {
                int minIndex = minIndex(lanes);
                System.out.println("Choosing min lane: " + minIndex + " with size = " + lanes.get(minIndex).size());
                if (minIndex == 0) {
                    playLane1(lane1, nextSquare);
                } else if (minIndex == 1) {
                    playLane2(lane2, nextSquare);
                } else {
                    playLane3(lane3, nextSquare);
                }
            }

        }
    }

    private void playLane3(Stack<BalanceSquare> lane3, BalanceSquare nextSquare) {
        game.pressRightArrowKey();
        detectUnknownAndUpdateLaneState(lane3, nextSquare, LANE_3_X);
        System.out.println("Lane 3:" + lane3);
    }

    private void playLane2(Stack<BalanceSquare> lane2, BalanceSquare nextSquare) {
        game.pressDownArrowKey();
        detectUnknownAndUpdateLaneState(lane2, nextSquare, LANE_2_X);
        System.out.println("Lane 2:" + lane2);
    }

    private void playLane1(Stack<BalanceSquare> lane1, BalanceSquare nextSquare) {
        game.pressLeftArrowKey();
        detectUnknownAndUpdateLaneState(lane1, nextSquare, LANE_1_X);
        System.out.println("Lane 1:" + lane1);
    }

    private void detectUnknownAndUpdateLaneState(Stack<BalanceSquare> lane2, BalanceSquare nextSquare, int lane2X) {
        waitMs(waitAfterKeyPressMs);
        if (nextSquare == Unknown) {
            waitMs(waitBeforeDetectingMs);
            nextSquare = detectUnknown(lane2X);
        }
        if (nextSquare == NoBlock) {
            clear2InARow(lane2);
        } else {
            lane2.push(nextSquare);
            clear3InARow(lane2);
        }
    }

    public static int minIndex(List<Stack<BalanceSquare>> list) {
        int i = 0, minIndex = -1, minimum = Integer.MAX_VALUE;
        for (Stack<BalanceSquare> x : list) {
            if ((x != null) && (x.size() < minimum)) {
                minimum = x.size();
                minIndex = i;
            }
            i++;
        }
        System.out.println("Index " + minIndex + " won. Lanes: " + list.get(0).size() + "," + list.get(1).size() + "," + list.get(2).size());
        return minIndex;
    }

    private BalanceSquare detectUnknown(int laneX) {
        int count = 0;
        while (count < 30) {
            BufferedImage bufferedImage = game.screenShot(new Rectangle(laneX, LANE_WINDOW_Y, 1, LANE_WINDOW_HEIGHT));
            try {
                if (count == 1) {
                    ImageIO.write(bufferedImage, "bmp", new File("laneDetect.bmp"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < bufferedImage.getHeight(); i++) {
                BalanceSquare square = rgbDetectMap.get(bufferedImage.getRGB(0, i));
                if (square != null && square != MenuAfterEndedGame) {
                    System.out.println("Detected square: " + square);
                    if (square == NoBlock && count < 29) {
                        break;
                    }
                    return square;
                }
            }

            System.out.println("Unable to detect top block in lane with X=" + laneX);
            waitMs(1);
            count++;
        }

        throw new RuntimeException("Unable to detect top block in lane with X=" + laneX);
    }

    private void clear3InARow(Stack<BalanceSquare> lane) {
        int size = lane.size();
        if (lane.size() >= 3 && lane.get(size - 1) == lane.get(size - 2) && lane.get(size - 2) == lane.get(size - 3)) {
            lane.pop();
            lane.pop();
            lane.pop();
        }
    }

    private void clear2InARow(Stack<BalanceSquare> lane) {
        lane.pop();
        lane.pop();
    }


    private void waitMs(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveDebugImageOfGame() {
        try {
            game.screenshotGame("BalanceError.bmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BalanceSquare getNextSquare() {
        BufferedImage bufferedImage = game.screenShot(new Rectangle(NEXT_SQUARE_X, NEXT_SQUARE_Y, 1, 1));
        int rgb = bufferedImage.getRGB(0, 0);
        return rgbNextMap.get(rgb);
    }

    private void clickStart() {
        game.clickWithinGame(START_BUTTON_X, START_BUTTON_Y);
    }

}
