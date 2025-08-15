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
('Welcome Message', 'Welcome to AudioCast, your personal transcript management system with advanced text-to-speech capabilities. This application allows you to create, edit, and manage your transcripts while providing high-quality audio playback using state-of-the-art text-to-speech technology.'),
('Introduction to Audio Technology', 'Audio technology has revolutionized how we consume and interact with digital content. From simple audio players to sophisticated text-to-speech systems, the field continues to evolve rapidly. Modern TTS engines can produce remarkably natural-sounding speech that is nearly indistinguishable from human voices.'),
('TTS Demo Text', 'This is a demonstration of our text-to-speech functionality. The system can read aloud any text you provide, making your transcripts accessible through audio playback. Try clicking the Speak button to hear this text read aloud. You can pause and resume playback at any time using the controls below.'),
('Sample Meeting Transcript', 'Good morning everyone, and welcome to our weekly team meeting. Today we will be discussing the progress on our current projects, reviewing the quarterly goals, and planning for the upcoming product launch. Please make sure to take notes as we go through each agenda item. Let us start with the status updates from each department head.'),
('Lorem Ipsum', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.');
