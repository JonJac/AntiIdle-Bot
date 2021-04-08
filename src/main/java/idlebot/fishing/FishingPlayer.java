package idlebot.fishing;

import idlebot.Game;
import ml.FishingClassifier;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;

import static ml.ModelUtil.predict;

public class FishingPlayer {
    private Game game;

    public FishingPlayer(Game game) {
        this.game = game;
    }

    public void play() throws IOException, InterruptedException {
        while (true) {
            BufferedImage barImage = game.screenShot(FishingConstants.fishingBar);
            for (int i = 0; i < barImage.getWidth(); i++) {
                if (FishingConstants.TRIANGLE_COLORS.contains(barImage.getRGB(i, barImage.getHeight() - 1))) { //only look at lowest row
                    trianglePresent(barImage);
                    game.clickWithinGame(FishingConstants.levelUp);
                    break;
                }
            }
            //game.waitMs(50);
        }
    }

    private void scanner(BufferedImage barImage) throws IOException {
        System.out.println("SCANNING!");
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object

        String userName = myObj.nextLine();  // Read user input
        barImage = game.screenShot(FishingConstants.fishingBar);
        ImageIO.write(barImage, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));

        userName = myObj.nextLine();  // Read user input
        barImage = game.screenShot(FishingConstants.fishingBar);
        ImageIO.write(barImage, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));

        userName = myObj.nextLine();  // Read user input
        barImage = game.screenShot(FishingConstants.fishingBar);
        ImageIO.write(barImage, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));
    }

    private void trianglePresent(BufferedImage barImage) throws IOException {
        System.out.println("triangle!");
        int[] triangleHistory = new int[FishingConstants.triangleHistory];
        int halfTriangle = FishingConstants.triangleWidth / 2;
        int indicatorIndex = -1;

        for (int i = 0; i < barImage.getWidth(); i++) {
            if (barImage.getRGB(i, 0) == FishingConstants.BLUE_INDICATOR_COLOR && barImage.getRGB(i + 3, 0) == FishingConstants.BLUE_INDICATOR_COLOR) {
                indicatorIndex = i;
                break;
            }
        }

        if (indicatorIndex == -1) {
            System.out.println("could not find middle");
            ImageIO.write(barImage, "bmp", new File("images/Error-" + UUID.randomUUID() + ".bmp"));
            return;
        }

        System.out.println("indicatorIndex=" + indicatorIndex);

        int count = 0;

        outer:
        while (true) {
            game.waitMs(20);
            barImage = game.screenShot(FishingConstants.fishingBar);
            for (int i = 0; i < barImage.getWidth(); i++) {
                if (FishingConstants.TRIANGLE_COLORS.contains(barImage.getRGB(i, barImage.getHeight() - 1))) { //only look at lowest row
                    //System.out.println("triangle middle: " + (i + halfTriangle));
                    if (count < triangleHistory.length) {
                        triangleHistory[count] = i + halfTriangle;
                    }
                    if (triangleOnIndicator(halfTriangle, indicatorIndex, i) || predictedSpeed(triangleHistory, halfTriangle, indicatorIndex, count)) {
                        game.pressSpace();
                        //System.out.println("Pressed space");
                        break outer;
                    }
                    break;
                }
            }

            count++;
        }
    }

    private boolean predictedSpeed(int[] triangleHistory, int halfTriangle, int indicatorIndex, int count) {
        return count >= triangleHistory.length && Math.abs(halfTriangle - indicatorIndex) == speedPrImage(triangleHistory);
    }

    private boolean triangleOnIndicator(int halfTriangle, int indicatorIndex, int i) {
        return i + halfTriangle == indicatorIndex + 1 || i + halfTriangle == indicatorIndex + 2;
    }

    private int speedPrImage(int[] triangleHistory) {
        int sum = 0;
        for (int i = 1; i < triangleHistory.length; i++) {
            sum += Math.abs(triangleHistory[i] - triangleHistory[i - 1]);
        }
        //System.out.println("sum: " + sum);
        return sum / (triangleHistory.length - 1);
    }


}
