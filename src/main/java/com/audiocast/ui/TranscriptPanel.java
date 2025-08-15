package com.audiocast.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import com.audiocast.database.DatabaseManager;
import com.audiocast.database.TranscriptDAO;
import com.audiocast.model.Transcript;
import com.audiocast.tts.TTSManager;

public class TranscriptPanel extends JPanel {
    private TTSManager ttsManager;
    private TranscriptDAO transcriptDAO;
    
    private JList<Transcript> transcriptList;
    private DefaultListModel<Transcript> listModel;
    private JTextArea contentArea;
    private JTextField titleField;
    private JTextField searchField;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton speakButton;
    
    private Transcript currentTranscript;
    
    public TranscriptPanel(DatabaseManager dbManager, TTSManager ttsManager) {
        this.ttsManager = ttsManager;
        this.transcriptDAO = new TranscriptDAO(dbManager);
        
        initializeUI();
        loadTranscripts();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(MainWindow.getBackgroundColor());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Left panel - transcript list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(320, 0));
        leftPanel.setBackground(MainWindow.getPanelColor());
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainWindow.getBorderColor(), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(MainWindow.getPanelColor());
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(MainWindow.getTextColor());
        searchLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        searchField = new JTextField();
        searchField.addActionListener(e -> performSearch());
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainWindow.getBorderColor(), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        searchField.setBackground(MainWindow.getPanelColor());
        searchField.setForeground(MainWindow.getTextColor());
        
        JButton searchButton = createStyledButton("Search");
        searchButton.addActionListener(e -> performSearch());
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Transcript list
        listModel = new DefaultListModel<>();
        transcriptList = new JList<>(listModel);
        transcriptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transcriptList.addListSelectionListener(new TranscriptSelectionListener());
        transcriptList.setBackground(MainWindow.getPanelColor());
        transcriptList.setForeground(MainWindow.getTextColor());
        transcriptList.setBorder(new EmptyBorder(5, 10, 5, 10));
        transcriptList.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        
        // Custom list cell renderer for better styling
        transcriptList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                setBorder(new EmptyBorder(8, 10, 8, 10));
                
                if (isSelected) {
                    setBackground(new Color(240, 240, 240));
                    setForeground(MainWindow.getTextColor());
                } else {
                    setBackground(MainWindow.getPanelColor());
                    setForeground(MainWindow.getTextColor());
                }
                
