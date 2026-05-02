package software.project.models;

import org.junit.jupiter.api.Test;
import software.project.core.WaterSimulator;
import software.project.models.*;
import java.util.ArrayList;
import java.util.List;

public class WaterSimulationTestRunner {

    private final GameMap map;
    private final WaterSimulator simulator;
    int ticks = 15;


    public WaterSimulationTestRunner(GameMap map) {
        this.map = map;
        this.simulator = new WaterSimulator(map);
    }

    public static void main(String[] args) {
        new WaterSimulationTestRunner(new GameMap()).run();
    }
    public void run() {

        System.out.println("===== WATER SIMULATION TEST START =====");

        for (int i = 1; i <= ticks; i++) {

            System.out.println("\n========================");
            System.out.println("TICK " + i);
            System.out.println("========================");

            // 1. simulate
           int lostWater = simulator.tick();
           System.out.printf("Amount of lost water %d%n",lostWater);

            // 2. debug output
            printState();
        }

        System.out.println("\n===== END =====");
    }

    private void printState() {

        System.out.println("\n--- STATE SNAPSHOT ---");

        for (Element e : map.getElements()) {

            if (e instanceof Spring s) {
                System.out.println("[SPRING " + s.getId() + "]");
            }

            if (e instanceof Pump p) {
                System.out.println("[PUMP " + p.getId() + "]"
                        + " stored=" + p.getStoredWater()
                        + " broken=" + p.isBroken());
            }

            if (e instanceof Cistern c) {
                System.out.println("[CISTERN " + c.getId() + "]"
                        + " stored=" + c.getStoredWater());
            }

            if (e instanceof Pipe pipe) {
                int in1 = pipe.getEnd1().getCurrentWater();
                int in2 = pipe.getEnd2().getCurrentWater();

                System.out.println("[PIPE " + pipe.getId() + "] "
                        + "E1=" + in1 + " | E2=" + in2);
            }
        }

        System.out.println("---------------------\n");
    }
}