package idlebot.arcade.balance;

import idlebot.Game;
import idlebot.XyTriple;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static idlebot.arcade.balance.BalanceConstants.*;
import static idlebot.arcade.balance.BalanceSquare.*;
import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;

public class BalancePlayer {
    private Game game;
    private int waitAfterKeyPressMs = 20;
    private int waitBeforeDetectingMs = 25;
    private int blockCountBeforeForceEndingGame = 680;
    private final int nextSquareScreenShotWidth = 35;
    private int[] laneDepth = new int[3];

    public BalancePlayer(Game game) {
        this.game = game;
    }

    public int playBalance(boolean attemptMaxScore) {
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
            //BalanceSquare nextSquareOld = getNextSquareUsingColor();
            BalanceSquare nextSquare = getNextSquareUsingSymbol();
/*            if(nextSquare != nextSquareOld){
                System.out.println("Mismatch next square: " + nextSquare + ", " + nextSquareOld);
            }*/

            //System.out.println("next square: " + nextSquare);
            if (nextSquare == null) {
                saveDebugImageOfGame();
                break;
            }

            if (nextSquare == MenuAfterEndedGame) {
                System.out.println("Menu detected");
                break;
            }

            if (!attemptMaxScore && count > blockCountBeforeForceEndingGame) {
                System.out.println("SUICIDE");
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
                int minIndex = indexOfLaneWithFewestSquaresThatDoesNotHavePurpleOnTop_UseLaneDepthWhenEqual(lanes);
                if (minIndex == 0) {
                    playLane1(lane1, nextSquare);
                } else if (minIndex == 1) {
                    playLane2(lane2, nextSquare);
                } else {
                    playLane3(lane3, nextSquare);
                }
            }

            if ((count & 0b00000000000000000000000000001111) == 0) { //every 16th nextSquare
                updateLaneDepth(LANE_1_X);
                updateLaneDepth(LANE_2_X);
                updateLaneDepth(LANE_3_X);

/*                if(count > 1500 && theDeepestLaneIsTheLightest(lanes)){
                    System.out.println("wain as theDeepestLaneIsTheLightest");
                    game.waitMs(500);
                }*/
            }

            //The state is still imperfect. The state here often has less blocks than in real game. One can use Legend difficulty and the stuff below to try and debug it.
            String state = "State: Depth: " + Arrays.toString(laneDepth) + ", Lanes: " + lane1.size() + "," + lane2.size() + "," + lane3.size() + ", " + lane1 + ", " + lane2 + ", " + lane3;
            //System.out.println(state);


/*
            int lane1Height = detectAmountOfSquaresInLane(LANE_1_X);
            if(lane1.size() != lane1Height){
                System.out.println("Lane 1 is incorrect size. Real height: " + lane1Height);
            }
            int lane2Height = detectAmountOfSquaresInLane(LANE_2_X);
            if(lane2.size() != lane2Height){
                System.out.println("Lane 2 is incorrect size. Real height: " + lane2Height);
            }
            int lane3Height = detectAmountOfSquaresInLane(LANE_3_X);
            if(lane3.size() != lane3Height){
                System.out.println("Lane 3 is incorrect size. Real height: " + lane3Height);
            }
*/

/*            try {
                game.screenshotGame("images/"+ state.replace(':',' ') +".bmp");
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }*/

            //game.waitMs(50);

            count++;
        }

