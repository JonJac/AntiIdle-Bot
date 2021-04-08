package idlebot;

public class ProgressController {
    Game game;

    public ProgressController(Game game) {
        this.game = game;
    }

    public void clickUpgradeIdleModeOn() {
        game.clickWithinGameWithUiUpdateDelay(374, 201);
    }

    public void clickUpgradeIdleModeOff() {
        game.clickWithinGameWithUiUpdateDelay(374, 232);
    }

    public void clickCloseMenu() {
        game.clickWithinGameWithUiUpdateDelay(648, 133);
    }
}
