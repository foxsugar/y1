package game;

import game.Message.MessageHandler;
import game.Message.MessageHolder;
import game.timer.Timer;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by sun on 2015/8/21.
 *
 */
public class GameProcessor implements Runnable{

    private GameProcessor(){}

    public static GameProcessor instance;

    public LinkedBlockingQueue<MessageHolder> messageQueue = new LinkedBlockingQueue<>();
    public MessageHandler handler = new MessageHandler();
    public Timer timer = new Timer();

    public static GameProcessor getInstance(){
        if(instance == null){
            instance = new GameProcessor();
        }
        return instance;
    }


    @Override
    public void run() {
       while(true){

           //timer
           timer.doWork();
           MessageHolder messHolder = null;
           try {
               messHolder = messageQueue.poll(10, TimeUnit.MILLISECONDS);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           if(messHolder != null&&messHolder.message !=null){
               System.out.println("-111111111111111");
               handler.handleMessage(messHolder);
           }
       }
    }

}
