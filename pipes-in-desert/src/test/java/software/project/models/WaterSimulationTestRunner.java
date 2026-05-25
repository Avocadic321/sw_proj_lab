package software.project.models;

import software.project.core.WaterSimulator;
import software.project.map.*;

import java.awt.event.KeyEvent;

public class WaterSimulationTestRunner {

    private final GameMap map;
    private final WaterSimulator simulator;
    int ticks = 5;


    public WaterSimulationTestRunner(GameMap map) {
        this.map = map;
        this.simulator = new WaterSimulator(map);
    }

    public static void main(String[] args) {
        new WaterSimulationTestRunner(new GameMap()).run();
    }
    public void run() {

        System.out.println("===== WATER SIMULATION TEST START =====");

        for (int i = 1; i <= 15; i++) {

            System.out.println("\n========================");
            System.out.println("TICK " + i);
            System.out.println("========================");

            // 1. simulate
           int lostWater = simulator.tickFlow();
           System.out.printf("Amount of lost water %d%n",lostWater);

            // 2. debug output
            printState();
        }



                var x =  map.getAllPipes().stream().filter(f -> f.getId().equals("PIPE4")).findAny();
               var y = map.getAllPumps().stream().filter(f -> f.getId().equals("PUMP1")).findAny();
                if(x.isPresent() && y.isPresent()){
                    y.get().setDirection(y.get().getInputPipe(),x.get().getEnd1());
                }


        for (int i = 1; i <= 30; i++) {

            System.out.println("\n========================");
            System.out.println("TICK " + i);
            System.out.println("========================");

            // 1. simulate
            int lostWater = simulator.tickFlow();
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
            } else if (e instanceof Pump p) {
                System.out.println("[PUMP " + p.getId() + "]"
                        + " stored=" + p.getStoredWater()
                        + " flowing=" + p.getCurrentFlowingWater()
                        + " broken=" + p.isBroken());
            } else if (e instanceof Cistern c) {
                System.out.println("[CISTERN " + c.getId() + "]"
                        + " stored=" + c.getStoredWater());
            } else if (e instanceof Pipe pipe) {
                System.out.println("[PIPE " + pipe.getId() + "]"
                        + " currentWater=" + pipe.getCurrentWater()
                        + " flowing=" + pipe.getCurrentFlowingWater()
                        + " broken=" + pipe.isBroken()
                        + " conflict=" + pipe.isConflict());
            }
        }

        System.out.println("---------------------\n");
    }
    }