-- AudioCast Database Schema
-- H2 Database with full-text search support

CREATE TABLE IF NOT EXISTS transcripts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create full-text search index for content
CREATE ALIAS IF NOT EXISTS FT_INIT FOR "org.h2.fulltext.FullText.init";
CALL FT_INIT();

-- Initialize full-text search on transcripts table
CALL FT_CREATE_INDEX('PUBLIC', 'TRANSCRIPTS', 'CONTENT');

-- Sample data for testing
INSERT INTO transcripts (title, content) VALUES 
('Welcome Message', 'Welcome to AudioCast, your personal transcript management system with text-to-speech capabilities.'),
('Test Transcript', 'This is a test transcript to verify the text-to-speech functionality is working correctly.'),
('Lorem Ipsum', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore.');
