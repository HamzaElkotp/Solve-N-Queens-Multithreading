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
        boolean isFirstTimeAtCol1 = true; // Track if this is the first time visiting column 1

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
                boolean foundAlternative = false;
                
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
                
                // If we've backtracked to column 1 (START_COL + 1) and haven't found an alternative
                if (col == START_COL + 1 && !foundAlternative) {
                    // Check if this is the second time visiting column 1
                    if (!isFirstTimeAtCol1) {
                        // Second time at column 1 - we've exhausted all possibilities
                        // Clear all queens except the starting one
                        Arrays.fill(queens, -1);
                        queens[startRow] = START_COL;
                        emitSnapshot(queens, "TERMINATED");
                        return; // Exit the thread - no more attempts
                    }
                    
                    // First time at column 1 - try to place a queen here
                    isFirstTimeAtCol1 = false;
                    
                    // Check if there's any queen already in column 1
                    int existingRow = findQueenRow(queens, col);
                    
                    // If there's a queen in column 1, remove it first
                    if (existingRow >= 0) {
                        queens[existingRow] = -1;
                        emitSnapshot(queens, "BACKTRACKING");
                        try {
                            Thread.sleep(DELAY_MS);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    
                    // Try to place a queen in column 1, starting from row 0
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
                    
                    // If we can't place in column 1 on first visit, continue to next iteration
                    // which will backtrack again and eventually return to column 1 (second time)
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