package ui;

import model.Snapshot;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BoardPanel extends JPanel {

    private int N = 8;

    private Map<Integer, Snapshot> latestStates = new HashMap<>();
    private Map<Integer, Color> threadColorMap = new HashMap<>();

    private int[] queenPositions = null;

    public BoardPanel() {
        setPreferredSize(new Dimension(600, 600));
    }

    public void setBoardSize(int n) {
        this.N = n;
        repaint();
    }

    public void updateBoardState(Map<Integer, Snapshot> latestStates, Map<Integer, Color> threadColors) {
        this.latestStates = new HashMap<>(latestStates);
        this.threadColorMap = threadColors;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (N <= 0) return;

        int size = Math.min(getWidth(), getHeight());
        int cell = size / N;

        // draw grid
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if ((r + c) % 2 == 0)
                    g.setColor(new Color(18, 26, 33));
                else
                    g.setColor(new Color(64, 64, 64));
                g.fillRect(c * cell, r * cell, cell, cell);
            }
        }

        for (Map.Entry<Integer, Snapshot> entry : latestStates.entrySet()) {
            int threadId = entry.getKey();
            Snapshot snapshot = entry.getValue();
            int[] queens = snapshot.getQueens();

            Color color = threadColorMap.getOrDefault(threadId, Color.BLACK);
            g.setColor(color);

            for (int row = 0; row < queens.length; row++) {
                int col = queens[row];
                if (col >= 0) {
                    int x = col * cell + cell / 4;
                    int y = row * cell + cell / 4;
                    g.fillOval(x, y, cell / 2, cell / 2);
                }
            }
        }
    }

    public void updateBoard(HashMap<Integer, Snapshot> latestStates, HashMap<Integer, Color> threadColors) {
        this.latestStates = new HashMap<Integer, Snapshot>(latestStates);
        this.threadColorMap = threadColors;
        repaint();
    }
}
