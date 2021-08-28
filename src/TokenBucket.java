public interface TokenBucket {

    public Long acquire(int tokenNum, Long curTime);

    public Long startTime(int tokenNum, Long curTime);
}
