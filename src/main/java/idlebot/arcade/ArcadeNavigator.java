package idlebot.arcade;

import idlebot.Game;

public class ArcadeNavigator {
    private Game game;

    public ArcadeNavigator(Game game) {
        this.game = game;
    }

    public void clickPlay() {
        game.clickWithinGameWithUiUpdateDelay(70, 375);
    }

    public void clickBack() {
        game.clickWithinGameWithUiUpdateDelay(252, 391);
    }
}
