package ui;

import controller.SnapshotConsumer;
import controller.ThreadManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private ThreadManager threadManager;
    private SnapshotConsumer snapshotConsumer;

    private BoardPanel boardPanel;
    private ControlPanel controlPanel;
    private ColorLegendPanel legendPanel;

    public MainFrame() {
        setTitle("N-Queens Solver (Multithreaded)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout());

        boardPanel = new BoardPanel();
        controlPanel = new ControlPanel(boardPanel);
        legendPanel = new ColorLegendPanel();

        add(controlPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(legendPanel, BorderLayout.EAST);

        int numThreads = Runtime.getRuntime().availableProcessors();
        threadManager = new ThreadManager(numThreads);

        snapshotConsumer = new SnapshotConsumer(
                boardPanel,
                threadManager.getSnapshotQueue(),
                threadManager.getThreadColorMap()
        );

        controlPanel.setThreadManager(threadManager);
        controlPanel.setSnapshotConsumer(snapshotConsumer);

        legendPanel.setThreadColors(threadManager.getThreadColorMap());
    }

    public void updateThreadColors(java.util.Map<Integer, Color> colorMap) {
        legendPanel.setThreadColors(colorMap);
    }
}
