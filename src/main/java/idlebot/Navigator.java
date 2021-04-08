package idlebot;

import java.awt.event.KeyEvent;

public class Navigator {
    Game game;

    public Navigator(Game game) {
        this.game = game;
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

    public void clickWorld() {
        game.clickWithinGameWithUiUpdateDelay(587, 330);
    }

    public void clickAchievements() {
        game.clickWithinGameWithUiUpdateDelay(51, 638);
    }

    public void gotoGarden() {
        clickWorld();
        game.clickWithinGameWithUiUpdateDelay(472, 183);
    }

    public void gotoBattleArena() {
        clickWorld();
        game.clickWithinGameWithUiUpdateDelay(472, 211);
    }

    public void gotoButton() {
        clickWorld();
        game.clickWithinGameWithUiUpdateDelay(472, 234);
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
