import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * demo说明：
 *        桶的设置：每秒可产生2个tokne，桶中最多有4个token存储
 *        设置三个线程，用线程池来调用执行
 *        t1线程需要3个token来执行
 *        t2线程需要1个
 *        t3线程需要两个
 *
 *        以线程2，1，3的执行顺序来讲解：
 *                     线程2需要1个token，所以刚一进去就可以执行，第一秒产生了两个，还剩1个
 *                     线程1需要3个token，拿到第1秒的1个还剩两个，所以在第2s可以开始执行
 *                     线程3需要2个tokne，所以可以在第3s开始执行
 */

public class demo {

    public static void main(String[] args) {
        TokenBucketImpl bucket = new TokenBucketImpl(2, 4);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, 8, 1000, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        Runnable t1 = new Runnable() {
            @Override
            public void run() {
                long sleepTime = bucket.acquire(3, System.currentTimeMillis());
                try {
                    Thread.sleep(sleepTime);
                    pritTime(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable t2 = new Runnable() {
            @Override
            public void run() {
                long sleepTime = bucket.acquire(1, System.currentTimeMillis());
                try {
                    Thread.sleep(sleepTime);
                    pritTime(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable t3 = new Runnable() {
            @Override
            public void run() {
                long sleepTime = bucket.acquire(2, System.currentTimeMillis());
                try {
                    Thread.sleep(sleepTime);
                    pritTime(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        threadPool.execute(t1);
        threadPool.execute(t2);
        threadPool.execute(t3);
        threadPool.shutdown();
    }

    public static void pritTime(int num) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        System.out.println("当前线程" + num + "执行时间是： ");
        System.out.println(formater.format(date));
    }
}
