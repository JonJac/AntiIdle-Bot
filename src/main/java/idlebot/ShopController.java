package idlebot;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ShopController {
    private Game game;
    private static int PURCHASED_COLOR = 0xFF64FA00;
    private static int UNABLE_TO_BUY_GOLD_COLOR = 0xFF64FA00;
    private static int ABLE_TO_BUY_GOLD_COLOR = 0xFF95952F;
    private static int BUY_BUTTON_X = 632;
    private static int BUY_BUTTON_Y = 166;
    private static int BUY_BUTTON_DISTANCE_TO_NEXT_Y = 45;

    public ShopController(Game game) {
        this.game = game;
    }

    private boolean buyIndex(int index) {
        if (index < 1 || index > 10) {
            throw new RuntimeException("Illegal shop attempt");
        }
        Point point = new Point(BUY_BUTTON_X, BUY_BUTTON_Y + (index - 1) * BUY_BUTTON_DISTANCE_TO_NEXT_Y);
        game.clickWithinGameWithUiUpdateDelay(point.x, point.y);
        game.waitMs(40);
        BufferedImage screenShotFullGame = game.screenShotFullGame();
        return screenShotFullGame.getRGB(point.x, point.y) == PURCHASED_COLOR;
    }

    private void navigateToShop(int index) {
        if (index < 1 || index > 4) {
            throw new RuntimeException("Illegal shop attempt");
        }
        game.clickWithinGameWithUiUpdateDelay(281 + (index - 1) * 100, 133);
    }

    public boolean buyIdleMode() {
        navigateToShop(1);
        return buyIndex(1);
    }

    public boolean buyBoostGenerator() {
        navigateToShop(1);
        return buyIndex(2);
    }

    public boolean buyGarden() {
        navigateToShop(1);
        return buyIndex(3);
    }

    public boolean buyBattleArena() {
        navigateToShop(1);
        return buyIndex(4);
    }

    public boolean buyButtonMachine() {
        navigateToShop(1);
        return buyIndex(5);
    }

    public boolean buyMoneyPrinter() {
        navigateToShop(1);
        return buyIndex(6);
    }

    public boolean buyArcade() {
        navigateToShop(1);
        return buyIndex(7);
    }

    //page2
    public boolean buyAutoBooster() {
        navigateToShop(2);
        return buyIndex(1);
    }

    public boolean buyMiniGarden() {
        navigateToShop(2);
        return buyIndex(5);
    }

    public boolean buyArcadePack() {
        navigateToShop(2);
        return buyIndex(8);
    }

}
