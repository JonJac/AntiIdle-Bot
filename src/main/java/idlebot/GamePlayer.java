package idlebot;

import idlebot.arcade.ArcadeNavigator;
import idlebot.arcade.avoidance.AvoidancePlayer;
import idlebot.arcade.balance.BalancePlayer;
import idlebot.arcade.whackagreg.WhackAGregPlayerThreaded;
import idlebot.battle.arena.BattleArenaPlayer;
import idlebot.botton.ButtonPlayer;
import idlebot.fishing.FishingPlayer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class GamePlayer {
    private Game game;

    private AvoidancePlayer avoidancePlayer;
    private BalancePlayer balancePlayer;
    private WhackAGregPlayerThreaded whackAGregPlayer;

    private ButtonPlayer buttonPlayer;
    private FishingPlayer fishingPlayer;
    private BattleArenaPlayer battleArenaPlayer;

    private BoostController boostController;
    private DragonController dragonController;
    private GameNavigator gameNavigator;
    private ProgressController progressController;
    private ShopController shopController;
    private ArcadeNavigator arcadeNavigator;
    private CardsController cardsController;

    public GamePlayer(Game game, AvoidancePlayer avoidancePlayer, BalancePlayer balancePlayer, WhackAGregPlayerThreaded whackAGregPlayer, ButtonPlayer buttonPlayer, FishingPlayer fishingPlayer, BattleArenaPlayer battleArenaPlayer, BoostController boostController, DragonController dragonController, GameNavigator gameNavigator, ProgressController progressController, ShopController shopController, ArcadeNavigator arcadeNavigator, CardsController cardsController) {
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
        this.arcadeNavigator = arcadeNavigator;
        this.cardsController = cardsController;
    }

    public void play() {
/*        clickStart();
        playUntilButtonBought();
        playUntilArcadeBought();
        playUntilExpCardActivated();*/
/*        if(resetGameIfNoExpCard()){
            play();
        }*/
        //playUntil6WagRating();
        //playUntilAnotherIsUnlockedInArcade();

        gameNavigator.gotoArcade();
        arcadeNavigator.clickBalance3();
        arcadeNavigator.clickHigherDifficulty();

        ArrayList<Integer> scores = new ArrayList<>();
        ArrayList<Long> times = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            long time = System.currentTimeMillis();
            int blocksPlaces = balancePlayer.playBalance(false);
            long timeSpent = System.currentTimeMillis() - time;
            times.add(timeSpent);
            scores.add(blocksPlaces);
            System.out.println("score:" + scores);
            System.out.println("time in ms:" + times);
            game.clickWithinGameWithUiUpdateDelay(252, 391);
            if (i % 10 == 0) {
                arcadeNavigator.clickBack();
                arcadeNavigator.buyTokens();
                arcadeNavigator.buyBonusExp();
                arcadeNavigator.clickBalance3();
            }

            if (i % 3 == 0) {
                upgradeAllTheThings();
            }
        }
        System.out.println(scores);
        System.out.println("Avg score: " + (scores.stream().reduce(Integer::sum).get() / scores.size()));
        System.out.println("Avg time: " + (times.stream().reduce(Long::sum).get() / times.size()));

    }

    private void playUntilAnotherIsUnlockedInArcade() {
        gameNavigator.gotoArcade();
        arcadeNavigator.clickBalance3();
        arcadeNavigator.clickHigherDifficulty();
        balancePlayer.playBalance(false);
        game.clickWithinGameWithUiUpdateDelay(252, 391);
        balancePlayer.playBalance(false);
        game.clickWithinGameWithUiUpdateDelay(252, 391);
        arcadeNavigator.clickHigherDifficulty();
    }

    private void playUntil6WagRating() {
        upgradeProgressBar(20);
        upgradeBoost(3);
        gameNavigator.gotoArcade();
        arcadeNavigator.clickWag();
        whackAGregPlayer.playWAG(100);
        arcadeNavigator.clickHigherDifficulty();
        whackAGregPlayer.playWAG(350);
        upgradeBoost(3);
        arcadeNavigator.buyBonusExp();
        whackAGregPlayer.playWAG(350);
        upgradeBoost(3);
        arcadeNavigator.buyTokens();
        whackAGregPlayer.playWAG(350);
        upgradeBoost(3);
    }

    private void upgradeAllTheThings() {
        upgradeProgressBar(20);
        upgradeBoost(3);

        //TODO: Dragon
        //TODO: Business
        //TODO: Epic skills
        //TODO: Career
        //TODO: Rested
    }

    private boolean resetGameIfNoExpCard() {
        if (!gameNavigator.isExpCardActivated()) {
            System.out.println("No exp card");
            gameNavigator.clickOption();
            gameNavigator.resetSpeedrun();
            return true;
        }
        return false;
    }

    private void playUntilExpCardActivated() {
        gameNavigator.gotoArcade();
        arcadeNavigator.clickWag();
        arcadeNavigator.clickHigherDifficulty();
        whackAGregPlayer.playWAG(100);
        buyAutoBooster();
        gameNavigator.gotoDragon();
        dragonController.clickLevelUpReward();
        gameNavigator.gotoCards();
        cardsController.clickExpCard();
        cardsController.clickCoinCard();
        cardsController.clickIdleBotCard();
    }

    private void playUntilArcadeBought() {
        playButton(500);
        while (!buyArcade()) {
            playButton(100);
        }
        upgradeProgressBar(70);
        upgradeBoost(10);

    }

    private boolean buyArcade() {
        gameNavigator.clickShop();
        boolean bought = shopController.buyArcade();
        if (!bought) {
            gameNavigator.closeThingsLikeShop();
            return false;
        }
        shopController.buyArcadePack();
        gameNavigator.closeThingsLikeShop();
        return true;
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

    private void playButton(int clicks) {
        gameNavigator.gotoButton();
        try {
            buttonPlayer.play(clicks);
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
