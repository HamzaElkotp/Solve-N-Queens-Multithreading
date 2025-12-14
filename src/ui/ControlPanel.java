package ui;

import controller.SnapshotConsumer;
import controller.ThreadManager;
import ui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private ThreadManager threadManager;
    private SnapshotConsumer snapshotConsumer;

    private JSpinner nInput;
    private JButton runBtn;
    private JButton stopBtn;

    private BoardPanel boardPanel;
    private ColorLegendPanel legendPanel;
    private MainFrame mainFrame;

    public ControlPanel(BoardPanel panel, ColorLegendPanel legendPanel, MainFrame mainFrame) {
        this.boardPanel = panel;
        this.legendPanel = legendPanel;
        this.mainFrame = mainFrame;

        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(new JLabel("Grid Size (N):"));
        nInput = new JSpinner(new SpinnerNumberModel(8, 4, 100, 1));
        nInput.setPreferredSize(new Dimension(60, 25));
        add(nInput);

        runBtn = new JButton("Run");
        stopBtn = new JButton("Stop");
        stopBtn.setEnabled(false);

        add(runBtn);
        add(stopBtn);

        runBtn.addActionListener(e -> {
            try {
                int N = (Integer) nInput.getValue();
                
                // Set board size first
                boardPanel.setBoardSize(N);
                boardPanel.clearBoard();
                
                // Initialize thread manager with N threads (same as grid size)
                mainFrame.initializeThreadManager(N);
                
                // Wait a bit for initialization
                SwingUtilities.invokeLater(() -> {
                    if (threadManager != null && snapshotConsumer != null) {
                        snapshotConsumer.startConsuming();
                        threadManager.startAll(N);
                        runBtn.setEnabled(false);
                        stopBtn.setEnabled(true);
                    }
                });

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        stopBtn.addActionListener(e -> {
            if (threadManager != null && snapshotConsumer != null) {
                threadManager.stopAll();
                snapshotConsumer.stopConsuming();
                boardPanel.clearBoard();
                runBtn.setEnabled(true);
                stopBtn.setEnabled(false);
            }
        });
    }

    public void setThreadManager(ThreadManager manager) {
        this.threadManager = manager;
    }

    public void setSnapshotConsumer(SnapshotConsumer consumer) {
        this.snapshotConsumer = consumer;
    }
}