                return this;
            }
        });
        
        JScrollPane listScrollPane = new JScrollPane(transcriptList);
        listScrollPane.setBorder(BorderFactory.createLineBorder(MainWindow.getBorderColor(), 1));
        listScrollPane.getViewport().setBackground(MainWindow.getPanelColor());
        
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(Box.createVerticalStrut(15), BorderLayout.CENTER);
        leftPanel.add(listScrollPane, BorderLayout.SOUTH);
        
        // Right panel - transcript editor
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(MainWindow.getPanelColor());
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainWindow.getBorderColor(), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Title field
        JPanel titlePanel = new JPanel(new BorderLayout(10, 0));
        titlePanel.setBackground(MainWindow.getPanelColor());
        
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setForeground(MainWindow.getTextColor());
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        titleField = new JTextField();
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainWindow.getBorderColor(), 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        titleField.setBackground(MainWindow.getPanelColor());
        titleField.setForeground(MainWindow.getTextColor());
        titleField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(titleField, BorderLayout.CENTER);
        
        // Content area
        contentArea = new JTextArea();
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        contentArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentArea.setBackground(Color.WHITE);
        contentArea.setForeground(Color.BLACK);
        contentArea.setCaretColor(Color.BLUE);
        contentArea.setEnabled(true); // Force enable
        contentArea.setEditable(true); // Force editable
        
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                "Content",
                0, 0,
                new Font(Font.SANS_SERIF, Font.BOLD, 12),
                Color.BLACK
            ),
            new EmptyBorder(5, 5, 5, 5)
        ));
        contentScrollPane.getViewport().setBackground(Color.WHITE);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(MainWindow.getPanelColor());
        
        JButton newButton = createStyledButton("New");
        saveButton = createStyledButton("Save");
        deleteButton = createStyledButton("Delete");
        speakButton = createStyledButton("Speak");
        JButton viewButton = createStyledButton("View Text");
        
        // Style delete button differently
        deleteButton.setBackground(new Color(245, 245, 245));
        deleteButton.setForeground(new Color(180, 50, 50));
        
        saveButton.addActionListener(new SaveActionListener());
        deleteButton.addActionListener(new DeleteActionListener());
        speakButton.addActionListener(new SpeakActionListener());
        newButton.addActionListener(e -> createNewTranscript());
        viewButton.addActionListener(e -> {
            if (currentTranscript != null) {
                showTextInPopup(currentTranscript.getTitle(), currentTranscript.getContent());
            } else {
                showTextInPopup("No Selection", "Please select a transcript first.");
            }
        });
        
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(speakButton);
        buttonPanel.add(viewButton);
        
        rightPanel.add(titlePanel, BorderLayout.NORTH);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(contentScrollPane, BorderLayout.CENTER);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add panels to main panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(340);
        splitPane.setBackground(MainWindow.getBackgroundColor());
        splitPane.setBorder(null);
        splitPane.setDividerSize(10);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Initially disable edit controls
        setEditingEnabled(false);
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(MainWindow.getPanelColor());
        button.setForeground(MainWindow.getTextColor());
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainWindow.getBorderColor(), 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        button.setFocusPainted(false);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 240, 240));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!text.equals("Delete")) {
                    button.setBackground(MainWindow.getPanelColor());
                } else {
                    button.setBackground(new Color(245, 245, 245));
                }
            }
        });
        
        return button;
    }
    
    private void loadTranscripts() {
        SwingWorker<List<Transcript>, Void> worker = new SwingWorker<List<Transcript>, Void>() {
            @Override
            protected List<Transcript> doInBackground() throws Exception {
                return transcriptDAO.getAllTranscripts();
            }
            
            @Override
            protected void done() {
                try {
                    List<Transcript> transcripts = get();
                    listModel.clear();
                    for (Transcript transcript : transcripts) {
                        listModel.addElement(transcript);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TranscriptPanel.this,
                        "Error loading transcripts: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void performSearch() {
        String query = searchField.getText().trim();
        
        SwingWorker<List<Transcript>, Void> worker = new SwingWorker<List<Transcript>, Void>() {
            @Override
            protected List<Transcript> doInBackground() throws Exception {
                return transcriptDAO.searchTranscripts(query);
            }
            
            @Override
            protected void done() {
                try {
                    List<Transcript> transcripts = get();
                    listModel.clear();
                    for (Transcript transcript : transcripts) {
                        listModel.addElement(transcript);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(TranscriptPanel.this,
                        "Error searching transcripts: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    public void createNewTranscript() {
        currentTranscript = new Transcript("New Transcript", "");
        titleField.setText(currentTranscript.getTitle());
        contentArea.setText(currentTranscript.getContent());
        setEditingEnabled(true);
        titleField.requestFocus();
        titleField.selectAll();
    }
    
    private void setEditingEnabled(boolean enabled) {
        titleField.setEnabled(enabled);
        contentArea.setEnabled(enabled);
        contentArea.setEditable(enabled);
        saveButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled && currentTranscript != null && currentTranscript.getId() != null);
        speakButton.setEnabled(enabled);
        System.out.println("Set editing enabled: " + enabled + ", content area enabled: " + contentArea.isEnabled());
    }
    
    private void showTextInPopup(String title, String content) {
        JFrame popup = new JFrame("Transcript Content - " + title);
        popup.setSize(600, 400);
        popup.setLocationRelativeTo(this);
        
        JTextArea popupText = new JTextArea(content);
        popupText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        popupText.setBackground(Color.WHITE);
        popupText.setForeground(Color.BLACK);
        popupText.setEditable(true);
        popupText.setLineWrap(true);
        popupText.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(popupText);
        scrollPane.setBackground(Color.WHITE);
        
        JButton copyToMain = new JButton("Copy to Main Editor");
        copyToMain.addActionListener(e -> {
            contentArea.setText(popupText.getText());
            if (currentTranscript != null) {
                currentTranscript.setContent(popupText.getText());
            }
            popup.dispose();
        });
        
        JButton speakButton = new JButton("Speak This Text");
        speakButton.addActionListener(e -> {
            String textToSpeak = popupText.getText();
            if (!textToSpeak.trim().isEmpty()) {
                ttsManager.speak(textToSpeak);
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(copyToMain);
        buttonPanel.add(speakButton);
        
        popup.setLayout(new BorderLayout());
        popup.add(scrollPane, BorderLayout.CENTER);
        popup.add(buttonPanel, BorderLayout.SOUTH);
        
        popup.setVisible(true);
    }
    
    private class TranscriptSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                Transcript selected = transcriptList.getSelectedValue();
                if (selected != null) {
                    currentTranscript = selected;
                    System.out.println("Selected transcript: " + selected.getTitle());
                    System.out.println("Content: " + selected.getContent());
                    titleField.setText(selected.getTitle());
                    System.out.println("Setting content area text to: " + selected.getContent());
                    contentArea.setText(selected.getContent());
                    contentArea.revalidate();
                    contentArea.repaint();
                    System.out.println("Content area text after setting: " + contentArea.getText());
                    contentArea.setEnabled(true);
                    contentArea.setEditable(true);
                    setEditingEnabled(true);
                    System.out.println("Content area enabled: " + contentArea.isEnabled());
                    System.out.println("Content area editable: " + contentArea.isEditable());
                    
                    // FORCE SHOW TEXT IN POPUP WINDOW
                    showTextInPopup(selected.getTitle(), selected.getContent());
                }
            }
        }
    }
    
    private class SaveActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentTranscript == null) return;
            
            String title = titleField.getText().trim();
            String content = contentArea.getText();
            
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(TranscriptPanel.this,
                    "Title cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            currentTranscript.setTitle(title);
            currentTranscript.setContent(content);
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return transcriptDAO.saveTranscript(currentTranscript);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            loadTranscripts();
                            JOptionPane.showMessageDialog(TranscriptPanel.this,
                                "Transcript saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(TranscriptPanel.this,
                                "Failed to save transcript.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(TranscriptPanel.this,
                            "Error saving transcript: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private class DeleteActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentTranscript == null || currentTranscript.getId() == null) return;
            
            int confirm = JOptionPane.showConfirmDialog(TranscriptPanel.this,
                "Are you sure you want to delete this transcript?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return transcriptDAO.deleteTranscript(currentTranscript.getId());
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            boolean success = get();
                            if (success) {
                                loadTranscripts();
                                titleField.setText("");
                                contentArea.setText("");
                                currentTranscript = null;
                                setEditingEnabled(false);
                            } else {
                                JOptionPane.showMessageDialog(TranscriptPanel.this,
                                    "Failed to delete transcript.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(TranscriptPanel.this,
                                "Error deleting transcript: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();
            }
        }
    }
    
    private class SpeakActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String content = contentArea.getText().trim();
            if (!content.isEmpty()) {
                ttsManager.speak(content);
            }
        }
    }
}
