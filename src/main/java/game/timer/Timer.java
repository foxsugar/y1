package game.timer;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Created by sun on 2015/8/10.
 */
public class Timer {

    private PriorityQueue<TimerNode> queue = new PriorityQueue<>(100, new Comparator<TimerNode>() {
        @Override
        public int compare(TimerNode o1, TimerNode o2) {
            if (o1.getNextTriggerTime() > o2.getNextTriggerTime()) {
                return 1;
            }else if (o1.getNextTriggerTime() < o2.getNextTriggerTime()){
                return -1;
            }
            return 0;
        }
    });

    public void doWork() {
        long nowTime = System.currentTimeMillis();
        while (true){
            TimerNode node = queue.peek();
            if(node == null || node.getNextTriggerTime() > nowTime){
                return;
            }else{
                queue.poll().fire();
                if(node.isPeroid()){
                    queue.add(node);

                }
            }
        }
    }

    public void addTimerNode(TimerNode node) {
        queue.add(node);
    }

    public void removeNode(TimerNode node) {
        queue.remove(node);
    }


    public static void main(String[] args) {
        PriorityQueue<Integer> q = new PriorityQueue<>();
        q.offer(3);
        q.offer(30);
        q.offer(20);
        q.offer(90);
        q.offer(69);
        Iterator<Integer> it = q.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }
    }
}
