package threads;

import model.Snapshot;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueenWorker implements Runnable {

    private final int threadId;
    private final int startRow;
    private final int N;
    private final AtomicBoolean stopFlag;
    private final BlockingQueue<Snapshot> queue;

    private static final int START_COL = 0;
    private static final int SNAPSHOT_INTERVAL = 1; // Emit snapshot after every step
    private static final int DELAY_MS = 100; // Delay between operations in milliseconds

    public QueenWorker(int threadId,
                       int startRow,
                       int startCol,
                       int N,
                       AtomicBoolean stopFlag,
                       BlockingQueue<Snapshot> queue) {

        this.threadId = threadId;
        this.startRow = startRow;
        this.N = N;
        this.stopFlag = stopFlag;
        this.queue = queue;
    }

    @Override
    public void run() {

        int[] queens = new int[N];
        Arrays.fill(queens, -1);

        // Initial placement
        queens[startRow] = START_COL;
        
        // Emit initial snapshot
        emitSnapshot(queens, "INITIAL");

        int col = START_COL + 1;
        int steps = 0;

        while (!stopFlag.get() && !Thread.currentThread().isInterrupted()) {

            boolean placed = false;

            for (int row = 0; row < N; row++) {
                if (isSafe(queens, row, col)) {
                    queens[row] = col;
                    placed = true;
                    col++;
                    break;
                }
            }

            if (!placed) {
                col--;

                if (col == START_COL) {
                    emitSnapshot(queens, "TERMINATED");
                    return;
                }

                int prevRow = findQueenRow(queens, col);
                queens[prevRow] = -1;
                
                // Emit snapshot after backtracking
                emitSnapshot(queens, "BACKTRACKING");
                
                // Add delay to slow down operations
                try {
                    Thread.sleep(DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                continue;
            }

            steps++;
            // Emit snapshot after every step to see progress
            emitSnapshot(queens, "SEARCHING");
            
            // Add delay to slow down operations
            try {
                Thread.sleep(DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            if (col == N) {
                emitSnapshot(queens, "SOLUTION");
                return;
            }
        }
    }

    private boolean isSafe(int[] queens, int row, int col) {
        for (int r = 0; r < queens.length; r++) {
            int c = queens[r];
            if (c == -1) continue;

            if (r == row) return false;
            if (Math.abs(r - row) == Math.abs(c - col)) return false;
        }
        return true;
    }

    private int findQueenRow(int[] queens, int col) {
        for (int r = 0; r < queens.length; r++) {
            if (queens[r] == col) return r;
        }
        return -1;
    }

    private void emitSnapshot(int[] queens, String status) {
        queue.offer(new Snapshot(
                threadId,
                queens.clone(),
                System.currentTimeMillis()
        ));
    }
}