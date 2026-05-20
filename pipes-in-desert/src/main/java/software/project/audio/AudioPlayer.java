package software.project.audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AudioPlayer {
    private static AudioPlayer instance;
    private final Map<String, Clip> songs = new HashMap<>();
    private final Map<String, Clip> effects = new HashMap<>();
    private String currentSongKey;
    private float volume = 0.8f;
    private boolean songMute = false;
    private boolean effectMute = false;

    private AudioPlayer() {}

    public static AudioPlayer getInstance() {
        if (instance == null) instance = new AudioPlayer();
        return instance;
    }

    /**
     * Load a background music file (WAV) from the classpath.
     * @param key unique identifier for this song
     * @param path resource path, e.g. "/audio/menu.wav"
     */
    public void loadSong(String key, String path) {
        Clip clip = loadClip(path);
        if (clip != null) songs.put(key, clip);
    }

    /**
     * Load a sound effect file (WAV) from the classpath.
     * @param key unique identifier for this effect
     * @param path resource path, e.g. "/audio/jump.wav"
     */
    public void loadEffect(String key, String path) {
        Clip clip = loadClip(path);
        if (clip != null) effects.put(key, clip);
    }

    private Clip loadClip(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Audio file not found: " + path);
                return null;
            }
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    // --- Music playback ---
    public void playSong(String key) {
        if (songMute) return;
        stopCurrentSong();
        Clip clip = songs.get(key);
        if (clip != null) {
            currentSongKey = key;
            clip.setMicrosecondPosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            updateVolume(clip);
        }
    }

    public void stopCurrentSong() {
        if (currentSongKey != null) {
            Clip clip = songs.get(currentSongKey);
            if (clip != null && clip.isRunning()) clip.stop();
            currentSongKey = null;
        }
    }

    public void playEffect(String key) {
        if (effectMute) return;
        Clip clip = effects.get(key);
        if (clip != null) {
            // Stop if currently playing (so we can rewind)
            if (clip.isRunning()) {
                clip.stop();
            }
            // Rewind to the beginning
            clip.setMicrosecondPosition(0);
            clip.start();
            updateVolume(clip);
        }
    }

    public void setVolume(float volume) {
        this.volume = Math.clamp(volume, 0.0f, 1.0f);
        // Update currently playing song and all effects (if they are open)
        if (currentSongKey != null) updateVolume(songs.get(currentSongKey));
        for (Clip clip : effects.values()) updateVolume(clip);
    }

    private void updateVolume(Clip clip) {
        if (clip == null) return;
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float min = gainControl.getMinimum();
        float max = gainControl.getMaximum();
        float gain = min + (max - min) * volume;
        gainControl.setValue(gain);
    }

    public void toggleSongMute() {
        songMute = !songMute;
        if (currentSongKey != null) {
            Clip clip = songs.get(currentSongKey);
            if (clip != null) {
                if (songMute && clip.isRunning()) clip.stop();
                else if (!songMute && !clip.isRunning()) clip.start();
            }
        }
    }

    public void toggleEffectMute() {
        effectMute = !effectMute;
    }

    public boolean isSongMute() { return songMute; }
    public boolean isEffectMute() { return effectMute; }
    public float getVolume() { return volume; }
}