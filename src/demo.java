import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.spi.ToolProvider;

public class demo {

    public static void main(String[] args) {
        //每秒最多产生2个token，桶中最多4个token
        TokenBucket bucket = new TokenBucket(2, 4);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                long sleepTime = bucket.acquire(2, System.currentTimeMillis());
                try {
                    Thread.sleep(sleepTime);
                    pritTime(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
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
        });

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                long sleepTime = bucket.acquire(1, System.currentTimeMillis());
                try {
                    Thread.sleep(sleepTime);
                    pritTime(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();
    }

    public static void pritTime (int num) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        System.out.println("当前线程" + num + "执行时间是： ");
        System.out.println(formater.format(date));
    }
}
