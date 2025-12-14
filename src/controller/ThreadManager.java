// This class is used to control the life cycle of all worker threads (Only N Queens solver threads)

package controller;

import model.Snapshot;
import threads.QueenWorker;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadManager {

    public final int numThreads;
    public final BlockingQueue<Snapshot> snapshotQueue;
    public final List<Thread> threads;
    public AtomicBoolean stopFlag;
    public HashMap<Integer, Color> threadColorMap;

    public ThreadManager(int numThreads) {
        this.numThreads = numThreads;
        this.threads = new ArrayList<>();
        this.stopFlag = new AtomicBoolean(false);
        this.snapshotQueue = new ArrayBlockingQueue<>(numThreads);
        this.threadColorMap = new HashMap<>();
    }

    public void startAll(int N) {
        this.stopFlag.set(false); // Signal that threads will start

        for (int thId = 0; thId < numThreads; thId++) {
            int strt_row = thId;
            int strt_col = 0;

            QueenWorker worker = new QueenWorker(thId, strt_row, strt_col, N, this.stopFlag, this.snapshotQueue);

            Thread new_thread = new Thread(worker);
            threads.add(new_thread);
            new_thread.start();
        }
    }

    public void stopAll() {
        this.stopFlag.set(false);

        for(Thread thread : threads){
            thread.interrupt();
        }

        threads.clear();
        snapshotQueue.clear();
    }

    public java.util.Map<Integer, Color> getThreadColorMap() {
        return threadColorMap;
    }

    public java.util.concurrent.BlockingQueue<Snapshot> getSnapshotQueue() {
        return snapshotQueue;
    }
}
