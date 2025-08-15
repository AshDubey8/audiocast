package com.audiocast.tts;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class TTSManager {
    private static final String VOICE_NAME = "kevin16";
    private Voice voice;
    private boolean isInitialized = false;
    private TTSFallback fallback;
    private volatile boolean isSpeaking = false;
    
    public TTSManager() {
        initializeTTS();
        fallback = new TTSFallback();
    }
    
    private void initializeTTS() {
        try {
            System.setProperty("freetts.voices", 
                "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
            
            VoiceManager voiceManager = VoiceManager.getInstance();
            voice = voiceManager.getVoice(VOICE_NAME);
            
            if (voice != null) {
                voice.allocate();
                voice.setRate(190);
                voice.setPitch(150);
                voice.setVolume(0.8f);
                isInitialized = true;
                System.out.println("FreeTTS initialized successfully");
            } else {
                System.err.println("Voice not found: " + VOICE_NAME);
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize FreeTTS: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void speak(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        
        new Thread(() -> {
            isSpeaking = true;
            try {
                if (isInitialized && voice != null) {
                    voice.speak(text);
                } else {
                    fallback.speak(text);
                }
            } catch (Exception e) {
                System.err.println("Error during speech synthesis: " + e.getMessage());
                fallback.speak(text);
            } finally {
                isSpeaking = false;
            }
        }).start();
    }
    
    public void stopSpeaking() {
        if (voice != null && isSpeaking) {
            // FreeTTS doesn't have a direct stop method, but we can deallocate and reallocate
            new Thread(() -> {
                try {
                    voice.deallocate();
                    Thread.sleep(100);
                    voice.allocate();
                } catch (Exception e) {
                    System.err.println("Error stopping speech: " + e.getMessage());
                }
            }).start();
        }
    }
    
    public boolean isAvailable() {
        return isInitialized && voice != null;
    }
    
    public boolean isSpeaking() {
        return isSpeaking;
    }
    
    public void shutdown() {
        if (voice != null) {
            voice.deallocate();
        }
    }
}
