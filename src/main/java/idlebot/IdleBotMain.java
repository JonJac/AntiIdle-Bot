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
import idlebot.botton.ButtonPlayer;
import ml.GregClassifier;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.imageio.ImageIO;

public class IdleBotMain {
    public static void main(String[] args) throws AWTException, IOException, InterruptedException {
        Rectangle antiIdleRect = getWindowForProcess("Adobe Flash Player 9");

        System.out.println("AntiIdleRect: " + antiIdleRect);
        Game game = new Game(antiIdleRect);
        game.clickWithinGame(10,34); //click so there is something to screenshot
        game.screenshotGame("test.bmp");

        ButtonPlayer player = new ButtonPlayer(game);
        player.play();
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
