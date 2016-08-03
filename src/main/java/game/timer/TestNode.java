package game.timer;

import game.module.Player;

/**
 * Created by sun on 2015/8/10.
 */
public class TestNode implements ITimeHandler {

    private Player player;

    public TestNode(Player player) {
        this.player = player;
    }

    @Override
    public void fire() {
        System.out.println("========timer ===  player id ===  "+player.getUserId());
    }
}
