// This class is used to control the life cycle of all worker threads (Only N Queens solver threads)

package controller;

import model.Snapshot;
import threads.QueenWorker;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadManager {

    public final int numThreads;
    public final BlockingQueue<Snapshot> snapshotQueue;
    public final List<Thread> threads;
    public AtomicBoolean stopFlag;
    public HashMap<Integer, Color> threadColorMap;

    private Color generateDistinctColor(int threadId) {
        float hue = (threadId * 1.0f / this.numThreads);
        float saturation = 0.9f;
        float brightness = 0.9f;
        return Color.getHSBColor(hue, saturation, brightness);
    }

    public ThreadManager(int numThreads) {
        this.numThreads = numThreads;
        this.threads = new ArrayList<>();
        this.stopFlag = new AtomicBoolean(false);
        this.snapshotQueue = new LinkedBlockingQueue<>(numThreads);
        this.threadColorMap = new HashMap<Integer, Color>();
        for (int i = 0; i < numThreads; i++) {
            threadColorMap.put(i, generateDistinctColor(i));
        }
    }

    public void startAll(int N) {
        this.stopFlag.set(false);

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
        this.stopFlag.set(true);

        for(Thread thread : threads){
            thread.interrupt();
        }

        threads.clear();
        snapshotQueue.clear();
    }

    public HashMap<Integer, Color> getThreadColorMap() {
        return threadColorMap;
    }

    public BlockingQueue<Snapshot> getSnapshotQueue() {
        return snapshotQueue;
    }
}
