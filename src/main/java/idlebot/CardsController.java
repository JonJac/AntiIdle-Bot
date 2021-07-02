package idlebot;

public class CardsController {
    Game game;

    public CardsController(Game game) {
        this.game = game;
    }

    public void clickExpCard() {
        game.clickWithinGameWithUiUpdateDelay(217, 150);
    }

    public void clickCoinCard() {
        game.clickWithinGameWithUiUpdateDelay(217, 205);
    }

    public void clickIdleBotCard() {
        game.clickWithinGameWithUiUpdateDelay(471, 205);
    }
}
