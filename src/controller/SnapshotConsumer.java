package controller;

import model.Snapshot;
import ui.BoardPanel;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SnapshotConsumer {

    public BoardPanel boardPanel;
    public BlockingQueue<Snapshot> queue;
    public HashMap<Integer, Color> threadColors;
    public AtomicBoolean is_Running;
    public HashMap latestStates;

    private Thread consumerThread;

    public SnapshotConsumer(BoardPanel boardPanel, BlockingQueue<Snapshot> queue, HashMap<Integer, Color> threadColors) {
        this.boardPanel = boardPanel;
        this.queue = queue;
        this.threadColors = threadColors;
        this.is_Running = new AtomicBoolean(false);
        latestStates = new HashMap<Integer, Snapshot>();
    }

    public void startConsuming() {
        // Stop any existing consumer first
        stopConsuming();
        
        this.is_Running.set(true);
        latestStates.clear();

        consumerThread = new Thread(() -> {
            try{
                while(is_Running.get()) {
                    Snapshot snapshot = queue.take(); // BLOCKING -> no busy waiting
                    latestStates.put(snapshot.getThreadId(), snapshot);

                    // Create a copy for thread safety
                    HashMap<Integer, Snapshot> statesCopy = new HashMap<>(latestStates);
                    HashMap<Integer, Color> colorsCopy = new HashMap<>(threadColors);
                    
                    SwingUtilities.invokeLater(() -> {
                        boardPanel.updateBoard(statesCopy, colorsCopy);
                    });
                }
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    public void stopConsuming() {
        this.is_Running.set(false);

        if (consumerThread != null) {
            consumerThread.interrupt();
        }

        latestStates.clear();
    }
}