        return count;
    }

    private boolean theDeepestLaneIsTheLightest(List<Stack<BalanceSquare>> lanes) {
        int deepestLane = -1, deepestMax = Integer.MIN_VALUE, lightestLane = -2, lightestMin = Integer.MAX_VALUE;
        for (int i = 0; i < lanes.size(); i++) {
            if (lanes.get(i).size() < lightestMin) {
                lightestLane = i;
                lightestMin = lanes.get(i).size();
            } else if (lanes.get(i).size() == lightestMin) { //forces us to find the exclusive lightest
                lightestLane = -2;
            }
        }

        for (int i = 0; i < laneDepth.length; i++) {
            if (laneDepth[i] > deepestMax) { //|| (laneDepth[i] == deepestMax && lanes.get(i).size() > lanes.get(lightestLane).size())
                deepestLane = i;
                deepestMax = laneDepth[i];
            }
        }

        boolean result = deepestLane == lightestLane && laneDepth[deepestLane] > 245;
        if (result) {
            System.out.println("Lane " + deepestLane + " was determined to be the deepest and the lightest: " + deepestMax + ", " + lightestMin);
        }
        return result;
    }

    private boolean hasSameSquareOnTop(Stack<BalanceSquare> lane, BalanceSquare nextSquare, int amount) {
        if (amount == 1) {
            return lane.size() > 0 && lane.peek() == nextSquare;
        } else if (amount == 2 && lane.size() > 1) {
            return lane.get(lane.size() - 1) == nextSquare && lane.get(lane.size() - 1) == lane.get(lane.size() - 2);
        }
        return false;
    }

    private void playLane3(Stack<BalanceSquare> lane3, BalanceSquare nextSquare) {
        game.pressRightArrowKey();
        detectUnknownAndUpdateLaneState(lane3, nextSquare, LANE_3_X);
        //System.out.println("Lane 3:" + lane3);
    }

    private void playLane2(Stack<BalanceSquare> lane2, BalanceSquare nextSquare) {
        game.pressDownArrowKey();
        detectUnknownAndUpdateLaneState(lane2, nextSquare, LANE_2_X);
        //System.out.println("Lane 2:" + lane2);
    }

    private void playLane1(Stack<BalanceSquare> lane1, BalanceSquare nextSquare) {
        game.pressLeftArrowKey();
        detectUnknownAndUpdateLaneState(lane1, nextSquare, LANE_1_X);
        //System.out.println("Lane 1:" + lane1);
    }

    private void detectUnknownAndUpdateLaneState(Stack<BalanceSquare> lane, BalanceSquare nextSquare, int laneXCoordinate) {
        if (nextSquare == Unknown) {
            waitMs(waitBeforeDetectingMs);
            nextSquare = detectUnknownWithSymbol(laneXCoordinate);
            //nextSquare = detectUnknownWithColor(laneXCoordinate);
        } else {
            waitMs(waitAfterKeyPressMs);
        }

        if (nextSquare == Unknown) {
            //We don't know how the current state, so just clear it and play on
            lane.clear();
            return;
        }
        if (nextSquare == NoBlock && lane.size() > 1) {
            clear2InARow(lane);
        } else if (nextSquare != NoBlock) { //Any case where we end up empty should be caught above
            lane.push(nextSquare);
            clear3InARow(lane);
        }
    }

    public int indexOfLaneWithFewestSquaresThatDoesNotHavePurpleOnTop_UseLaneDepthWhenEqual(List<Stack<BalanceSquare>> list) {
        int i = 0, minIndex = -1, minimum = Integer.MAX_VALUE;
        int[] sizes = new int[]{10, 10, 10};
        for (Stack<BalanceSquare> lane : list) {
            if (lane.size() > 0 && lane.peek() == Purple) {
                i++;
                continue;
            }
            if (lane.size() < minimum) {
                minimum = lane.size();
                minIndex = i;
            }
            sizes[i] = lane.size();
            i++;
        }

        //if equal squares, pick the lanes highest up
        ArrayList<Integer> lowest = new ArrayList<>();
        for (int k = 0; k < sizes.length; k++) {
            if (sizes[k] == minimum) {
                lowest.add(k);
            }
        }
        if (lowest.size() > 1) {
            int highestLane = Integer.MAX_VALUE;
            for (Integer index : lowest) {
                if (laneDepth[index] < highestLane) {
                    highestLane = laneDepth[index];
                    minIndex = index;
                }
            }
        } else {
            minIndex = lowest.get(0);
        }

        //System.out.println("Index " + minIndex + " won. Lanes: " + list.get(0).size() + "," + list.get(1).size() + "," + list.get(2).size());
        return minIndex;
    }

    private int detectAmountOfSquaresInLane(int laneX) {

        BufferedImage bufferedImage = game.screenShot(new Rectangle(laneX - 8, LANE_WINDOW_Y, 42, LANE_WINDOW_HEIGHT));

        int whiteCount = 3;
        while (bwLaneSquareMapping(21, whiteCount, bufferedImage) == white) {
            whiteCount++;
        }
        laneDepth[laneMap.get(laneX)] = whiteCount;

        int blackCount = whiteCount;
        while (bwLaneSquareMapping(21, blackCount, bufferedImage) == black) {
            blackCount++;
        }

        return 8 - (blackCount - whiteCount) / 20;
    }


    private void updateLaneDepth(int laneX) {
        BufferedImage bufferedImage = game.screenShot(new Rectangle(laneX - 8, LANE_WINDOW_Y, 42, LANE_WINDOW_HEIGHT));

        int blackCount = LANE_WINDOW_HEIGHT - 1;
        while (bwLaneSquareMapping(2, blackCount, bufferedImage) == black) {
            blackCount--;
        }
        laneDepth[laneMap.get(laneX)] = blackCount;
    }


    private BalanceSquare detectUnknownWithSymbol(int laneX) {

        BufferedImage bufferedImage = game.screenShot(new Rectangle(laneX - 8, LANE_WINDOW_Y, 42, LANE_WINDOW_HEIGHT));

        //first black pixel = how far down is the lane
        //4x white pixel = we are the bottom
        //5-6x white pixel, we need to detect symbol

        int whiteCount = 3;
        while (bwLaneSquareMapping(21, whiteCount, bufferedImage) == white) {
            whiteCount++;
        }
        //laneDepth[laneMap.get(laneX)] = whiteCount;

        int blackCount = whiteCount;
        while (bwLaneSquareMapping(21, blackCount, bufferedImage) == black) {
            blackCount++;
        }

        int whiteSymbolReachedCount = 0;
        while (bwLaneSquareMapping(21 - 15, blackCount + whiteSymbolReachedCount, bufferedImage) == white && whiteSymbolReachedCount < 5) { //-15 x because we then avoid hitting the symbols
            whiteSymbolReachedCount++;
        }

        if (whiteSymbolReachedCount == 4) {
            writeLaneSquareImageAsBW(bufferedImage, "lane/noBlock-");
            return NoBlock;
        }
        if (checkLane(xyLaneGreen, bufferedImage, blackCount)) {
            writeLaneSquareImageAsBW(bufferedImage, "lane/Green-");
            return Green;
        }
        if (checkLane(xyLaneRed, bufferedImage, blackCount)) {
            writeLaneSquareImageAsBW(bufferedImage, "lane/Red-");
            return Red;
        }
        if (checkLane(xyLaneYellow, bufferedImage, blackCount)) {
            writeLaneSquareImageAsBW(bufferedImage, "lane/Yellow-");
            return Yellow;
        }

        writeLaneSquareImageAsBW(bufferedImage, "lane/Unknown-");
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
            game.screenshotGame("images/BalanceError.bmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BalanceSquare getNextSquareUsingSymbol() {
        BufferedImage bufferedImage = game.screenShot(new Rectangle(82, 224, nextSquareScreenShotWidth, 30));
        if (checkNext(xyNextRed, bufferedImage)) {
            //writeNextSquareImageAsBW(bufferedImage, "Red-");
            return Red;
        }
        if (checkNext(xyNextYellow, bufferedImage)) {
            //writeNextSquareImageAsBW(bufferedImage, "Yellow-");
            return Yellow;
        }
        if (checkNext(xyNextGreen, bufferedImage)) {
            //writeNextSquareImageAsBW(bufferedImage, "Green-");
            return Green;
        }
        if (checkNext(xyNextPurple, bufferedImage)) {
            //writeNextSquareImageAsBW(bufferedImage, "Purple-");
            return Purple;
        }
        if (checkNext(xyNextUnknown, bufferedImage)) {
            //writeNextSquareImageAsBW(bufferedImage, "Unknown-");
            return Unknown;
        }

        return MenuAfterEndedGame;
    }


    private void writeLaneSquareImageAsBW(BufferedImage bufferedImage, String filenamePrefix) {
        /*
        BufferedImage blackAndWhiteNext = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), TYPE_BYTE_BINARY);

        for (int x = 0; x < blackAndWhiteNext.getWidth(); x++) {
            for (int y = 0; y < blackAndWhiteNext.getHeight(); y++) {
                blackAndWhiteNext.setRGB(x, y, bwLaneSquareMapping(x, y, bufferedImage));
            }
        }
        try {
            UUID uuid = UUID.randomUUID();
            ImageIO.write(bufferedImage, "bmp", new File("images/" + filenamePrefix + uuid + ".bmp"));
            ImageIO.write(blackAndWhiteNext, "bmp", new File("images/" + filenamePrefix + uuid + "-bw" + ".bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }

         */
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


    private void writeNextSquareImageAsBW(BufferedImage bufferedImage, String prefix) {
        BufferedImage blackAndWhiteNext = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), TYPE_BYTE_BINARY);
        for (int x = 0; x < blackAndWhiteNext.getWidth(); x++) {
            for (int y = 0; y < blackAndWhiteNext.getHeight(); y++) {
                blackAndWhiteNext.setRGB(x, y, bwNextSquareMapping(x, y, bufferedImage));
            }
        }
        try {
            UUID uuid = UUID.randomUUID();
            ImageIO.write(blackAndWhiteNext, "bmp", new File("images/" + prefix + uuid + ".bmp"));
            ImageIO.write(bufferedImage, "bmp", new File("images/" + prefix + uuid + "-bw" + ".bmp"));
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

    private boolean checkLane(XyTriple[] xyGoodGreg, BufferedImage toBeBlackAndWhite, int yOffset) {
        boolean result = true;
        for (XyTriple triple : xyGoodGreg) {
            if (bwLaneSquareMapping(triple.x - 1, triple.y - 1 + yOffset, toBeBlackAndWhite) != triple.color) {
                result = false;
                //System.out.println(triple + ", was " + blackAndWhite.getRGB(triple.x - 1,triple.y - 1));
            }
        }

        return result;
    }

    private boolean checkNext(XyTriple[] xyGoodGreg, BufferedImage toBeBlackAndWhite) {
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
