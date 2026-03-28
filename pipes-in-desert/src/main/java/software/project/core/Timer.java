package software.project.core;

public class Timer {
    public int duration;
    public int timeLeft;
    public boolean isRunning;

    public void start() {
        System.out.println("[Timer] start()");
    }

    public void pause() {
        System.out.println("[Timer] pause()");
    }

    public void resume() {
        System.out.println("[Timer] resume()");
    }

    public void stop() {
        System.out.println("[Timer] stop()");
    }

    public void tick() {
        System.out.println("[Timer] tick()");
    }

    public void setTurnDuration(int duration) {
        System.out.printf("[Timer] setTurnDuration(%d)%n", duration);
    }
}
