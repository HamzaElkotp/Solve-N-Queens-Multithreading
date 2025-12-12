package controller;

import model.Snapshot;
import ui.BoardPanel;

import java.awt.*;

public class SnapshotConsumer {

    public SnapshotConsumer(BoardPanel boardPanel,
                            java.util.concurrent.BlockingQueue<Snapshot> queue,
                            java.util.Map<Integer, Color> threadColors) {}

    public void startConsuming() {}

    public void stopConsuming() {}
}
