package idlebot.arcade;

import idlebot.Game;

public class ArcadeNavigator {
    private Game game;

    public ArcadeNavigator(Game game) {
        this.game = game;
    }

    public void clickHigherDifficulty() {
        game.clickWithinGameWithUiUpdateDelay(259, 431);
    }

    public void clickLowerDifficulty() {
        game.clickWithinGameWithUiUpdateDelay(138, 431);
    }

    public void clickPlay() {
        game.clickWithinGameWithUiUpdateDelay(70, 375);
    }

    public void clickBack() {
        game.clickWithinGameWithUiUpdateDelay(252, 391);
    }

    public void clickWag() {
        game.clickWithinGameWithUiUpdateDelay(182, 265);
    }

    public void clickMmrx() {
        game.clickWithinGameWithUiUpdateDelay(182, 342);
    }

    public void clickBalance3() {
        game.clickWithinGameWithUiUpdateDelay(182, 323);
    }

    public void clickMindSweeper() {
        game.clickWithinGameWithUiUpdateDelay(182, 323);
    }

    public void buyTokens() {
        clickShop();
        click50TokensWithinShop();
        clickShop();
    }

    public void buyBonusExp() {
        clickShop();
        click2xExpWithinShop();
        clickShop();
    }

    public void clickShop() {
        game.clickWithinGameWithUiUpdateDelay(267, 434);
    }

    public void click2xExpWithinShop() {
        game.clickWithinGameWithUiUpdateDelay(435, 250);
    }

    public void click50TokensWithinShop() {
        game.clickWithinGameWithUiUpdateDelay(435, 305);
    }
}
