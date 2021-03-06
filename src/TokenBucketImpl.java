import java.util.concurrent.locks.ReentrantLock;

public class TokenBucketImpl implements TokenBucket {

    /**
     * lock : 并发锁
     */
    ReentrantLock lock;

    /**
     * rate : 每秒产生rate个token
     */
    private final int rate;

    /**
     * max  : 桶中最多有max个token
     */
    private final int max;

    /**
     * nextStartTime ：下一个token可获取的时间。什么时候初始化的，什么时候就可以开始获取
     */
    private long nextStartTime;

    /**
     * curToken : 当前桶中token的数量，设置为double为了防止后面int除法四舍五入少精度
     */
    private double curToken;

    public TokenBucketImpl(int rate, int max) {
        this.rate = rate;
        this.max  = max;
        this.lock = new ReentrantLock();
    }

    /**
     * acquire返回若要获取TokenNum个token，还需要等多长时间
     * 为了防止多线程竞争，加入了锁的操作
     * @param tokenNum 当前线程需要tokenNum个token
     * @param curTime  当前时间是curTime
     * @return 返回需要等候的时间
     */
    public Long acquire(int tokenNum, Long curTime) {
        lock.lock();
        long startTime = startTime(tokenNum, curTime);
        long waitTime = Math.max(startTime - curTime, 0);
        lock.unlock();
        return waitTime;
    }

    /**
     * 如果要获取tokenNum个token，则最早的开始时间是多少
     * 懒加载模式，每次获取token的时候计算一下token的增长,同时更新一下下一次可获取token的时间
     * @param tokenNum
     * @param curTime
     * @return
     */
    public Long startTime(int tokenNum, Long curTime) {
        if (nextStartTime == 0) {
            nextStartTime = curTime;
        } else if (curTime > nextStartTime) {
            long timeSpend = curTime - nextStartTime;
            timeSpend = timeSpend / 1000;
            this.curToken = Math.min(max, (int) this.curToken + timeSpend * rate);
            this.nextStartTime = curTime;
        }
        long oldStartTime = nextStartTime;
        double canAcquireTokenNum = Math.min(tokenNum, this.curToken);
        double tokenStillNeed = tokenNum - canAcquireTokenNum;
        long waitTime = (long) (((tokenStillNeed / rate)) * 1000);
        this.nextStartTime = this.nextStartTime + waitTime;
        this.curToken -= canAcquireTokenNum;
        return oldStartTime;
    }
}
