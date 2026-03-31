package software.project.models;

/**
 * A player whose objective is to disrupt the water transport system.
 * <p>
 * Saboteurs can puncture pipes to cause water leakage and tamper with pump
 * directions to reroute water away from cisterns. Their score is derived
 * from every gallon of water that leaks into the desert.
 * </p>
 *
 * @see Player
 * @see Plumber
 * @see Pipe
 * @since 1.0
 */
public class Saboteur extends Player{
    /**
     * Damages the specified pipe, causing it to leak water.
     * <p>
     * Once a pipe is punctured, water passing through it spills into the desert
     * and contributes to the saboteur team's score. The pipe remains leaking
     * until repaired by a plumber.
     * </p>
     *
     * @param pipe the pipe to sabotage
     */
    public void sabotagePipe(Pipe pipe) {
        System.out.println("[Saboteur] sabotagePipe()");
    }
}
