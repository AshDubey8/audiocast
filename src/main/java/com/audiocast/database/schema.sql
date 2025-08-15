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
('Ocean Fact', 'The Pacific Ocean is larger than all land masses on Earth combined. It covers about one third of the planet surface.'),
('Space Fact', 'A day on Venus is longer than its year. Venus rotates so slowly that one day takes 243 Earth days, but it orbits the sun in only 225 Earth days.'),
('Animal Fact', 'Octopuses have three hearts and blue blood. Two hearts pump blood to the gills while the third pumps blood to the rest of the body.'),
('Food Fact', 'Honey never spoils. Archaeologists have found edible honey in ancient Egyptian tombs that is over 3000 years old.'),
('Human Fact', 'Your brain uses about 20 percent of your total energy even though it only weighs about 3 pounds. It consumes glucose constantly to function.');