package com.audiocast.ui;

import javax.swing.*;
import java.awt.*;
import com.audiocast.database.DatabaseManager;
import com.audiocast.tts.TTSManager;

public class MainWindow extends JFrame {
    private DatabaseManager dbManager;
    private TTSManager ttsManager;
    
    public MainWindow(DatabaseManager dbManager, TTSManager ttsManager) {
        this.dbManager = dbManager;
        this.ttsManager = ttsManager;
        
        initializeUI();
        setupMenuBar();
    }
    
    private void initializeUI() {
        setTitle("AudioCast - Transcript Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("Welcome to AudioCast!", JLabel.CENTER);
        welcomeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.CENTER);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                dbManager.shutdown();
                ttsManager.shutdown();
            }
        });
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New Transcript");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "AudioCast v1.0\n" +
            "Desktop accessibility app for transcript management\n" +
            "with text-to-speech capabilities.\n\n" +
            "Built with Java " + System.getProperty("java.version"),
            "About AudioCast",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
