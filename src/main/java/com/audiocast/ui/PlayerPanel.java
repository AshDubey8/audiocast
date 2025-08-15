package com.audiocast.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setBackground(MainWindow.getPanelColor());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, MainWindow.getBorderColor()),
            new EmptyBorder(15, 20, 15, 20)
        ));
        setPreferredSize(new Dimension(0, 70));
        
        // Left panel with controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controlPanel.setBackground(MainWindow.getPanelColor());
        
        stopButton = createStyledButton("⏹ Stop");
        stopButton.addActionListener(new StopActionListener());
        stopButton.setEnabled(false);
        
        controlPanel.add(stopButton);
        
        // Center status label
        statusLabel = new JLabel("Ready", JLabel.CENTER);
        statusLabel.setForeground(MainWindow.getAccentColor());
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 13));
        
        // Right panel with status info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(MainWindow.getPanelColor());
        
        JLabel infoLabel = new JLabel("AudioCast TTS Engine");
        infoLabel.setForeground(MainWindow.getAccentColor());
        infoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        rightPanel.add(infoLabel);
        
        add(controlPanel, BorderLayout.WEST);
        add(statusLabel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(MainWindow.getPanelColor());
        button.setForeground(MainWindow.getTextColor());
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainWindow.getBorderColor(), 1),
            new EmptyBorder(6, 12, 6, 12)
        ));
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        button.setFocusPainted(false);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 240, 240));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(MainWindow.getPanelColor());
            }
        });
        
        return button;
    }
    
    private void startStatusTimer() {
        statusTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean speaking = ttsManager.isSpeaking();
                stopButton.setEnabled(speaking);
                
                if (speaking) {
                    statusLabel.setText("🔊 Speaking...");
                    statusLabel.setForeground(new Color(50, 150, 50));
                } else {
                    statusLabel.setText("Ready");
                    statusLabel.setForeground(MainWindow.getAccentColor());
                }
            }
        });
        statusTimer.start();
    }
    
    private class StopActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ttsManager.stopSpeaking();
            statusLabel.setText("Stopping...");
            statusLabel.setForeground(new Color(200, 100, 50));
        }
    }
}
