package idlebot;

import idlebot.arcade.avoidance.AvoidancePlayer;
import idlebot.arcade.balance.BalancePlayer;
import idlebot.arcade.whackagreg.WhackAGregPlayer;
import idlebot.battle.arena.BattleArenaPlayer;
import idlebot.botton.ButtonPlayer;
import idlebot.fishing.FishingPlayer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class GamePlayer {
    private Game game;

    private AvoidancePlayer avoidancePlayer;
    private BalancePlayer balancePlayer;
    private WhackAGregPlayer whackAGregPlayer;

    private ButtonPlayer buttonPlayer;
    private FishingPlayer fishingPlayer;
    private BattleArenaPlayer battleArenaPlayer;

    private BoostController boostController;
    private DragonController dragonController;
    private GameNavigator gameNavigator;
    private ProgressController progressController;
    private ShopController shopController;

    public GamePlayer(Game game, AvoidancePlayer avoidancePlayer, BalancePlayer balancePlayer, WhackAGregPlayer whackAGregPlayer, ButtonPlayer buttonPlayer, FishingPlayer fishingPlayer, BattleArenaPlayer battleArenaPlayer, BoostController boostController, DragonController dragonController, GameNavigator gameNavigator, ProgressController progressController, ShopController shopController) {
        this.game = game;
        this.avoidancePlayer = avoidancePlayer;
        this.balancePlayer = balancePlayer;
        this.whackAGregPlayer = whackAGregPlayer;
        this.buttonPlayer = buttonPlayer;
        this.fishingPlayer = fishingPlayer;
        this.battleArenaPlayer = battleArenaPlayer;
        this.boostController = boostController;
        this.dragonController = dragonController;
        this.gameNavigator = gameNavigator;
        this.progressController = progressController;
        this.shopController = shopController;
    }

    public void play() {
        clickStart();
        //playUntilButtonBought();

        for (int i = 0; i < 1; i++) {
            playButton();
            buyAutoBooster();
            upgradeProgressBar(10);
            upgradeBoost(3);
        }
    }

    private void upgradeBoost(int amount) {
        gameNavigator.clickBoost();
        for (int i = 0; i < amount; i++) {
            boostController.clickUpgradeBoost();
        }
        boostController.closeBoost();
    }

    private void buyAutoBooster() {
        gameNavigator.clickShop();
        shopController.buyAutoBooster();
        gameNavigator.closeThingsLikeShop();
    }

    private void playUntilButtonBought() {
        clickUntilShopIsAvailable();
        buyIdleModeAndUpgradeProgressBar();
        gameNavigator.konamiCodeInAchievements();
        getToBoostGeneratorAndBoost();
        getToBattleArena();
        getToButton();
    }

    private void buyIdleModeAndUpgradeProgressBar() {
        gameNavigator.clickShop();
        shopController.buyIdleMode();
        gameNavigator.closeThingsLikeShop();
        upgradeProgressBar(50);
    }

    private void upgradeProgressBar(int amount) {
        gameNavigator.clickProgressBar();
        for (int i = 0; i < amount; i++) {
            progressController.clickUpgradeIdleModeOff();
        }
        progressController.clickCloseMenu();
    }

    private void playButton() {
        gameNavigator.gotoButton();
        try {
            buttonPlayer.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getToButton() {
        gameNavigator.clickShop();
        while (!shopController.buyButtonMachine()) {
        }
        gameNavigator.closeThingsLikeShop();

        //click first button once
        gameNavigator.gotoButton();
        game.clickWithinGameWithUiUpdateDelay(260, 260);
        gameNavigator.gotoDragon();
    }

    private void getToBattleArena() {
        gameNavigator.clickShop();
        while (!shopController.buyBoostGenerator()) {
        }
        while (!shopController.buyBattleArena()) {
        }
        gameNavigator.closeThingsLikeShop();
        gameNavigator.gotoBattleArena();
        battleArenaPlayer.enableAutoFight();
        battleArenaPlayer.goRight();
    }

    private void getToBoostGeneratorAndBoost() {
        startGame();
        while (!shopController.buyBoostGenerator()) {
        }
        gameNavigator.closeThingsLikeShop();
        gameNavigator.clickBoost();
        boostController.clickFreeRefill();
        boostController.closeBoost();
    }

    private void startGame() {
        gameNavigator.clickShop();
        game.waitMs(3000); // IO
    }

    private void clickUntilShopIsAvailable() {
        for (int i = 0; i < 1000; i++) {
            game.holdShiftDown();
            game.releaseShift();
            BufferedImage bufferedImage = game.screenShotFullGame();
            if (bufferedImage.getRGB(573, 638) == 0xFFAF7C2F) {
                return;
            }
            game.waitMs(30);
        }

        System.out.println("Never found working shop");
    }

    private void clickStart() {
        game.clickWithinGameWithUiUpdateDelay(145, 652);
    }
}
