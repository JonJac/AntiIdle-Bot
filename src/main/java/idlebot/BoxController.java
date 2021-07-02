package idlebot;

public class BoxController {
    Game game;

    public BoxController(Game game) {
        this.game = game;
    }

    public void clickLegendaryBox(int times) {
        for (int i = 0; i < times; i++) {
            game.clickWithinGameWithUiUpdateDelay(351, 305);
        }
    }
}
