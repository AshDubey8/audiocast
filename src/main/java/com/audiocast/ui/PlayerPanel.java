package com.audiocast.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.audiocast.tts.TTSManager;

public class PlayerPanel extends JPanel {
    private TTSManager ttsManager;
    private JButton stopButton;
    private JLabel statusLabel;
    private Timer statusTimer;
    
    public PlayerPanel(TTSManager ttsManager) {
        this.ttsManager = ttsManager;
        initializeUI();
        startStatusTimer();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Audio Player"));
        setPreferredSize(new Dimension(0, 80));
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        stopButton = new JButton("Stop");
        stopButton.addActionListener(new StopActionListener());
        stopButton.setEnabled(false);
        
        controlPanel.add(stopButton);
        
        statusLabel = new JLabel("Ready");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        
        add(controlPanel, BorderLayout.WEST);
        add(statusLabel, BorderLayout.CENTER);
    }
    
    private void startStatusTimer() {
        statusTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean speaking = ttsManager.isSpeaking();
                stopButton.setEnabled(speaking);
                statusLabel.setText(speaking ? "Speaking..." : "Ready");
            }
        });
        statusTimer.start();
    }
    
    private class StopActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ttsManager.stopSpeaking();
        }
    }
}
