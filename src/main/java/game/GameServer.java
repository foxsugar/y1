package game;

import game.db.DbProcessor;
import game.module.GameManager;
import game.net.socket.SocketServer;
import game.net.websocket.WebSocketServer;

import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by sun on 2015/8/21.
 */
public class GameServer {

    public static Executor executor = Executors.newCachedThreadPool();
    public static boolean isRun = false;
    private WebSocketServer webSocketServer;
    private SocketServer socketServer;

    private GameServer(){}

    private static GameServer instance;

    public static GameServer getInstance(){
        if(instance == null){
            instance = new GameServer();
        }
        return instance;
    }

    public static void main(String[] args) {
        GameServer.getInstance().startup();
    }


    private void startup(){


        GameManager.getInstance().start();

        isRun = true;

        //websocket
        webSocketServer = new WebSocketServer();
        executor.execute(webSocketServer);

        //socket
        socketServer = new SocketServer();
        executor.execute(socketServer);



        executor.execute(GameProcessor.getInstance());

        executor.execute(DbProcessor.getInstance());

        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()){
            String s = scanner.next();
            if("stop".equals(s)){
                shutdown();
            }
        }
    }

    private void shutdown(){

        while(true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(GameProcessor.getInstance().messageQueue.size()==0 && DbProcessor.getInstance().messages.size()==0){
                break;
            }
        }

        System.out.println("消息处理完毕 =====    ");

        GameManager.getInstance().stop();

        isRun = false;

        //netty
        webSocketServer.shutdown();

        //mysql连接关闭 等等

    }
}
