package ui;

import controller.SnapshotConsumer;
import controller.ThreadManager;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private ThreadManager threadManager;
    private SnapshotConsumer snapshotConsumer;

    private JTextField nInput;
    private JButton startBtn;
    private JButton stopBtn;

    private BoardPanel boardPanel;

    public ControlPanel(BoardPanel panel) {
        this.boardPanel = panel;

        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(new JLabel("N:"));
        nInput = new JTextField("8", 5);
        add(nInput);

        startBtn = new JButton("Start");
        stopBtn = new JButton("Stop");

        add(startBtn);
        add(stopBtn);

        startBtn.addActionListener(e -> {
            try {
                int N = Integer.parseInt(nInput.getText());
                boardPanel.setBoardSize(N);
                boardPanel.repaint();

                if (threadManager != null && snapshotConsumer != null) {
                    snapshotConsumer.startConsuming();
                    threadManager.startAll(N);
                } else {
                    JOptionPane.showMessageDialog(this, "ThreadManager/SnapshotConsumer not initialized!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid N");
            }
        });

        stopBtn.addActionListener(e -> {
            if (threadManager != null && snapshotConsumer != null) {
                threadManager.stopAll();
                snapshotConsumer.stopConsuming();
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
