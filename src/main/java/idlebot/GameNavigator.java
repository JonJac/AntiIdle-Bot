package idlebot;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static java.awt.event.KeyEvent.VK_1;

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

    public void clickOption() {
        game.clickWithinGameWithUiUpdateDelay(241, 678);
    }

    public void resetSpeedrun() {
        game.holdShiftDown();
        game.holdKey(VK_1);
        game.waitMs(30);
        game.releaseKey(VK_1);
        game.releaseShift();

        String text = "Yes, I want to end the speedrun.";
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);

        //copy paste
        game.rightClickWithinGameWithUiUpdateDelay(346, 313);
        game.clickWithinGameWithUiUpdateDelay(414, 370);

        //go back to speedrun challenge screen
        game.clickWithinGameWithUiUpdateDelay(585, 530);
        //click left arrow in challenge selection
        for (int i = 0; i < 6; i++) {
            game.clickWithinGameWithUiUpdateDelay(55, 612);
        }
    }

    public void clickWorld() {
        game.clickWithinGameWithUiUpdateDelay(467, 137);
        game.waitMs(200);
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

    public void gotoArcade() {
        clickWorld();
        game.clickWithinGameWithUiUpdateDelay(472, 285);
        clickNeutral();
    }

    private void clickArrowDownInWorldMenu() {
        game.clickWithinGameWithUiUpdateDelay(467, 457);
        game.waitMs(100);
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

    public void gotoMysteryBox() {
        clickWorld();
        clickArrowDownInWorldMenu();
        clickArrowDownInWorldMenu();
        clickArrowDownInWorldMenu();
        clickArrowDownInWorldMenu();
        game.clickWithinGameWithUiUpdateDelay(467, 385);
        clickNeutral();
        game.waitMs(100); //scroll menu messing
    }

    public void gotoCards() {
        clickWorld();
        clickArrowDownInWorldMenu();
        clickArrowDownInWorldMenu();
        clickArrowDownInWorldMenu();
        clickArrowDownInWorldMenu();
        game.clickWithinGameWithUiUpdateDelay(467, 410);
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

    public boolean isExpCardActivated() {
        BufferedImage bufferedImage = game.screenShot(562, 587, 1, 1);
        return bufferedImage.getRGB(0, 0) == 0xFF01FF01;
    }

    public boolean isCoinCardActivated() {
        BufferedImage bufferedImage = game.screenShot(603, 587, 1, 1);
        return bufferedImage.getRGB(0, 0) == 0xFFFBFF01;
    }

    public void clickCareer() {
        game.clickWithinGameWithUiUpdateDelay(302, 669);
    }

    public void clickPet() {
        game.clickWithinGameWithUiUpdateDelay(360, 669);
    }

    public boolean isCareersAvailable() {
        BufferedImage bufferedImage = game.screenShot(302, 669, 1, 1);
        return bufferedImage.getRGB(0, 0) != 0xFF1A2836;
    }

    public boolean isPetAvailable() {
        BufferedImage bufferedImage = game.screenShot(360, 669, 1, 1);
        return bufferedImage.getRGB(0, 0) != 0xFF1A2836;
    }
}
