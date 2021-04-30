package idlebot.arcade.mindsweeper;

import idlebot.Game;

import java.awt.image.BufferedImage;

public class MindSweeperPlayer {
    private Game game;

    public MindSweeperPlayer(Game game) {
        this.game = game;
    }

    public void play() {
        BufferedImage gameArea = game.screenShot(29, 132, 480, 240);

    }
}
