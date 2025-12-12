package ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ColorLegendPanel extends JPanel {

    private Map<Integer, Color> threadColors = new java.util.HashMap<>();

    public ColorLegendPanel() {
        setPreferredSize(new Dimension(150, 0));
    }

    public void setThreadColors(Map<Integer, Color> map) {
        this.threadColors = map;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString("Thread Colors:", 10, 20);

        int y = 40;
        for (var entry : threadColors.entrySet()) {
            g.setColor(entry.getValue());
            g.fillRect(10, y, 20, 20);
            g.setColor(Color.BLACK);
            g.drawString("T" + entry.getKey(), 40, y + 15);
            y += 30;
        }
    }
}
