CREATE TABLE IF NOT EXISTS transcripts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO transcripts (title, content) VALUES 
('Welcome!', 'Hey there! Welcome to AudioCast - where your text actually talks back to you. Pretty neat, right?'),
('Quick Coffee Break', 'Just had the most amazing coffee. The barista drew a tiny cat in the foam. Now I can conquer the world... or at least finish this project.'),
('TTS Test Drive', 'Testing one, two, three. Does this thing work? Click Speak and find out! Pro tip: it should sound way better than my actual voice.'),
('Meeting Notes', 'Team meeting today. Sarah brought donuts. Tom forgot his laptop again. We discussed the new project timeline. The donuts were definitely the highlight.'),
('Random Thoughts', 'Why do we park in driveways and drive on parkways? Also, pineapple on pizza is totally acceptable. Fight me.');
