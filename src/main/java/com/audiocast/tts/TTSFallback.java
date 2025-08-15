package com.audiocast.tts;

import java.io.IOException;

public class TTSFallback {
    private boolean isWindows;
    private boolean isMac;
    private boolean isLinux;
    
    public TTSFallback() {
        String os = System.getProperty("os.name").toLowerCase();
        isWindows = os.contains("win");
        isMac = os.contains("mac");
        isLinux = os.contains("nix") || os.contains("nux");
    }
    
    public void speak(String text) {
        try {
            if (isWindows) {
                speakWindows(text);
            } else if (isMac) {
                speakMac(text);
            } else if (isLinux) {
                speakLinux(text);
            } else {
                System.out.println("TTS: " + text);
            }
        } catch (Exception e) {
            System.err.println("Fallback TTS failed: " + e.getMessage());
            System.out.println("TTS: " + text);
        }
    }
    
    private void speakWindows(String text) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("powershell", "-Command", 
            "Add-Type -AssemblyName System.Speech; " +
            "(New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" + 
            escapeText(text) + "')");
        pb.start();
    }
    
    private void speakMac(String text) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("say", escapeText(text));
        pb.start();
    }
    
    private void speakLinux(String text) throws IOException {
        try {
            ProcessBuilder pb = new ProcessBuilder("espeak", escapeText(text));
            pb.start();
        } catch (IOException e) {
            ProcessBuilder pb = new ProcessBuilder("festival", "--tts");
            Process process = pb.start();
            process.getOutputStream().write(text.getBytes());
            process.getOutputStream().close();
        }
    }
    
    private String escapeText(String text) {
        return text.replace("'", "''").replace("\"", "\\\"");
    }
}
