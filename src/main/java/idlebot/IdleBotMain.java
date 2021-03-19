package idlebot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import idlebot.arcade.whackagreg.WhackAGregConstants;
import ml.GregClassifier;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.imageio.ImageIO;

public class IdleBotMain {
    private static final Object lock = new Object();
    private static Robot robot;
    private static Map<Rectangle, Long> lastClickedMap = new ConcurrentHashMap<>();
    private static final int timeSinceLastClickMs = 500;
    private static int maxClick = 140;

    public static void main(String[] args) throws AWTException, IOException, InterruptedException {
        robot = new Robot();
        Rectangle antiIdleRect = getWindowForProcess("Adobe Flash Player 9");

        System.out.println("AntiIdleRect: " + antiIdleRect);

        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork("model2.lol");
        for (int i = 0; i < 1; i++) {
            {
                int x1 = antiIdleRect.x + 70;
                int y1 = antiIdleRect.y + 375;
                click(x1, y1);
                Thread.sleep(3500);
            }
            AtomicInteger clickCount = new AtomicInteger(0);

            final List<Object> addObjectToStopLoop = new ArrayList<>();
            while (addObjectToStopLoop.size() == 0) {

                BufferedImage capture = robot.createScreenCapture(antiIdleRect);

                //Thread.sleep(100);
                //for(int x = 0; x < WhackAGregConstants.SQUARE_X_AMOUNT; x++) {
                IntStream.range(0, WhackAGregConstants.SQUARE_X_AMOUNT).parallel().forEach(x -> {
                    for (int y = 0; y < WhackAGregConstants.SQUARE_Y_AMOUNT; y++) {
                        int x1 = WhackAGregConstants.RELATIVE_TOP_LEFT_X + x * WhackAGregConstants.SQUARE_WIDTH; //antiIdleRect.x +
                        int y1 = WhackAGregConstants.RELATIVE_TOP_LEFT_Y + y * WhackAGregConstants.SQUARE_HEIGHT; //antiIdleRect.y +
                        //System.out.println("Looking for square from: x=" + x1 + ", y=" + y1);

                        if(clickCount.get() >= maxClick){
                            click(antiIdleRect.x + WhackAGregConstants.SQUARE_X_AMOUNT + WhackAGregConstants.SQUARE_WIDTH / 2, antiIdleRect.y + y1 + WhackAGregConstants.SQUARE_HEIGHT / 2);
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
                                        click(antiIdleRect.x + x1 + WhackAGregConstants.SQUARE_WIDTH / 2, antiIdleRect.y + y1 + WhackAGregConstants.SQUARE_HEIGHT / 2);
                                        lastClickedMap.put(gregRect, System.currentTimeMillis());
                                    } else {
                                        ImageIO.write(square, "bmp", new File("images/" + UUID.randomUUID() + ".bmp"));
                                    }
                                }
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }

        {
            Thread.sleep(100);
            int x1 = antiIdleRect.x + 260;
            int y1 = antiIdleRect.y + 395;
            click(x1, y1);
            Thread.sleep(100);
        }
        //}

        //BufferedImage capture = robot.createScreenCapture(antiIdleRect);
        //ImageIO.write(capture, "bmp", new File("test.bmp"));


        //doShit();


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

    private static void click(int x1, int y1) {
        robot.mouseMove(x1, y1);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void magic() {
/*        NativeImageLoader loader = new NativeImageLoader(40, 40);
        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork("model.lol");
        INDArray image = loader.asRowVector("idiot.bmp");
        INDArray input = Nd4j.create(new int[]{1, 40*40*3}).addRowVector(image);
        input.putRow(0, image);
        int[] predict = model.predict(input);
        System.out.println(Arrays.toString(predict));*/
    }

    public static Rectangle getWindowForProcess(String windowName) {
        final Rectangle rect = new Rectangle();
        final User32 user32 = User32.INSTANCE;
        user32.EnumWindows(new WinUser.WNDENUMPROC() {

            public boolean callback(WinDef.HWND hWnd, Pointer arg) {
                byte[] windowText = new byte[512];
                user32.GetWindowTextA(hWnd, windowText, 512);
                String wText = Native.toString(windowText).trim();

                if (wText.equals(windowName)) {
                    try {
                        Rectangle rect1 = getRect(wText);
                        rect.setBounds(rect1.x, rect1.y, rect1.width, rect1.height);
                        System.out.printf("The corner locations for the window \"%s\" are %s", wText, rect1);
                    } catch (LoopWindowsPOC.WindowNotFoundException | LoopWindowsPOC.GetWindowRectException e) {
                        e.printStackTrace();
                    }
                    user32.SetForegroundWindow(hWnd);
                    return false;
                }

                return true;
            }
        }, null);

        return rect;
    }

    public static List<String> doShit() {
        final List<String> windowNames = new ArrayList<String>();
        final User32 user32 = User32.INSTANCE;
        user32.EnumWindows(new WinUser.WNDENUMPROC() {
            int count = 0;

            public boolean callback(WinDef.HWND hWnd, Pointer arg) {
                byte[] windowText = new byte[512];
                user32.GetWindowTextA(hWnd, windowText, 512);
                String windowName = Native.toString(windowText).trim();

                if (!windowName.isEmpty()) {
                    windowNames.add(windowName);
                    System.out.println("Found window with text " + hWnd + ", total " + ++count + " Text: " + windowName);
                    if (windowName.equals("Adobe Flash Player 9")) {
/*                        try {
                            int[] rect = getRect(windowName);
                            System.out.printf("The corner locations for the window \"%s\" are %s", windowName, Arrays.toString(rect));
                        } catch (LoopWindowsPOC.WindowNotFoundException | LoopWindowsPOC.GetWindowRectException e) {
                            e.printStackTrace();
                        }*/
                        user32.SetForegroundWindow(hWnd);
                        return false;
                    }
                    return true;
                }

                return true;
            }
        }, null);

        return windowNames;

    }

    public static Rectangle getRect(String windowName) throws LoopWindowsPOC.WindowNotFoundException,
            LoopWindowsPOC.GetWindowRectException {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, windowName);
        if (hwnd == null) {
            throw new LoopWindowsPOC.WindowNotFoundException("", windowName);
        }

        int[] rect = {0, 0, 0, 0};
        int result = User32.INSTANCE.GetWindowRect(hwnd, rect);
        if (result == 0) {
            throw new LoopWindowsPOC.GetWindowRectException(windowName);
        }
        return new Rectangle(rect[0], rect[1], rect[2] - rect[0], rect[3] - rect[1]);
    }

}
