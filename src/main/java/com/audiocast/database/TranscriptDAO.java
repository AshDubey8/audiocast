package com.audiocast.database;

import com.audiocast.model.Transcript;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TranscriptDAO {
    private DatabaseManager dbManager;
    
    public TranscriptDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    public List<Transcript> getAllTranscripts() {
        List<Transcript> transcripts = new ArrayList<>();
        String sql = "SELECT * FROM transcripts ORDER BY updated_at DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                transcripts.add(mapResultSetToTranscript(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching transcripts: " + e.getMessage());
        }
        
        return transcripts;
    }
    
    public boolean saveTranscript(Transcript transcript) {
        if (transcript.getId() == null) {
            return insertTranscript(transcript);
        } else {
            return updateTranscript(transcript);
        }
    }
    
    private boolean insertTranscript(Transcript transcript) {
        String sql = "INSERT INTO transcripts (title, content, created_at, updated_at) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, transcript.getTitle());
            stmt.setString(2, transcript.getContent());
            stmt.setTimestamp(3, Timestamp.valueOf(transcript.getCreatedAt()));
            stmt.setTimestamp(4, Timestamp.valueOf(transcript.getUpdatedAt()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transcript.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting transcript: " + e.getMessage());
        }
        
        return false;
    }
    
    private boolean updateTranscript(Transcript transcript) {
        String sql = "UPDATE transcripts SET title = ?, content = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transcript.getTitle());
            stmt.setString(2, transcript.getContent());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(4, transcript.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating transcript: " + e.getMessage());
        }
        
        return false;
    }
    
    public boolean deleteTranscript(Long id) {
        String sql = "DELETE FROM transcripts WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting transcript: " + e.getMessage());
        }
        
        return false;
    }
    
    private Transcript mapResultSetToTranscript(ResultSet rs) throws SQLException {
        Transcript transcript = new Transcript();
        transcript.setId(rs.getLong("id"));
        transcript.setTitle(rs.getString("title"));
        transcript.setContent(rs.getString("content"));
        transcript.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        transcript.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return transcript;
    }
}
