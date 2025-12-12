package threads;

import model.Snapshot;

public class QueenWorker implements Runnable {
    public QueenWorker(int threadId, int startRow, int startCol,
                       int N,
                       java.util.concurrent.atomic.AtomicBoolean stopFlag,
                       java.util.concurrent.BlockingQueue<Snapshot> queue) {}

    @Override
    public void run() {}
}
