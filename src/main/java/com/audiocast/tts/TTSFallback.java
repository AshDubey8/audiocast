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
        String command = "powershell -Command \"Add-Type -AssemblyName System.Speech; " +
                        "(New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" + 
                        escapeText(text) + "')\"";
        Runtime.getRuntime().exec(command);
    }
    
    private void speakMac(String text) throws IOException {
        String[] command = {"say", escapeText(text)};
        Runtime.getRuntime().exec(command);
    }
    
    private void speakLinux(String text) throws IOException {
        try {
            String[] command = {"espeak", escapeText(text)};
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            String[] command = {"festival", "--tts"};
            Process process = Runtime.getRuntime().exec(command);
            process.getOutputStream().write(text.getBytes());
            process.getOutputStream().close();
        }
    }
    
    private String escapeText(String text) {
        return text.replace("'", "''").replace("\"", "\\\"");
    }
}
