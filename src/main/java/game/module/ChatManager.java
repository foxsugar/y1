package game.module;

import game.protobuf.PB;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by sun on 2015/8/26.
 */
public class ChatManager {
    private ChatManager(){}

    public static ChatManager instance;

    public static ChatManager getInstance(){
        if(instance == null){
            instance = new ChatManager();
        }
        return instance;
    }

    public void sendChat(PB.C2SMessageOrBuilder c2s,ChannelHandlerContext ctx){
        int channel = c2s.getSendChat().getChannel();
        String text = c2s.getSendChat().getText();

        PB.S2CMessage.Builder s2c = PB.S2CMessage.newBuilder().setMessageId(PB.MessageId.S2C_REVIEVECHAT);
        s2c.setRecieveChat(PB.S2CRecieveChat.newBuilder().setChannel(1).setText(text));
        for(ChannelHandlerContext channelHandlerContext : GameManager.getInstance().ctxSet){
            GameManager.getInstance().sendMsg2Client(channelHandlerContext,s2c.build());
        }
    }
}
