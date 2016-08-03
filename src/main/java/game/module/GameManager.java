package game.module;

import game.db.DbManager;
import game.protobuf.PB;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by sun on 2015/8/26.
 */
public class GameManager {

    public static GameManager instance;

    public Map<Long, Player> players = new HashMap<>();
    public Map<Long, ChannelHandlerContext> ctxs = new HashMap<>();
    public Map<Long,String> id_nameMap = new HashMap<>();
    public Map<String, Long> name_idMap = new HashMap<>();
    public Set<ChannelHandlerContext> ctxSet = new HashSet<>();


    private Connection conn = DbManager.getConn();
    private QueryRunner queryRunner = new QueryRunner();
    private GameManager() {

    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void start(){
        //读取
        String sql = "select user_id,name,level,gold,diamond,equip,pet from user_base where id > 0";
        ResultSetHandler rsh = new ResultSetHandler() {
            @Override
            public Object handle(ResultSet rs) throws SQLException {
                while (rs.next()){
                    long id = rs.getLong("user_id");
                    String name = rs.getString("name");
                    id_nameMap.put(id,name);
                    name_idMap.put(name, id);
                    //测试用 生成player
                    Player player = new Player();
                    player.setName(name);
                    player.setUserId(id);
                    player.setGold(rs.getInt("gold"));

                    players.put(id, player);
                }
                System.out.println(id_nameMap.toString());
                return null;
            }
        };
        try {
            queryRunner.query(conn,sql,rsh);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        String sql = "update user_base set name=?,level=?,gold=?,diamond=?,equip=?,pet=? where user_id=?";
        Object[][] oo = new Object[players.size()][];
        int count = 0;
        for(Map.Entry<Long,Player> entry : players.entrySet()){
            Player player = entry.getValue();
            Object[] o = new Object[7];
            o[0] = player.getName();
            o[1] = player.getLevel();
            o[2] = player.getGold();
            o[3] = player.getDiamond();
            o[4] = player.getEquip().convert2Json();
            o[5] = player.getPet().convert2Json();
            o[6] = player.getUserId();

            oo[count] = o;
            count++;
        }
        try {
            queryRunner.batch(conn,sql,oo);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg2Client(ChannelHandlerContext ctx, PB.S2CMessage msg) {
        BinaryWebSocketFrame bwf = new BinaryWebSocketFrame();
        int length = msg.getSerializedSize();
        bwf.content().writeInt(length);
        bwf.content().writeBytes(msg.toByteArray());
        ctx.writeAndFlush(bwf);
    }
}
