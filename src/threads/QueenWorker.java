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

        while (!stopFlag.get() && !Thread.currentThread().isInterrupted()) {

            // Check if we've found a solution
            if (col == N) {
                emitSnapshot(queens, "SOLUTION");
                return;
            }

            // Try to place a queen in the current column
            boolean placed = false;
            
            for (int row = 0; row < N; row++) {
                if (isSafe(queens, row, col)) {
                    queens[row] = col;
                    placed = true;
                    
                    // Emit snapshot after placing a queen
                    emitSnapshot(queens, "SEARCHING");
                    
                    // Add delay to slow down operations
                    try {
                        Thread.sleep(DELAY_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    
                    col++; // Move to next column
                    break;
                }
            }

            // If we couldn't place a queen, we need to backtrack
            if (!placed) {
                // Keep backtracking until we find a column where we can try an alternative
                // Never backtrack past column 1 (START_COL + 1) - the starting queen must stay
                while (col > START_COL + 1) {
                    col--; // Move back to previous column

                    // Find and remove the queen in the current column
                    int prevRow = findQueenRow(queens, col);
                    if (prevRow < 0) {
                        // No queen in this column, continue backtracking
                        continue;
                    }
                    
                    queens[prevRow] = -1;
                    
                    // Emit snapshot after removing queen (backtracking)
                    emitSnapshot(queens, "BACKTRACKING");
                    
                    // Add delay to slow down operations
                    try {
                        Thread.sleep(DELAY_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    
                    // Try to find the next safe position in this column
                    // Start searching from the row after the one we just removed
                    boolean foundAlternative = false;
                    for (int row = prevRow + 1; row < N; row++) {
                        if (isSafe(queens, row, col)) {
                            queens[row] = col;
                            foundAlternative = true;
                            
                            // Emit snapshot after placing alternative queen
                            emitSnapshot(queens, "SEARCHING");
                            
                            // Add delay to slow down operations
                            try {
                                Thread.sleep(DELAY_MS);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                            
                            col++; // Move forward to next column
                            break;
                        }
                    }
                    
                    // If we found an alternative, break out of backtracking loop
                    if (foundAlternative) {
                        break;
                    }
                    // Otherwise, continue backtracking (the while loop will continue)
                }
                
                // If we've backtracked to column 1 (START_COL + 1), try one more time to place a queen there
                // If we can't, we've exhausted all possibilities from the starting position
                if (col == START_COL + 1) {
                    // Try to place a queen in column 1 one more time
                    boolean canPlaceInCol1 = false;
                    for (int row = 0; row < N; row++) {
                        if (isSafe(queens, row, col)) {
                            queens[row] = col;
                            canPlaceInCol1 = true;
                            emitSnapshot(queens, "SEARCHING");
                            try {
                                Thread.sleep(DELAY_MS);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                            col++;
                            break;
                        }
                    }
                    
                    // If we still can't place in column 1, terminate
                    // The starting queen at (startRow, START_COL) must remain visible
                    if (!canPlaceInCol1) {
                        // Make sure the starting queen is still in the array
                        queens[startRow] = START_COL;
                        emitSnapshot(queens, "TERMINATED");
                        return;
                    }
                }
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