# AudioCast

A desktop accessibility application for transcript management with text-to-speech functionality.

## Why?

Managing transcripts for accessibility can be challenging. Many solutions exist, but most are either web-based or lack proper offline functionality. AudioCast provides a simple desktop solution for users who need reliable transcript management with text-to-speech capabilities.

If you're looking for a desktop app to manage transcripts with full-text search and TTS support, this tool can be valuable. The primary goal is to provide consistent accessibility features with professional UI design.

## What you can achieve

AudioCast focuses on practical transcript management:

- Create and edit transcripts with a clean interface
- Full-text search across all transcripts using H2 database
- Text-to-speech using FreeTTS with system fallbacks
- Professional UI with connection pooling for performance
- Offline functionality for privacy-sensitive content

## Requirements

- **Java 24** (latest version)
- **H2 Database 2.3.232**
- **FreeTTS 1.2.3** with voice libraries

## Setup

### Dependencies
Download JAR files to `/lib` folder:
- H2 2.3.232 from h2database.com
- FreeTTS 1.2.3 from GitHub JVoiceXML/FreeTTS releases
- Required voice files (cmu_us_kal, jsapi.jar)

### How to run

**Compile:**
```bash
javac -cp "lib/*" src/main/java/com/audiocast/*.java src/main/java/com/audiocast/*/*.java
```

**Run:**
```bash
java -cp "lib/*:src/main/java" com.audiocast.AudioCast
```

## Features

- **Database Storage**: H2 embedded database with connection pooling
- **Full-Text Search**: Built-in search across transcript content
- **Text-to-Speech**: FreeTTS integration with system TTS fallback
- **Professional UI**: Clean white/grey design with Swing components
- **Error Handling**: Comprehensive error handling and status feedback

## Project Structure

Built with Java and follows standard package organization:
- `com.audiocast` - Main application entry
- `com.audiocast.ui` - Swing UI components
- `com.audiocast.database` - H2 database management
- `com.audiocast.tts` - Text-to-speech handling
- `com.audiocast.model` - Data models

---

**Status**: Work in Progress (WIP)
