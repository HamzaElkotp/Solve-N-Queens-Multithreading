package ui;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

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
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid N");
            }
        });

        stopBtn.addActionListener(e -> {
            // Will be connected to ThreadManager.stopAll()
            JOptionPane.showMessageDialog(this, "Stop pressed — implement later.");
        });
    }
}
