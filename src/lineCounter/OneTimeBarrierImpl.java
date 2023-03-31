package lineCounter;

public class OneTimeBarrierImpl implements OneTimeBarrier {

    private int numThreads;

    public OneTimeBarrierImpl(int numThreads) {
        this.numThreads = numThreads;
    }

    @Override
    public synchronized void hitAndWaitAll() throws InterruptedException {
        this.numThreads--;
        System.out.println("Entered Thread named: " + Thread.currentThread().getName());
        while (this.numThreads != 0) {
            wait();
        }
        System.out.println("Exited Thread named: " + Thread.currentThread().getName());
        notifyAll();
    }

    @Override
    public boolean isFinished() {
        return this.numThreads == 0;
    }
}
