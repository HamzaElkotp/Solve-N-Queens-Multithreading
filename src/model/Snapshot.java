package model;

public class Snapshot {

    private final int threadId;
    private final int[] queens;
    private final long timestamp;
    
    public Snapshot(int threadId, int[] queens, long timestamp) {
        this.threadId = threadId;
        this.queens = queens.clone();
        this.timestamp = timestamp;
    }
    public int getThreadId() {
        return threadId;
    }
    public int[] getQueens() {
        return queens.clone();
    }
    public long getTimestamp() {
        return timestamp;
    }
}
