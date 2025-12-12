package ui;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    private int N = 8;

    // Later this will be fed by SnapshotConsumer
    private int[] queenPositions = null;
    private java.util.Map<Integer, Color> threadColorMap = new java.util.HashMap<>();

    public BoardPanel() {
        setPreferredSize(new Dimension(600, 600));
    }

    public void setBoardSize(int n) {
        this.N = n;
    }

    public void updateBoardState(int[] queens, java.util.Map<Integer, Color> colorMap) {
        this.queenPositions = queens;
        this.threadColorMap = colorMap;
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
                    g.setColor(new Color(240, 240, 240));
                else
                    g.setColor(new Color(200, 200, 200));
                g.fillRect(c * cell, r * cell, cell, cell);
            }
        }

        // draw queens (when data is available)
        if (queenPositions != null) {
            for (int row = 0; row < N; row++) {
                int col = queenPositions[row];
                if (col >= 0) {
                    Color color = threadColorMap.getOrDefault(row, Color.BLACK);
                    g.setColor(color);
                    int x = col * cell + cell / 4;
                    int y = row * cell + cell / 4;
                    g.fillOval(x, y, cell / 2, cell / 2);
                }
            }
        }
    }
}
