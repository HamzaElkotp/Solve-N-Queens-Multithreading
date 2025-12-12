package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

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
    }

    // Called later when threads are created
    public void updateThreadColors(java.util.Map<Integer, Color> colorMap) {
        legendPanel.setThreadColors(colorMap);
    }
}
