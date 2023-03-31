package lineCounter;

public interface OneTimeBarrier {

    void hitAndWaitAll() throws InterruptedException;

    boolean isFinished();
}
