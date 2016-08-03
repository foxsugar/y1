package game.utils;

/**
 * Created by sun on 2015/9/1.
 * 1位标志
 * 41位毫秒
 * 5位serverId
 * 5位workId
 * 12位count
 */
public class IdWorker {
    private static final long flag = 0L;
    private static final int maxCount = 4096;

    private int serverId;
    private int workId;
    private long lastMillis;
    private int count;

    public IdWorker(int serverId,int workId){
        this.serverId = serverId;
        this.workId = serverId;
    }

    public synchronized long nextId(){
        long time = System.currentTimeMillis();

        int serverId = this.serverId;
        int workId = this.workId;

        if(lastMillis == time){
            this.count++;
        }
        this.lastMillis = time;

        long id = time<<22|serverId<<17|workId<<12|count;

        return id;
    }
    public static void main(String[] args) {

//        System.out.println(nextId());
        IdWorker idWorker = new IdWorker(1,1);
        System.out.println(idWorker.nextId());
        System.out.println(idWorker.nextId());
        System.out.println(idWorker.nextId());
        System.out.println(idWorker.nextId());
        System.out.println(idWorker.nextId());
    }


}
