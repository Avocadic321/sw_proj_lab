package software.project.core;

import software.project.models.Team;
import software.project.utils.GameState;

public class Renderer {
    public void renderMessage(String message) {
        System.out.println("[Renderer] " + message);
    }

    public void renderState(GameState state) {
        System.out.println("[Renderer] state=" + state);
    }

    public void renderDisplayedScores(Team plumber, Team saboteur) {
        System.out.println("[Renderer] updateDisplayedScores()");
        int plumberScore = plumber == null ? 0 : plumber.getScore();
        int saboteurScore = saboteur == null ? 0 : saboteur.getScore();
        System.out.println("[Renderer] plumberScore=" + plumberScore + ", saboteurScore=" + saboteurScore);
    }

    public void renderFinalResult(Team winner) {
        System.out.println("[Renderer] displayFinalResult(winner=" + winner + ")");
    }
}
