package idlebot.arcade.whackagreg;

import idlebot.Game;
import ml.GregClassifier;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static idlebot.arcade.whackagreg.WhackAGregConstants.BEGIN_GAME_BUTTON_X;
import static idlebot.arcade.whackagreg.WhackAGregConstants.BEGIN_GAME_BUTTON_Y;

public class WhackAGregPlayer {
    private static final Object lock = new Object();
    private Robot robot;
    private Map<Rectangle, Long> lastClickedMap = new ConcurrentHashMap<>();
    private final int timeSinceLastClickMs = 500;
    private int maxClick = 20000;
    private Game game;

    public WhackAGregPlayer(Game game) {
        this.game = game;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void playAGameOfGreg(Rectangle antiIdleRect) throws IOException, InterruptedException {
        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork("greg.model");
        for (int i = 0; i < 1; i++) {
            {
                game.clickWithinGame(BEGIN_GAME_BUTTON_X, BEGIN_GAME_BUTTON_Y);
                Thread.sleep(3500);
            }
            AtomicInteger clickCount = new AtomicInteger(0);

            final List<Object> addObjectToStopLoop = new ArrayList<>();
            while (addObjectToStopLoop.size() < 100) {

                BufferedImage capture = robot.createScreenCapture(antiIdleRect);

                //for(int x = 0; x < WhackAGregConstants.SQUARE_X_AMOUNT; x++) {
                IntStream.range(0, WhackAGregConstants.SQUARE_X_AMOUNT).parallel().forEach(x -> {
                    for (int y = 0; y < WhackAGregConstants.SQUARE_Y_AMOUNT; y++) {
                        int x1 = WhackAGregConstants.RELATIVE_TOP_LEFT_X + x * WhackAGregConstants.SQUARE_WIDTH; //antiIdleRect.x +
                        int y1 = WhackAGregConstants.RELATIVE_TOP_LEFT_Y + y * WhackAGregConstants.SQUARE_HEIGHT; //antiIdleRect.y +
                        //System.out.println("Looking for square from: x=" + x1 + ", y=" + y1);

                        if(clickCount.get() >= maxClick){
                            game.clickWithinGame(WhackAGregConstants.RELATIVE_TOP_LEFT_X + WhackAGregConstants.SQUARE_WIDTH / 2, WhackAGregConstants.RELATIVE_TOP_LEFT_Y + WhackAGregConstants.SQUARE_HEIGHT / 2);
                        }

                        NativeImageLoader loader = new NativeImageLoader(40, 40);

                        BufferedImage square = capture.getSubimage(x1, y1, WhackAGregConstants.SQUARE_WIDTH, WhackAGregConstants.SQUARE_HEIGHT);
                        try {
                            int[] predict = predict(model, loader, square);
                            //System.out.println("predict: " + predict[0]);


                            //ImageIO.write(square, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));

                            if (predict[0] == 6) { //are we in menu?

                                //ImageIO.write(square, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));
                                System.out.println("predict: " + predict[0]);
                                addObjectToStopLoop.add(Boolean.TRUE);
                            }

                            if (GregClassifier.stuffToClick.contains(predict[0])) {
                                //ImageIO.write(square, "bmp", new File("images/predicted_" + predict[0] + "_" + UUID.randomUUID() + ".bmp"));
                                System.out.println("GOOD PREDICT");
                                clickCount.incrementAndGet();

                                synchronized (lock) {
                                    Rectangle gregRect = new Rectangle(
                                            antiIdleRect.x + x1,
                                            antiIdleRect.y + y1,
                                            WhackAGregConstants.SQUARE_WIDTH, WhackAGregConstants.SQUARE_HEIGHT);
                                    if (lastClickedMap.get(gregRect) == null || lastClickedMap.get(gregRect) < System.currentTimeMillis() - timeSinceLastClickMs) {
/*                                    BufferedImage screenCapture = robot.createScreenCapture(gregRect);
                                    int guess = predict(model, loader, square)[0];
                                    System.out.println("predict: " + guess);
                                    if(GregClassifier.stuffToClick.contains(guess)){
                                        ImageIO.write(screenCapture, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));
                                    }*/
                                        game.clickWithinGame(x1 + WhackAGregConstants.SQUARE_WIDTH / 2, y1 + WhackAGregConstants.SQUARE_HEIGHT / 2);
                                        lastClickedMap.put(gregRect, System.currentTimeMillis());
                                    } else {
                                        //ImageIO.write(square, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));
                                    }
                                }
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

            {
                Thread.sleep(2000);
                game.clickWithinGame(260, 395);
                Thread.sleep(100);
            }
        }
    }

    private static int[] predict(MultiLayerNetwork model, NativeImageLoader loader, BufferedImage square) throws IOException {
        INDArray image;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(square, "bmp", os);                          // Passing: ​(RenderedImage im, String formatName, OutputStream output)
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        image = loader.asRowVector(is);

        INDArray input = Nd4j.create(new int[]{1, 40 * 40 * 3}).addRowVector(image);
        input.putRow(0, image);
        int[] predict = model.predict(input);
        return predict;
    }
}
