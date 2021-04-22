package idlebot.battle.arena;

import idlebot.Game;

public class BattleArenaPlayer {
    private Game game;

    public BattleArenaPlayer(Game game) {
        this.game = game;
    }

    public void goRight() {
        game.clickWithinGameWithUiUpdateDelay(505, 302);
    }

    public void enableAutoFight() {
        game.clickWithinGameWithUiUpdateDelay(289, 417);
    }
}
