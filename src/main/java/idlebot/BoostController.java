package idlebot;

public class BoostController {
    private Game game;

    public BoostController(Game game) {
        this.game = game;
    }

    public void clickFreeRefill() {
        game.clickWithinGameWithUiUpdateDelay(128, 289);
    }

    public void clickUpgradeBoost() {
        game.clickWithinGameWithUiUpdateDelay(360, 464);
    }

    public void closeBoost() {
        game.clickWithinGameWithUiUpdateDelay(647, 130);
    }
}
