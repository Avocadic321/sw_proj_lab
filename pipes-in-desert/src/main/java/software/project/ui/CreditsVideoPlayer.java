package software.project.ui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import software.project.audio.AudioPlayer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public final class CreditsVideoPlayer {
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;

    private static final Object LOCK = new Object();

    private static JFrame frame;
    private static JFXPanel fxPanel;
    private static MediaPlayer mediaPlayer;
    private static boolean stopRequested;
    private static boolean pausedSong;

    private CreditsVideoPlayer() {
    }

    public static void play() {
        if (fxPanel == null) {
            new JFXPanel();
        }

        URL resource = CreditsVideoPlayer.class.getResource("/ui/credits/credits.mp4");
        if (resource == null) {
            System.err.println("[ERROR] Credits video not found at /ui/credits/credits.mp4");
            return;
        }

        final URL finalResource = resource;

        synchronized (LOCK) {
            if (frame != null) {
                SwingUtilities.invokeLater(() -> {
                    if (frame != null) {
                        frame.toFront();
                        frame.requestFocus();
                    }
                });
                return;
            }
            stopRequested = false;
        }

        pausedSong = AudioPlayer.getInstance().pauseCurrentSong();
        SwingUtilities.invokeLater(() -> openWindow(finalResource));
    }

    public static void stop() {
        MediaPlayer playerToStop;
        JFrame frameToClose;
        JFXPanel panelToClear;

        synchronized (LOCK) {
            stopRequested = true;
            playerToStop = mediaPlayer;
            mediaPlayer = null;
            frameToClose = frame;
            frame = null;
            panelToClear = fxPanel;
            fxPanel = null;
        }

        if (playerToStop != null) {
            Platform.runLater(() -> {
                try {
                    playerToStop.stop();
                } finally {
                    playerToStop.dispose();
                }
            });
        }

        if (panelToClear != null) {
            Platform.runLater(() -> panelToClear.setScene(null));
        }

        if (frameToClose != null) {
            SwingUtilities.invokeLater(frameToClose::dispose);
        }

        if (pausedSong) {
            AudioPlayer.getInstance().resumeCurrentSong();
            pausedSong = false;
        }
    }

    private static void openWindow(URL resource) {
        synchronized (LOCK) {
            if (frame != null) {
                return;
            }

            frame = new JFrame("Credits");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setUndecorated(true);
            frame.setResizable(false);
            frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setLocationRelativeTo(null);

            fxPanel = new JFXPanel();
            Platform.runLater(() -> Platform.setImplicitExit(false));
            frame.add(fxPanel);
            fxPanel.setFocusable(true);

            // Swing key listener: SPACE or ESC to stop
            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int code = e.getKeyCode();
                    if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ESCAPE) {
                        stop();
                    }
                }
            });

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    stop();
                }

                @Override
                public void windowClosed(WindowEvent e) {
                    stop();
                }
            });
            frame.setVisible(true);
        }

        Platform.runLater(() -> setupPlayer(resource));
    }

    private static void setupPlayer(URL resource) {
        synchronized (LOCK) {
            if (stopRequested || fxPanel == null || frame == null) {
                return;
            }
        }

        final MediaPlayer player;
        try {
            player = new MediaPlayer(new Media(resource.toExternalForm()));
        } catch (RuntimeException ex) {
            System.err.println("[ERROR] Unable to load credits video: " + ex.getMessage());
            stop();
            return;
        }

        player.setOnEndOfMedia(CreditsVideoPlayer::stop);
        player.setOnError(() -> {
            System.err.println("[ERROR] Credits video playback error: " + player.getError());
            stop();
        });

        MediaView mediaView = new MediaView(player);
        mediaView.setPreserveRatio(true);
        mediaView.setFitWidth(WINDOW_WIDTH);
        mediaView.setFitHeight(WINDOW_HEIGHT);

        StackPane root = new StackPane(mediaView);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        // JavaFX key event: SPACE or ESC to stop
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.SPACE || code == KeyCode.ESCAPE) {
                stop();
            }
        });

        synchronized (LOCK) {
            if (stopRequested || fxPanel == null || frame == null) {
                player.dispose();
                return;
            }
            mediaPlayer = player;
        }

        fxPanel.setScene(scene);
        player.play();

        Platform.runLater(() -> {
            synchronized (LOCK) {
                if (frame != null) {
                    frame.requestFocus();
                }
            }
        });
    }
}