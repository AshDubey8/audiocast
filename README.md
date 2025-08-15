# AudioCast

Desktop accessibility app for transcript management with text-to-speech functionality.

## Features
- Transcript management with H2 database
- Text-to-speech using FreeTTS with system fallback
- Full-text search capabilities
- Connection pooling for concurrent operations
- Cross-platform audio player controls

## Requirements
- Java 24
- H2 Database 2.3.232
- FreeTTS 1.2.3

## Setup
1. Download required JAR files to `/lib` folder
2. Compile: `javac -cp "lib/*" src/main/java/com/audiocast/*.java src/main/java/com/audiocast/*/*.java`
3. Run: `java -cp "lib/*:src/main/java" com.audiocast.AudioCast`

## Technology Stack
- **Language**: Java 24
- **GUI Framework**: Swing
- **Database**: H2 (embedded) with connection pooling
- **Text-to-Speech**: FreeTTS with automatic system fallback
- **Build**: Standard Java project structure