package idlebot;

import java.awt.event.KeyEvent;

public class GameNavigator {
    private Game game;

    public GameNavigator(Game game) {
        this.game = game;
    }

    public void closeThingsLikeShop() {
        game.clickWithinGameWithUiUpdateDelay(648, 132);
    }

    public void clickShop() {
        game.clickWithinGameWithUiUpdateDelay(554, 639);
    }

    public void clickBoost() {
        game.clickWithinGameWithUiUpdateDelay(590, 505);
    }

    public void clickLeftAnt() {
        game.clickWithinGameWithUiUpdateDelay(574, 567);
    }

    public void clickRightAnt() {
        game.clickWithinGameWithUiUpdateDelay(640, 565);
    }

    public void clickBoostBar() {
        game.clickWithinGameWithUiUpdateDelay(587, 330);
    }

    public void clickProgressBar() {
        game.clickWithinGameWithUiUpdateDelay(586, 313);
    }

    public void clickWorld() {
        game.clickWithinGameWithUiUpdateDelay(467, 137);
        game.waitMs(100);
    }

    public void clickAchievements() {
        game.clickWithinGameWithUiUpdateDelay(51, 638);
    }

    public void gotoGarden() {
        clickWorld();
        game.clickWithinGameWithUiUpdateDelay(472, 183);
        clickNeutral();
    }

    public void gotoBattleArena() {
        clickWorld();
        game.clickWithinGameWithUiUpdateDelay(472, 211);
        clickNeutral();
    }

    private void clickNeutral() {
        game.clickWithinGameWithUiUpdateDelay(50, 50);
    }

    public void gotoButton() {
        clickWorld();
        game.clickWithinGameWithUiUpdateDelay(472, 234);
        clickNeutral();
    }

    private void clickArrowDownInWorldMenu() {
        game.clickWithinGameWithUiUpdateDelay(467, 457);
    }

    public void gotoDragon() {
        clickWorld();
        clickArrowDownInWorldMenu();
        clickArrowDownInWorldMenu();
        clickArrowDownInWorldMenu();
        clickArrowDownInWorldMenu();
        game.clickWithinGameWithUiUpdateDelay(467, 360);
        clickNeutral();
        game.waitMs(100); //scroll menu messing
    }

    public void konamiCodeInAchievements() {
        clickAchievements();
        game.pressKey(KeyEvent.VK_UP);
        game.pressKey(KeyEvent.VK_UP);
        game.pressKey(KeyEvent.VK_DOWN);
        game.pressKey(KeyEvent.VK_DOWN);
        game.pressKey(KeyEvent.VK_LEFT);
        game.pressKey(KeyEvent.VK_RIGHT);
        game.pressKey(KeyEvent.VK_LEFT);
        game.pressKey(KeyEvent.VK_RIGHT);
        game.pressKey(KeyEvent.VK_B);
        game.pressKey(KeyEvent.VK_A);
        clickAchievements();
    }
}
