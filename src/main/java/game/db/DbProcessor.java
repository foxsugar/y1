package game.db;

import game.GameServer;
import game.Message.MessageHolder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by sun on 2015/8/25.
 */
public class DbProcessor implements Runnable{
    private static DbProcessor instance;

    private DbProcessor(){

    }

    public static DbProcessor getInstance(){
        if(instance == null){
            instance = new DbProcessor();
        }
        return instance;
    }



    public LinkedBlockingQueue<MessageHolder> messages = new LinkedBlockingQueue<>();


    @Override
    public void run() {
        while (true){

            MessageHolder holder = null;
            try {
                holder = messages.poll(10, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(holder != null && holder.message!=null){
                DbMsgDispatcher.getInstance().handle(holder);
            }
        }
    }
}
