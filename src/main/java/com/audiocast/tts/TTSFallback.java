package com.audiocast.tts;

import java.io.IOException;

public class TTSFallback {
    private boolean isWindows;
    private boolean isMac;
    private boolean isLinux;
    private volatile Process currentProcess;
    private volatile boolean isSpeaking = false;
    
    public TTSFallback() {
        String os = System.getProperty("os.name").toLowerCase();
        isWindows = os.contains("win");
        isMac = os.contains("mac");
        isLinux = os.contains("nix") || os.contains("nux");
    }
    
    public void speak(String text) {
        isSpeaking = true;
        try {
            if (isWindows) {
                currentProcess = speakWindows(text);
            } else if (isMac) {
                currentProcess = speakMac(text);
            } else if (isLinux) {
                currentProcess = speakLinux(text);
            } else {
                System.out.println("TTS: " + text);
            }
            
            // Wait for process to complete in background thread
            if (currentProcess != null) {
                new Thread(() -> {
                    try {
                        currentProcess.waitFor();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        isSpeaking = false;
                        currentProcess = null;
                    }
                }).start();
            } else {
                isSpeaking = false;
            }
        } catch (Exception e) {
            System.err.println("Fallback TTS failed: " + e.getMessage());
            System.out.println("TTS: " + text);
            isSpeaking = false;
        }
    }
    
    private Process speakWindows(String text) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("powershell", "-Command", 
            "Add-Type -AssemblyName System.Speech; " +
            "(New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" + 
            escapeText(text) + "')");
        return pb.start();
    }
    
    private Process speakMac(String text) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("say", escapeText(text));
        return pb.start();
    }
    
    private Process speakLinux(String text) throws IOException {
        try {
            ProcessBuilder pb = new ProcessBuilder("espeak", escapeText(text));
            return pb.start();
        } catch (IOException e) {
            ProcessBuilder pb = new ProcessBuilder("festival", "--tts");
            Process process = pb.start();
            process.getOutputStream().write(text.getBytes());
            process.getOutputStream().close();
            return process;
        }
    }
    
    public void stop() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroyForcibly();
            currentProcess = null;
        }
        isSpeaking = false;
    }
    
    public boolean isSpeaking() {
        return isSpeaking && (currentProcess == null || currentProcess.isAlive());
    }
    
    private String escapeText(String text) {
        return text.replace("'", "''").replace("\"", "\\\"");
    }
}
