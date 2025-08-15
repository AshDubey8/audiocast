package com.audiocast;

import javax.swing.*;
import com.audiocast.database.DatabaseManager;
import com.audiocast.tts.TTSManager;
import com.audiocast.ui.MainWindow;

public class AudioCast {
    private DatabaseManager dbManager;
    private TTSManager ttsManager;
    private MainWindow mainWindow;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for better OS integration
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // Use default look and feel if system fails
                e.printStackTrace();
            }
            
            new AudioCast().start();
        });
    }
    
    private void start() {
        JFrame frame = new JFrame("AudioCast - Transcript Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        JLabel statusLabel = new JLabel("Initializing AudioCast...", JLabel.CENTER);
        mainPanel.add(statusLabel);
        
        frame.add(mainPanel);
        frame.setVisible(true);
        
        SwingWorker<Void, String> initWorker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Initializing database...");
                dbManager = DatabaseManager.getInstance();
                
                publish("Initializing text-to-speech...");
                ttsManager = new TTSManager();
                
                publish("Ready!");
                return null;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                statusLabel.setText(chunks.get(chunks.size() - 1));
            }
            
            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    
                    // Create UI on EDT
                    mainWindow = new MainWindow(dbManager, ttsManager);
                    frame.dispose();
                    mainWindow.setVisible(true);
                    
                    if (ttsManager.isAvailable()) {
                        ttsManager.speak("AudioCast is ready for use");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame, 
                        "Error initializing AudioCast: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        initWorker.execute();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (ttsManager != null) {
                ttsManager.shutdown();
            }
            if (dbManager != null) {
                dbManager.shutdown();
            }
        }));
    }
}
