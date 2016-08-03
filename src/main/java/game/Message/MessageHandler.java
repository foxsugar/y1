package game.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.googlecode.protobuf.format.JsonFormat;
import game.db.DbProcessor;
import game.module.ChatManager;
import game.module.GameManager;
import game.module.Player;
import game.protobuf.DB;
import game.protobuf.PB;
import game.utils.IdWorker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.io.IOException;

/**
 * Created by sun on 2015/8/21.
 */
public class MessageHandler {

    private static AttributeKey<Long> attributeKey = AttributeKey.newInstance("userId");
    private IdWorker idWorker = new IdWorker(1,1);

    public void handleMessage(MessageHolder msgHolder){
        System.out.println("000000000000000000000");
        //do
//        System.out.println(messHolder.content.toString());
        Object message = msgHolder.message;

        if(message instanceof PB.C2SMessageOrBuilder){
            System.out.println(JsonFormat.printToString((PB.C2SMessage)message));
            handleC2SMessage((PB.C2SMessage) message, msgHolder.ctx);
        }else if(message instanceof DB.D2SMessageOrBuilder){
            System.out.println("d2s Message");
            handleD2SMessage(msgHolder);
        }else{
            System.out.println(" other message");
        }
    }

    /**
     * 处理客户端发来的消息
     * @param message message
     * @param ctx ctx
     */
    private void handleC2SMessage(PB.C2SMessage message,ChannelHandlerContext ctx){
        PB.MessageId messageId = message.getMessageId();

        System.out.println(messageId.toString());
        if(ctx.attr(attributeKey).get()!=null){

            long uId  = ctx.attr(attributeKey).get();
            Player player = GameManager.getInstance().players.get(uId);
        }

        switch (messageId){
            case C2S_REGISTER:
                handleC2SRegister(message,ctx);
                break;
            case C2S_LOGIN:
                handleC2SLogin(message,ctx);
                break;
            case C2S_SENDCHAT:
                System.out.println("---------------");
                ChatManager.getInstance().sendChat(message, ctx);
                break;
            default:
                break;

        }
    }

    private void handleD2SMessage(MessageHolder msgHolder){
        DB.D2SMessageOrBuilder msg = (DB.D2SMessageOrBuilder)msgHolder.message;
        PB.MessageId messageId = msg.getMessageId();

        switch (messageId){
            case D2S_LOGINRESULT:
                handleD2S_LoginResult(msg, msgHolder);
                break;
            default:
                break;
        }
    }


    private void handleC2SRegister(PB.C2SMessage message,ChannelHandlerContext ctx){
        //名字是否冲突
        String name = message.getRegister().getName();
        boolean isHas = GameManager.getInstance().name_idMap.containsKey(name);
        if(isHas){
            //通知客户端名字冲突
        }else{
            //注册成功
            //生成player对象
            Player player = new Player();
            player.setName(name);
            player.setUserId(idWorker.nextId());

            //通知db
            DB.S2DMessage.Builder s2d = DB.S2DMessage.newBuilder();
            s2d.setMessageId(PB.MessageId.S2D_REGISTER);
            try {
                s2d.setRegister(getDbPlayer(player));

                MessageHolder sendHolder = new MessageHolder();
                sendHolder.message = s2d;
                sendHolder.ctx = ctx;
                DbProcessor.getInstance().messages.add(sendHolder);

                //关联 id ctx
                ctx.attr(attributeKey).set(player.getUserId());
                GameManager gameManager = GameManager.getInstance();
                gameManager.players.put(player.getUserId(),player);
                gameManager.id_nameMap.put(player.getUserId(),player.getName());
                gameManager.name_idMap.put(player.getName(),player.getUserId());
                gameManager.ctxs.put(player.getUserId(),ctx);
                gameManager.ctxSet.add(ctx);


            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }


        }
    }

    private DB.DbPlayer.Builder getDbPlayer(Player player) throws JsonProcessingException {
        return DB.DbPlayer.newBuilder().setName(player.getName())
                .setUserId(player.getUserId())
                .setEquip(player.getEquip().convert2Json()).setPet(player.getPet().convert2Json());
    }


    /**
     * 处理 登陆消息
     * @param message msg
     * @param ctx ctx
     */
    private void handleC2SLogin(PB.C2SMessage message,ChannelHandlerContext ctx){
        MessageHolder sendHolder = new MessageHolder();
        long userId = message.getLogin().getUserId();
        String pass = message.getLogin().getPasswd();

        DB.S2DMessage.Builder s2d = DB.S2DMessage.newBuilder();
        DB.S2DLogin.Builder login = DB.S2DLogin.newBuilder();
        login.setUserId(userId);
        s2d.setMessageId(PB.MessageId.S2D_LOGIN);
        s2d.setS2DLogin(login);
        sendHolder.message = s2d;
        sendHolder.ctx = ctx;
        DbProcessor.getInstance().messages.add(sendHolder);
    }

    /**
     * 处理db传来的登陆结果
     * @param msg
     * @param msgHolder
     */
    private void handleD2S_LoginResult( DB.D2SMessageOrBuilder msg,MessageHolder msgHolder){
        PB.S2CLoginResult.Builder loginResult = PB.S2CLoginResult.newBuilder();
        boolean flag = msg.getLoginResult().getIsHas();
        //登陆成功
        if (flag) {
            //将ctx和player关联起来


            loginResult.setResult(true);
            try {
                Player player = Player.getInstanceFromPB(msg.getLoginResult().getPlayerDb());

                //添加进map
                GameManager.getInstance().players.put(player.getUserId(), player);
                //userId 放进ctx
                msgHolder.ctx.attr(attributeKey).set(player.getUserId());
                //userId ctx 映射
                GameManager.getInstance().ctxs.put(player.getUserId(),msgHolder.ctx);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }else{
            loginResult.setResult(false);
        }

        //登陆成功
        GameManager.getInstance().ctxSet.add(msgHolder.ctx);
        //给客户端发消息
        ChannelHandlerContext ctx = msgHolder.ctx;
        PB.S2CMessage.Builder s2c = PB.S2CMessage.newBuilder();
        s2c.setMessageId(PB.MessageId.S2C_LOGIN);
        s2c.setLoginResult(loginResult);
        GameManager.getInstance().sendMsg2Client(ctx, s2c.build());
    }



}
