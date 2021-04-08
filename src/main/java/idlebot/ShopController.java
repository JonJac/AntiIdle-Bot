package idlebot;

public class ShopController {
    private Game game;
    private static int PURCHASED_COLOR = 0xFF64FA00;
    private static int UNABLE_TO_BUY_GOLD_COLOR = 0xFF64FA00;
    private static int ABLE_TO_BUY_GOLD_COLOR = 0xFF95952F;

    public ShopController(Game game) {
        this.game = game;
    }

    private boolean buyIndex(int index) {
        if (index < 1 || index > 10) {
            throw new RuntimeException("Illegal shop attempt");
        }
        return false;
    }
}
