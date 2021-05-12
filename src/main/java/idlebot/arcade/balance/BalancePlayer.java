package idlebot.arcade.balance;

import idlebot.Game;
import idlebot.XyTriple;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import static idlebot.arcade.balance.BalanceConstants.*;
import static idlebot.arcade.balance.BalanceSquare.*;
import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;

public class BalancePlayer {
    private Game game;
    private int waitAfterKeyPressMs = 30;
    private int waitBeforeDetectingMs = 20;
    private int blockCountBeforeForceEndingGame = 500;
    private final int nextSquareScreenShotWidth = 35;
    private BufferedImage blackAndWhiteNext = new BufferedImage(nextSquareScreenShotWidth, 30, TYPE_BYTE_BINARY);
    private int[] laneDepth = new int[3];

    public BalancePlayer(Game game) {
        this.game = game;
    }

    public void playBalance(boolean attemptMaxScore) {
        laneDepth = new int[3];

        clickStart();
        waitMs(3300);

        Stack<BalanceSquare> lane1 = new Stack<>();
        Stack<BalanceSquare> lane2 = new Stack<>();
        Stack<BalanceSquare> lane3 = new Stack<>();
        List<Stack<BalanceSquare>> lanes = List.of(lane1, lane2, lane3);

        int count = 0;
        //Problem: How to determine what the unknown sqaure was?
        while (true) {
            BalanceSquare nextSquare = getNextSquareUsingColor();
            BalanceSquare nextSquareUsingSymbol = getNextSquareUsingSymbol();
/*            if(nextSquare != nextSquareUsingSymbol){
                System.out.println("Missmatch with next: " + nextSquare + " was detected as " + nextSquareUsingSymbol);
                BufferedImage bufferedImage = game.screenShot(new Rectangle(82, 224, nextSquareScreenShotWidth, 30));
                writeNextSquareImageAsBW(bufferedImage);
            }*/
            System.out.println("next square: " + nextSquare);
            if (nextSquare == null) {
                saveDebugImageOfGame();
                break;
            }

            if (nextSquare == MenuAfterEndedGame) {
                System.out.println("Menu detected");
                break;
            }

            if (!attemptMaxScore && count > blockCountBeforeForceEndingGame) {
                playLane1(lane1, nextSquare);
            }

            if (hasSameSquareOnTop(lane1, nextSquare, 2)) {
                playLane1(lane1, nextSquare);
            } else if (hasSameSquareOnTop(lane2, nextSquare, 2)) {
                playLane2(lane2, nextSquare);
            } else if (hasSameSquareOnTop(lane3, nextSquare, 2)) {
                playLane3(lane3, nextSquare);
            } else if (hasSameSquareOnTop(lane1, nextSquare, 1)) {
                playLane1(lane1, nextSquare);
            } else if (hasSameSquareOnTop(lane2, nextSquare, 1)) {
                playLane2(lane2, nextSquare);
            } else if (hasSameSquareOnTop(lane3, nextSquare, 1)) {
                playLane3(lane3, nextSquare);
            } else {
                //Unknown ends up here
                int minIndex = indexOfLaneWithFewestSquares(lanes);
                System.out.println("Choosing min lane: " + minIndex + " with size = " + lanes.get(minIndex).size());
                if (minIndex == 0) {
                    playLane1(lane1, nextSquare);
                } else if (minIndex == 1) {
                    playLane2(lane2, nextSquare);
                } else {
                    playLane3(lane3, nextSquare);
                }
            }

            count++;
        }
    }

