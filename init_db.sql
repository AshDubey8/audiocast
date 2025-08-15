CREATE TABLE IF NOT EXISTS transcripts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO transcripts (title, content) VALUES 
('Quadratic Formula', 'The quadratic formula is x equals negative b plus or minus the square root of b squared minus 4ac, all divided by 2a. This formula solves any quadratic equation of the form ax squared plus bx plus c equals zero.'),
('Newton Laws', 'Newton first law states that an object at rest stays at rest and an object in motion stays in motion unless acted upon by an external force. This is also known as the law of inertia.'),
('Hindi Basics', 'Hindi mein namaste ka matlab hai hello. Main ek vidyarthi hun means I am a student. Aap kaise hain means how are you. Hindi bharat ki rajbhasha hai.'),
('Calculus Derivatives', 'The derivative of x squared is 2x. The derivative of sine x is cosine x. The power rule states that the derivative of x to the n is n times x to the n minus one.'),
('Einstein Relativity', 'Einstein famous equation E equals mc squared shows that mass and energy are interchangeable. Time dilation occurs when objects move at speeds approaching the speed of light.');