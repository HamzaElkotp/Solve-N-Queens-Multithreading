public class BoardState {

    private int N;
    private int[] queens;

    public BoardState(int N) {
        this.N = N;
        this.queens = new int[N];

        for (int i = 0; i < N; i++) {
            queens[i] = -1;
        }
    }

    public int getN() {
        return N;
    }

    public int[] getQueens() {
        return queens;
    }

    public void setQueen(int col, int row) {
        queens[col] = row;
    }

    public void removeQueen(int col) {
        queens[col] = -1;
    }
}
