-- H2 Database Schema for AudioCast
-- Supports full-text search on transcript content

CREATE TABLE IF NOT EXISTS transcripts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create full-text index for fast searching
CREATE ALIAS IF NOT EXISTS FT_INIT FOR "org.h2.fulltext.FullText.init";
CALL FT_INIT();

-- Enable full-text search on transcript content
CALL FT_CREATE_INDEX('PUBLIC', 'TRANSCRIPTS', 'CONTENT');
