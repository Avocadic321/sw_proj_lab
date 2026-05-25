package software.project.audio;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
    private static AudioPlayer instance;
    private final Map<String, Clip> songs = new HashMap<>();
    private final Map<String, Clip> effects = new HashMap<>();
    private String currentSongKey;
    private long pausedSongPosition = -1;
    private boolean songPaused = false;
    private float songVolume = 0.8f;
    private float effectVolume = 0.8f;
    private boolean songMute = false;
    private boolean effectMute = false;

    private AudioPlayer() {
    }

    public static AudioPlayer getInstance() {
        if (instance == null)
            instance = new AudioPlayer();
        return instance;
    }

    /**
     * Load a background music file (WAV) from the classpath.
     * 
     * @param key  unique identifier for this song
     * @param path resource path, e.g. "/audio/menu.wav"
     */
    public void loadSong(String key, String path) {
        Clip clip = loadClip(path);
        if (clip != null)
            songs.put(key, clip);
    }

    /**
     * Load a sound effect file (WAV) from the classpath.
     * 
     * @param key  unique identifier for this effect
     * @param path resource path, e.g. "/audio/jump.wav"
     */
    public void loadEffect(String key, String path) {
        Clip clip = loadClip(path);
        if (clip != null)
            effects.put(key, clip);
    }

    /**
     * Load a background music file (WAV) from the local filesystem.
     *
     * @param key  unique identifier for this song
     * @param file audio file on disk
     */
    public void loadSongFromFile(String key, File file) {
        Clip clip = loadClipFromFile(file);
        if (clip != null)
            songs.put(key, clip);
    }

    private Clip loadClip(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Audio file not found: " + path);
                return null;
            }
            try (AudioInputStream audio = AudioSystem.getAudioInputStream(url);
                    AudioInputStream decoded = decodeToPcm(audio)) {
                Clip clip = AudioSystem.getClip();
                clip.open(decoded);
                return clip;
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Clip loadClipFromFile(File file) {
        if (file == null) {
            return null;
        }
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(file);
                AudioInputStream decoded = decodeToPcm(audio)) {
            Clip clip = AudioSystem.getClip();
            clip.open(decoded);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    private AudioInputStream decodeToPcm(AudioInputStream sourceStream) {
        AudioFormat sourceFormat = sourceStream.getFormat();
        if (AudioFormat.Encoding.PCM_SIGNED.equals(sourceFormat.getEncoding())) {
            return sourceStream;
        }

        AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sourceFormat.getSampleRate(),
                16,
                sourceFormat.getChannels(),
                sourceFormat.getChannels() * 2,
                sourceFormat.getSampleRate(),
                false);
        return AudioSystem.getAudioInputStream(targetFormat, sourceStream);
    }

    // --- Music playback ---
    public void playSong(String key) {
        if (songMute)
            return;
        stopCurrentSong();
        Clip clip = songs.get(key);
        if (clip != null) {
            currentSongKey = key;
            songPaused = false;
            pausedSongPosition = -1;
            clip.setMicrosecondPosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            updateVolume(clip, songVolume);
        }
    }

    public void playSongFromFile(File file) {
        if (songMute)
            return;
        stopCurrentSong();
        Clip clip = loadClipFromFile(file);
        if (clip != null) {
            currentSongKey = "__custom_file__";
            songs.put(currentSongKey, clip);
            songPaused = false;
            pausedSongPosition = -1;
            clip.setMicrosecondPosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            updateVolume(clip, songVolume);
        }
    }

    public void stopCurrentSong() {
        if (currentSongKey != null) {
            Clip clip = songs.get(currentSongKey);
            if (clip != null) {
                if (clip.isRunning()) {
                    clip.stop();
                }
                songPaused = false;
                pausedSongPosition = -1;
                if ("__custom_file__".equals(currentSongKey)) {
                    clip.close();
                    songs.remove(currentSongKey);
                }
            }
            currentSongKey = null;
        }
    }

    public boolean pauseCurrentSong() {
        if (currentSongKey == null) {
            return false;
        }
        Clip clip = songs.get(currentSongKey);
        if (clip == null) {
            return false;
        }
        pausedSongPosition = clip.getMicrosecondPosition();
        if (clip.isRunning()) {
            clip.stop();
        }
        songPaused = true;
        return true;
    }

    public void resumeCurrentSong() {
        if (songMute || currentSongKey == null || !songPaused) {
            return;
        }
        Clip clip = songs.get(currentSongKey);
        if (clip == null) {
            return;
        }
        if (pausedSongPosition >= 0) {
            clip.setMicrosecondPosition(pausedSongPosition);
        }
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        updateVolume(clip, songVolume);
        songPaused = false;
        pausedSongPosition = -1;
    }

    public void playEffect(String key) {
        if (effectMute)
            return;
        Clip clip = effects.get(key);
        if (clip != null) {
            // Stop if currently playing (so we can rewind)
            if (clip.isRunning()) {
                clip.stop();
            }
            // Rewind to the beginning
            clip.setMicrosecondPosition(0);
            clip.start();
            updateVolume(clip, effectVolume);
        }
    }

    public void setVolume(float songVolume, float effectVolume) {
        this.songVolume = Math.clamp(songVolume, 0.0f, 1.0f);
        this.effectVolume = Math.clamp(effectVolume, 0.0f, 1.0f);
        // Update currently playing song and all effects (if they are open)
        if (currentSongKey != null)
            updateVolume(songs.get(currentSongKey), songVolume);
        for (Clip clip : effects.values())
            updateVolume(clip, effectVolume);
    }

    public void setSongVolume(float volume) {
        this.songVolume = Math.clamp(volume, 0.0f, 1.0f);
        if (currentSongKey != null) {
            updateVolume(songs.get(currentSongKey), songVolume);
        }
    }

    public void setEffectVolume(float volume) {
        this.effectVolume = Math.clamp(volume, 0.0f, 1.0f);
        for (Clip clip : effects.values()) {
            updateVolume(clip, effectVolume);
        }
    }

    public float getSongVolume() {
        return songVolume;
    }

    public float getEffectVolume() {
        return effectVolume;
    }

    private void updateVolume(Clip clip, float volumeValue) {
        if (clip == null)
            return;
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float min = gainControl.getMinimum();
        float max = gainControl.getMaximum();
        float clamped = Math.clamp(volumeValue, 0.0f, 1.0f);
        float shaped = clamped == 0.0f ? 0.0f : (float) Math.log10(1.0 + 9.0 * clamped);
        float gain = min + (max - min) * shaped;
        gainControl.setValue(gain);
    }

    public void toggleSongMute() {
        songMute = !songMute;
        if (currentSongKey != null) {
            Clip clip = songs.get(currentSongKey);
            if (clip != null) {
                if (songMute && clip.isRunning())
                    clip.stop();
                else if (!songMute && !clip.isRunning() && songPaused)
                    resumeCurrentSong();
            }
        }
    }

    public void toggleEffectMute() {
        effectMute = !effectMute;
    }

    public boolean isSongMute() {
        return songMute;
    }

    public boolean isEffectMute() {
        return effectMute;
    }
}
