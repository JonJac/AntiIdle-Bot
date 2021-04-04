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


        outer:
        while (true) {
            barImage = game.screenShot(FishingConstants.fishingBar);
            for (int i = 0; i < barImage.getWidth(); i++) {
                if (FishingConstants.TRIANGLE_COLORS.contains(barImage.getRGB(i, barImage.getHeight() - 1))) { //only look at lowest row
                    //System.out.println("triangle middle: " + (i + halfTriangle));
                    if (i + halfTriangle == indicatorIndex + 1 || i + halfTriangle == indicatorIndex + 2) {
                        game.pressSpace();
                        //System.out.println("Pressed space");
                        break outer;
                    }
                    break;
                }
            }
        }


    }


}
