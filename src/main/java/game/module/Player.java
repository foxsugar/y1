package game.module;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import game.GameProcessor;
import game.Message.MessageHolder;
import game.db.DbProcessor;
import game.protobuf.DB;
import game.protobuf.PB;
import game.timer.ITimeHandler;
import game.timer.TimerNode;

import java.io.IOException;

/**
 * Created by sun on 2015/8/25.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
    public class Player {

    private long userId;
    private String name;
    private int gold;
    private int diamond;
    private int level;

    private Equip equip;
    private Pet pet;
    private Bag bag;

    public Player(){
        init();
        refreshPer5min();
    }

    private void init(){
        this.equip = new Equip();
        this.pet = new Pet();
        this.bag = new Bag();
    }


    public static Player getInstanceFromPB(DB.DbPlayerOrBuilder dbPlayer) throws IOException {
        Player player = new Player();
        player.userId = dbPlayer.getUserId();
        player.name = dbPlayer.getName();
        player.gold = dbPlayer.getGold();
        player.diamond = dbPlayer.getDiamond();
        player.equip.parseFromJson(player,dbPlayer.getEquip());
        return player;
    }


    /**
     * 发给db
     * 更新玩家数据
     */
    private void send2DbUpdate(){

        DB.S2DMessage.Builder s2d = DB.S2DMessage.newBuilder();

        try {
            s2d.setUpdatePlayer(getDbPlayer(this));
            s2d.setMessageId(PB.MessageId.S2D_UPDATEPLAYER);
            sendMsg2Db(s2d);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过player 得到dbPlayer
     * @param player
     * @return
     * @throws JsonProcessingException
     */
    public DB.DbPlayer.Builder getDbPlayer(Player player) throws JsonProcessingException {
        return DB.DbPlayer.newBuilder().setName(player.getName())
                .setUserId(player.getUserId())
                .setGold(player.getGold())
                .setDiamond(player.getDiamond())
                .setLevel(player.getLevel())
                .setEquip(player.getEquip().convert2Json())
                .setPet(player.getPet().convert2Json());
    }

    /**
     * 给db发消息
     * @param s2d
     */
    private void sendMsg2Db(DB.S2DMessageOrBuilder s2d){
        MessageHolder holder = new MessageHolder();
        holder.message = s2d;
        DbProcessor.getInstance().messages.add(holder);
    }

    private void refreshPer5min(){
        TimerNode node = new TimerNode(System.currentTimeMillis(), 1000, true, new ITimeHandler() {
            @Override
            public void fire() {
                send2DbUpdate();
            }
        });
        GameProcessor.getInstance().timer.addTimerNode(node);

    }










    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Equip getEquip() {
        return equip;
    }

    public void setEquip(Equip equip) {
        this.equip = equip;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Bag getBag() {
        return bag;
    }

    public void setBag(Bag bag) {
        this.bag = bag;
    }
}
