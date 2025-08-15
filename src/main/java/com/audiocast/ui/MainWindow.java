package com.audiocast.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.audiocast.database.DatabaseManager;
import com.audiocast.tts.TTSManager;

public class MainWindow extends JFrame {
    private DatabaseManager dbManager;
    private TTSManager ttsManager;
    private TranscriptPanel transcriptPanel;
    private PlayerPanel playerPanel;
    
    // Professional color scheme
    private static final Color BACKGROUND_COLOR = new Color(248, 248, 248);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color ACCENT_COLOR = new Color(100, 100, 100);
    
    public MainWindow(DatabaseManager dbManager, TTSManager ttsManager) {
        this.dbManager = dbManager;
        this.ttsManager = ttsManager;
        
        setupLookAndFeel();
        initializeUI();
        setupMenuBar();
    }
    
    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            
            // Customize UI colors
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("Button.background", PANEL_COLOR);
            UIManager.put("Button.foreground", TEXT_COLOR);
            UIManager.put("Button.border", BorderFactory.createLineBorder(BORDER_COLOR));
            UIManager.put("TextField.background", PANEL_COLOR);
            UIManager.put("TextField.foreground", TEXT_COLOR);
            UIManager.put("TextArea.background", PANEL_COLOR);
            UIManager.put("TextArea.foreground", TEXT_COLOR);
            UIManager.put("List.background", PANEL_COLOR);
            UIManager.put("List.foreground", TEXT_COLOR);
            UIManager.put("List.selectionBackground", new Color(230, 230, 230));
            UIManager.put("List.selectionForeground", TEXT_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initializeUI() {
        setTitle("AudioCast - Transcript Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());
        
        transcriptPanel = new TranscriptPanel(dbManager, ttsManager);
        playerPanel = new PlayerPanel(ttsManager);
        
        add(transcriptPanel, BorderLayout.CENTER);
        add(playerPanel, BorderLayout.SOUTH);
        
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
        menuBar.setBackground(PANEL_COLOR);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(TEXT_COLOR);
        JMenuItem newItem = new JMenuItem("New Transcript");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        styleMenuItem(newItem);
        styleMenuItem(exitItem);
        
        newItem.addActionListener(e -> transcriptPanel.createNewTranscript());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(TEXT_COLOR);
        JMenuItem aboutItem = new JMenuItem("About");
        styleMenuItem(aboutItem);
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void styleMenuItem(JMenuItem item) {
        item.setBackground(PANEL_COLOR);
        item.setForeground(TEXT_COLOR);
        item.setBorder(new EmptyBorder(5, 10, 5, 10));
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
    
    public static Color getBackgroundColor() { return BACKGROUND_COLOR; }
    public static Color getPanelColor() { return PANEL_COLOR; }
    public static Color getBorderColor() { return BORDER_COLOR; }
    public static Color getTextColor() { return TEXT_COLOR; }
    public static Color getAccentColor() { return ACCENT_COLOR; }
}
