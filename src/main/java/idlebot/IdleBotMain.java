package idlebot;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import idlebot.arcade.ArcadeNavigator;
import idlebot.arcade.avoidance.AvoidancePlayer;
import idlebot.arcade.balance.BalancePlayer;
import idlebot.arcade.mmrx.MmrXPlayer;
import idlebot.arcade.whackagreg.ScreenShotContainer;
import idlebot.arcade.whackagreg.WhackAGregConstants;
import idlebot.arcade.whackagreg.WhackAGregPlayer;
import idlebot.arcade.whackagreg.WhackAGregPlayerThreaded;
import idlebot.battle.arena.BattleArenaPlayer;
import idlebot.botton.ButtonPlayer;
import idlebot.fishing.FishingPlayer;

import static java.lang.Thread.MAX_PRIORITY;

public class IdleBotMain {
    private static int expectedWidth = 690;
    private static int expectedHeight = 740;

    public static void main(String[] args) throws AWTException, IOException, InterruptedException {
        Rectangle antiIdleRect = getWindowForProcess("Adobe Flash Player 9");
        if (antiIdleRect.height == 0) {
            System.out.println("could not find game window");
            return;
        }

        System.out.println("AntiIdleRect: " + antiIdleRect);
        Game game = new Game(antiIdleRect);
        game.clickWithinGame(10, 34); //click so there is something to screenshot
        game.screenshotGame("test.bmp");

        GamePlayer gamePlayer = createGamePlayer(game);
        //gamePlayer.play();

        //System.out.println(String.format("0x%08X", -220392));

        ArcadeNavigator arcadeNavigator = new ArcadeNavigator(game);
        MmrXPlayer mmrXPlayer = new MmrXPlayer(game);
        arcadeNavigator.clickPlay();
        mmrXPlayer.play();
        arcadeNavigator.clickBack();

        //playGreg(antiIdleRect, game);

/*        BalancePlayer balancePlayer = new BalancePlayer(game);
        for (int i = 0; i < 20; i++) {
            balancePlayer.playBalance(false);
            game.clickWithinGameWithUiUpdateDelay(252, 391);
        }*/

    }

    private static void playGreg(Rectangle antiIdleRect, Game game) throws InterruptedException {
        ScreenShotContainer screenShotContainer = new ScreenShotContainer();
        Thread screenThread = new Thread(() -> screenShotContainer.startScreenCapturing(new Rectangle(antiIdleRect.x + WhackAGregConstants.RELATIVE_TOP_LEFT_X, antiIdleRect.y + WhackAGregConstants.RELATIVE_TOP_LEFT_Y, WhackAGregConstants.SQUARE_WIDTH * WhackAGregConstants.SQUARE_X_AMOUNT, WhackAGregConstants.SQUARE_HEIGHT * WhackAGregConstants.SQUARE_Y_AMOUNT)));
        screenThread.setPriority(MAX_PRIORITY);
        screenThread.start();

        WhackAGregPlayerThreaded whackAGregPlayer = new WhackAGregPlayerThreaded(game);
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            final int column = i;
            threads.add(new Thread(() -> whackAGregPlayer.playAGameOfGreg(antiIdleRect, column, screenShotContainer)));
        }
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }
    }

    private static void oldShit(Rectangle antiIdleRect, Game game) throws IOException, InterruptedException {
        playGreg(antiIdleRect, game);


        ButtonPlayer buttonPlayer = new ButtonPlayer(game);
        for (int i = 0; i < 5000; i++) {
            buttonPlayer.play();
        }

        AvoidancePlayer avoidancePlayer = new AvoidancePlayer(game);
        avoidancePlayer.play();

        FishingPlayer fishingPlayer = new FishingPlayer(game);
        fishingPlayer.play();

        WhackAGregPlayer whackAGregPlayer = new WhackAGregPlayer(game);
        for (int i = 0; i < 0; i++) {
            whackAGregPlayer.playAGameOfGreg(antiIdleRect);
        }

        BalancePlayer balancePlayer = new BalancePlayer(game);
        for (int i = 0; i < 500; i++) {
            balancePlayer.playBalance(false);
            game.clickWithinGameWithUiUpdateDelay(252, 391);
        }

        ArcadeNavigator arcadeNavigator = new ArcadeNavigator(game);
        MmrXPlayer mmrXPlayer = new MmrXPlayer(game);
        arcadeNavigator.clickPlay();
        mmrXPlayer.play();
        arcadeNavigator.clickBack();

    }

    private static GamePlayer createGamePlayer(Game game) {
        return new GamePlayer(game, new AvoidancePlayer(game), new BalancePlayer(game), new WhackAGregPlayer(game), new ButtonPlayer(game), new FishingPlayer(game), new BattleArenaPlayer(game), new BoostController(game), new DragonController(game),
                new GameNavigator(game), new ProgressController(game), new ShopController(game));
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
                        if (rect1.width > expectedWidth || rect1.height > expectedHeight) {
                            System.out.println("Found flashplayer but width and height were wrong.");
                            return true;
                        }
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
