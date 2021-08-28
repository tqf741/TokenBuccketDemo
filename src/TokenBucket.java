public interface TokenBucket {

    /**
     * acquire返回若要获取TokenNum个token，还需要等多长时间
     * 为了防止多线程竞争，加入了锁的操作
     * @param tokenNum 当前线程需要tokenNum个token
     * @param curTime  当前时间是curTime
     * @return 返回需要等候的时间
     */
    public Long acquire(int tokenNum, Long curTime);

    /**
     * 如果要获取tokenNum个token，则最早的开始时间是多少
     * @param tokenNum
     * @param curTime
     * @return
     */
    public Long startTime(int tokenNum, Long curTime);
}
