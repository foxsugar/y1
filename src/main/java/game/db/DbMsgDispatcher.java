package game.db;

import game.GameProcessor;
import game.Message.MessageHolder;
import game.protobuf.DB;
import game.utils.ProperitesUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Timer;

import static game.protobuf.PB.*;

/**
 * Created by sun on 2015/8/25.
 */
public class DbMsgDispatcher {

    private static DbMsgDispatcher instance;

    public Connection conn;

    public QueryRunner queryRunner = new QueryRunner();

    private DbMsgDispatcher(){
        init();
    }

    public static DbMsgDispatcher getInstance(){
        if(instance == null){
            instance = new DbMsgDispatcher();
        }
        return instance;
    }



    private void init() {
        conn = DbManager.getConn();
    }

    public void handle(MessageHolder holder) {
        if (holder.message instanceof DB.S2DMessageOrBuilder) {
            DB.S2DMessageOrBuilder msg = (DB.S2DMessageOrBuilder) holder.message;
            long userId = msg.getS2DLogin().getUserId();

            switch (msg.getMessageId()) {
                case S2D_LOGIN:
                    handleLogin(msg,holder);
                    break;
                case S2D_REGISTER:
                    handleRegister(msg,holder);
                    break;
                case S2D_UPDATEPLAYER:
                    handleUpdatePlayer(msg);
                    break;
            }
        }
    }


    private void handleLogin(DB.S2DMessageOrBuilder msg,MessageHolder holder){
        DB.D2SMessage.Builder d2s = DB.D2SMessage.newBuilder().setMessageId(MessageId.D2S_LOGINRESULT);
        final DB.D2SLoginResult.Builder result = DB.D2SLoginResult.newBuilder();
        final DB.DbPlayer.Builder playerDb = DB.DbPlayer.newBuilder();
        long userId = msg.getS2DLogin().getUserId();
        String sql = "select * from user_base where user_id = ?";
        ResultSetHandler rsh = new ResultSetHandler() {
            @Override
            public DB.D2SLoginResult.Builder handle(ResultSet rs) throws SQLException {
                if(rs.first()){
                    result.setIsHas(true);
                    playerDb.setUserId(rs.getInt("user_id"));
                    playerDb.setName(rs.getString("name"));
                    playerDb.setLevel(rs.getInt("level"));
                    playerDb.setGold(rs.getInt("gold"));
                    playerDb.setDiamond(rs.getInt("diamond"));
                    result.setPlayerDb(playerDb);
                }else {
                    result.setIsHas(false);
                }
                return result;
            }
        };

        try {
             queryRunner.query(conn, sql, rsh, userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        d2s.setLoginResult(result);
        MessageHolder h = new MessageHolder();
        h.message = d2s;
        h.ctx = holder.ctx;
        GameProcessor.getInstance().messageQueue.add(h);
    }

    private void handleRegister(DB.S2DMessageOrBuilder s2d, MessageHolder holder){
        DB.DbPlayer player = s2d.getRegister();
        String sql = "INSERT INTO user_base (user_id,name,level,gold,diamond,equip,pet) VALUES (?,?,?,?,?,?,?);";
        try {
            queryRunner.update(conn,sql,player.getUserId(),player.getName(),player.getLevel(),player.getGold(),player.getDiamond(),player.getEquip(),player.getPet());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleUpdatePlayer(DB.S2DMessageOrBuilder s2d){
        DB.DbPlayer player = s2d.getUpdatePlayer();
        String sql = "update user_base set name=?,level=?,gold=?,diamond=?,equip=?,pet=? where user_id=?";
        try {
            queryRunner.update(conn,sql,player.getName(),player.getLevel(),player.getGold(),player.getDiamond(),player.getEquip(),player.getPet(),player.getUserId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
