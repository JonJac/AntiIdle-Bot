package idlebot.arcade.balance;

import idlebot.Game;

import java.awt.*;
import java.awt.event.InputEvent;

import static idlebot.arcade.balance.BalanceConstants.START_BUTTON_X;
import static idlebot.arcade.balance.BalanceConstants.START_BUTTON_Y;

public class BalancePlayer {
    private Game game;

    public BalancePlayer(Game game) {
        this.game = game;
    }

    public void playBalance(){
        clickStart();
        //Problem: How to determine what the unknown sqaure was?


    }

    private void clickStart() {
        game.clickWithinGame(START_BUTTON_X, START_BUTTON_Y);
    }

}
