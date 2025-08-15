package com.audiocast.ui;

import javax.swing.*;
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
        
        // Left panel - transcript list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchField.addActionListener(e -> performSearch());
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        // Transcript list
        listModel = new DefaultListModel<>();
        transcriptList = new JList<>(listModel);
        transcriptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transcriptList.addListSelectionListener(new TranscriptSelectionListener());
        
        JScrollPane listScrollPane = new JScrollPane(transcriptList);
        
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        
        // Right panel - transcript editor
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        // Title field
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(new JLabel("Title: "), BorderLayout.WEST);
        titleField = new JTextField();
        titlePanel.add(titleField, BorderLayout.CENTER);
        
        // Content area
        contentArea = new JTextArea();
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        saveButton = new JButton("Save");
        deleteButton = new JButton("Delete");
        speakButton = new JButton("Speak");
        JButton newButton = new JButton("New");
        
        saveButton.addActionListener(new SaveActionListener());
        deleteButton.addActionListener(new DeleteActionListener());
        speakButton.addActionListener(new SpeakActionListener());
        newButton.addActionListener(e -> createNewTranscript());
        
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(speakButton);
        
        rightPanel.add(titlePanel, BorderLayout.NORTH);
        rightPanel.add(contentScrollPane, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add panels to main panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);
        
        // Initially disable edit controls
        setEditingEnabled(false);
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
        saveButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled && currentTranscript != null && currentTranscript.getId() != null);
        speakButton.setEnabled(enabled);
    }
    
    private class TranscriptSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                Transcript selected = transcriptList.getSelectedValue();
                if (selected != null) {
                    currentTranscript = selected;
                    titleField.setText(selected.getTitle());
                    contentArea.setText(selected.getContent());
                    setEditingEnabled(true);
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
