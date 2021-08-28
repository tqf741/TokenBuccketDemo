import java.util.concurrent.locks.ReentrantLock;

public class TokenBucket {

    ReentrantLock lock = new ReentrantLock();
    /*
        rate : 每秒产生rate个token
        max  : 桶中最多有max个token
        nextStartTime ：下一个token可获取的时间。什么时候初始化的，什么时候就可以开始获取
        curToken : 当前桶中token的数量，设置为double为了防止后面int除法四舍五入少精度
    */
    private final int rate;
    private final int max;
    private long nextStartTime;
    private double curToken;

    public TokenBucket(int rate, int max) {
        this.rate = rate;
        this.max = max;
    }

    //这个acquire返回若要获取TokenNum个token，还需要等多长时间
    //这里为了防止多线程竞争，加入了锁的操作
    public Long acquire(int tokenNum, Long curTime) {
        lock.lock();
        long startTime = possieStartTime(tokenNum, curTime);
        long waitTime = Math.max(startTime - curTime, 0);
        lock.unlock();
        return waitTime;
    }

    //如果要获取tokenNum个token，则可能的开始时间是多少
    public Long possieStartTime(int tokenNum, Long curTime) {
        //懒加载模式，每次获取token的时候计算一下token的增长,同时更新一下下一次可获取token的时间
        if (nextStartTime == 0) {
            nextStartTime = curTime;
        } else if (curTime > nextStartTime) {
            long timeSpend = curTime - nextStartTime;
            timeSpend = timeSpend / 1000;
            this.curToken = Math.min(max, (int) timeSpend * rate);
            this.nextStartTime = curTime;
        }
        long oldStartTime = nextStartTime;
        double canAcquireTokenNum = Math.min(tokenNum, this.curToken);
        double tokenStillNeed = tokenNum - canAcquireTokenNum;
        double spendAcquireToken = canAcquireTokenNum < rate ? 0 : (canAcquireTokenNum / rate);
        long waitTime = (long) ((spendAcquireToken + (tokenStillNeed / rate)) * 1000);
        this.nextStartTime = this.nextStartTime + waitTime;
        this.curToken -= canAcquireTokenNum;
        return oldStartTime;
    }
}
