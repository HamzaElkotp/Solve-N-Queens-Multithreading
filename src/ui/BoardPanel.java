package ui;

import model.Snapshot;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BoardPanel extends JPanel {

    private int N = 8;

    private Map<Integer, Snapshot> latestStates = new HashMap<>();
    private Map<Integer, Color> threadColorMap = new HashMap<>();

    public BoardPanel() {
        setPreferredSize(new Dimension(600, 600));
    }

    public void setBoardSize(int n) {
        this.N = n;
        repaint();
    }

    public void clearBoard() {
        this.latestStates.clear();
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
        int cellSize = size / N;

        // Draw grid
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if ((r + c) % 2 == 0)
                    g.setColor(new Color(18, 26, 33));
                else
                    g.setColor(new Color(64, 64, 64));
                g.fillRect(c * cellSize, r * cellSize, cellSize, cellSize);
            }
        }

        // Build a map of cell positions to list of thread IDs that have queens there
        Map<String, List<Integer>> cellToThreads = new HashMap<>();
        
        for (Map.Entry<Integer, Snapshot> entry : latestStates.entrySet()) {
            int threadId = entry.getKey();
            Snapshot snapshot = entry.getValue();
            int[] queens = snapshot.getQueens();

            for (int row = 0; row < queens.length; row++) {
                int col = queens[row];
                if (col >= 0 && col < N && row < N) {
                    String cellKey = row + "," + col;
                    cellToThreads.computeIfAbsent(cellKey, k -> new ArrayList<>()).add(threadId);
                }
            }
        }

        // Draw queens: if multiple threads have queens in the same cell, show multiple circles
        for (Map.Entry<String, List<Integer>> cellEntry : cellToThreads.entrySet()) {
            String[] parts = cellEntry.getKey().split(",");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            List<Integer> threadIds = cellEntry.getValue();
            
            int numQueens = threadIds.size();
            int circleSize = Math.max(8, cellSize / (numQueens + 1));
            int spacing = circleSize + 2;
            
            // Calculate starting position to center the circles
            int totalWidth = (numQueens - 1) * spacing + circleSize;
            int startX = col * cellSize + (cellSize - totalWidth) / 2;
            int startY = row * cellSize + (cellSize - circleSize) / 2;
            
            for (int i = 0; i < numQueens; i++) {
                int threadId = threadIds.get(i);
                Color color = threadColorMap.getOrDefault(threadId, Color.BLACK);
                g.setColor(color);
                
                int x = startX + i * spacing;
                int y = startY;
                g.fillOval(x, y, circleSize, circleSize);
                
                // Draw a white border for better visibility
                g.setColor(Color.WHITE);
                g.drawOval(x, y, circleSize, circleSize);
            }
        }
    }

    public void updateBoard(HashMap<Integer, Snapshot> latestStates, HashMap<Integer, Color> threadColors) {
        this.latestStates = new HashMap<>(latestStates);
        this.threadColorMap = threadColors;
        repaint();
    }
}
