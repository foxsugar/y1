package game.db;

import game.utils.ProperitesUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.*;
import java.util.Properties;

/**
 * Created by sun on 2015/8/26.
 */
public class DbManager {
    private static DbManager ourInstance = new DbManager();

    public Connection conn;

    public QueryRunner queryRunner = new QueryRunner();


    public static DbManager getInstance() {
        return ourInstance;
    }

    private DbManager() {
        init();
    }

    static{
        try{
            //加载MySql的驱动类
            Class.forName("com.mysql.jdbc.Driver") ;
        }catch(ClassNotFoundException e){
            System.out.println("找不到驱动程序类 ，加载驱动失败！");
            e.printStackTrace() ;
        }
    }


    private void init() {
        try {
            Properties p = null;
            p = ProperitesUtil.loadProperties("db.properites");
            String url = p.getProperty("url");
            String user = p.getProperty("user");
            String passwd = p.getProperty("passwd");
            conn = DriverManager.getConnection(url, user, passwd);
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    public static Connection getConn(){
        Connection conn = null;
        try {
            Properties p = ProperitesUtil.loadProperties("db.properites");
            String url = p.getProperty("url");
            String user = p.getProperty("user");
            String passwd = p.getProperty("passwd");
            conn = DriverManager.getConnection(url, user, passwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    private void test(){
        String sql = "select * from user_base where user_id = 1;";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){

                int userId = rs.getInt(1);
                System.out.println(userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public static void main(String[] args) {
        DbManager.getInstance().test();
    }

}
