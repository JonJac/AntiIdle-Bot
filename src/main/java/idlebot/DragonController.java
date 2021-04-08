package idlebot;

public class DragonController {
    Game game;

    public DragonController(Game game) {
        this.game = game;
    }

    public void clickLevelUpReward() {
        game.clickWithinGameWithUiUpdateDelay(78, 367);
    }

    public void clickFeed() {
        game.clickWithinGameWithUiUpdateDelay(82, 431);
    }
}