    private boolean hasSameSquareOnTop(Stack<BalanceSquare> lane1, BalanceSquare nextSquare, int amount) {
        if (amount == 1) {
            return lane1.size() > 0 && lane1.peek() == nextSquare;
        } else if (lane1.size() > amount - 1) {
            return lane1.get(lane1.size() - 1) == nextSquare && lane1.get(lane1.size() - 1) == lane1.get(lane1.size() - 2);
        }
        return false;
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

    private void detectUnknownAndUpdateLaneState(Stack<BalanceSquare> lane, BalanceSquare nextSquare, int laneXCoordinate) {
        waitMs(waitAfterKeyPressMs);
        if (nextSquare == Unknown) {
            waitMs(waitBeforeDetectingMs);
            detectUnknownWithSymbol(laneXCoordinate);
            nextSquare = detectUnknownWithColor(laneXCoordinate);
        }
        if (nextSquare == Unknown) {
            //We don't know how the current state, so just clear it and play on
            lane.clear();
            return;
        }
        if (nextSquare == NoBlock && lane.size() > 1) {
            clear2InARow(lane);
        } else {
            lane.push(nextSquare);
            clear3InARow(lane);
        }
    }

    public static int indexOfLaneWithFewestSquares(List<Stack<BalanceSquare>> list) {
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


    private BalanceSquare detectUnknownWithSymbol(int laneX) {

        BufferedImage bufferedImage = game.screenShot(new Rectangle(laneX - 8, LANE_WINDOW_Y, 42, LANE_WINDOW_HEIGHT));
        //writeLaneSquareImageAsBW(bufferedImage);

        int whiteCount = 0;
        while (bwLaneSquareMapping(21, whiteCount, bufferedImage) == white) {
            whiteCount++;
        }


        //first black pixel = how far down is the lane
        //4x white pixel = we are the bottom
        //5-6x white pixel, we need to detect symbol

        return Unknown;
    }

    private BalanceSquare detectUnknownWithColor(int laneX) {
        int count = 0;
        while (count < 30) {
            BufferedImage bufferedImage = game.screenShot(new Rectangle(laneX, LANE_WINDOW_Y, 1, LANE_WINDOW_HEIGHT));
/*            try {
                if (count == 1) {
                    ImageIO.write(bufferedImage, "bmp", new File("laneDetect.bmp"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }*/

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

        //throw new RuntimeException("Unable to detect top block in lane with X=" + laneX);

        return Unknown;
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
        if (lane.size() >= 2 && lane.get(1) == lane.get(0)) {
            lane.pop();
            lane.pop();
        }
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

    private BalanceSquare getNextSquareUsingSymbol() {
        BufferedImage bufferedImage = game.screenShot(new Rectangle(82, 224, nextSquareScreenShotWidth, 30));
        //writeImageAsBW(bufferedImage);
        if (check(xyNextRed, bufferedImage)) {
            return Red;
        }
        if (check(xyNextYellow, bufferedImage)) {
            return Yellow;
        }
        if (check(xyNextGreen, bufferedImage)) {
            return Green;
        }
        if (check(xyNextPurple, bufferedImage)) {
            return Purple;
        }
        if (check(xyNextUnknown, bufferedImage)) {
            return Unknown;
        }

        return MenuAfterEndedGame;
    }

    private void writeLaneSquareImageAsBW(BufferedImage bufferedImage) {
        BufferedImage blackAndWhiteNext = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), TYPE_BYTE_BINARY);

        for (int x = 0; x < blackAndWhiteNext.getWidth(); x++) {
            for (int y = 0; y < blackAndWhiteNext.getHeight(); y++) {
                blackAndWhiteNext.setRGB(x, y, bwLaneSquareMapping(x, y, bufferedImage));
            }
        }
        try {
            ImageIO.write(bufferedImage, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));
            ImageIO.write(blackAndWhiteNext, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int bwLaneSquareMapping(int x, int y, BufferedImage bufferedImage) {
        //Gray = 0.299×Red + 0.587×Green + 0.114×Blue
        int rgb = bufferedImage.getRGB(x, y);
        int blue = rgb & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int red = (rgb >> 16) & 0xFF;
        if (Math.abs(red - green) < 4) { //yellow square case
            if (0.52 * red + 0.52 * green + 0.10 * blue > 156) {
                return white;
            }
        } else {
            if (0.55 * red + 0.55 * green + 0.55 * blue > 150) {
                return white;
            }
        }
        return black;
    }

    private void writeNextSquareImageAsBW(BufferedImage bufferedImage) {
        BufferedImage blackAndWhiteNext = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), TYPE_BYTE_BINARY);
        for (int x = 0; x < blackAndWhiteNext.getWidth(); x++) {
            for (int y = 0; y < blackAndWhiteNext.getHeight(); y++) {
                blackAndWhiteNext.setRGB(x, y, bwNextSquareMapping(x, y, bufferedImage));
            }
        }
        try {
            ImageIO.write(blackAndWhiteNext, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int bwNextSquareMapping(int x, int y, BufferedImage bufferedImage) {
        //Gray = 0.299×Red + 0.587×Green + 0.114×Blue
        int rgb = bufferedImage.getRGB(x, y);
        int blue = rgb & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int red = (rgb >> 16) & 0xFF;
        if (0.6 * red + 0.6 * green + 0.6 * blue > 128) {
            return black;
        }
        return white;
    }

    private BalanceSquare getNextSquareUsingColor() {
        BufferedImage bufferedImage = game.screenShot(new Rectangle(NEXT_SQUARE_X, NEXT_SQUARE_Y, 1, 1));
        int rgb = bufferedImage.getRGB(0, 0);
        BalanceSquare square = rgbNextMap.get(rgb);
        if (square == null) {
            System.out.println("null value: " + String.format("0x%08X", rgb));
        }
        return square;
    }

    private void clickStart() {
        game.clickWithinGame(START_BUTTON_X, START_BUTTON_Y);
    }

    private boolean check(XyTriple[] xyGoodGreg, BufferedImage toBeBlackAndWhite) {
        boolean result = true;
        for (XyTriple triple : xyGoodGreg) {
            if (bwNextSquareMapping(triple.x - 1, triple.y - 1, toBeBlackAndWhite) != triple.color) {
                result = false;
                //System.out.println(triple + ", was " + blackAndWhite.getRGB(triple.x - 1,triple.y - 1));
            }
        }

        return result;
    }
}
