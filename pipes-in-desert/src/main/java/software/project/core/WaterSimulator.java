package software.project.core;

import software.project.models.Element;
import software.project.models.GameMap;
import software.project.models.Pipe;
import software.project.models.PipeEnd;
import software.project.utils.Debug;

import java.util.ArrayList;
import java.util.List;

public class WaterSimulator {

    private final GameMap map;

    // DO ALL ACTIONS, THEN Simulate Water flow cannot be in middle
    /*
    1. players do actions
    2. timer expires OR turn ends
    3. simulate water tick
    4. check win conditions
    5. repeat

Basically simulation runs every couple of seconds, and player input is just applied into the next tick

     */


    public WaterSimulator(GameMap map) {
        this.map = map;
    }

    public int tick() {

        int lostWater = 0;
        for (Element e : map.getElements()) {
           lostWater += e.receiveAndTransferWater();

        }
        for(PipeEnd pipeEnd: getAllPipeEnds()) {
        pipeEnd.commit();
        }
        Debug.log("LEAKED WATER: %d",lostWater);
        return lostWater;
    }
    private List<PipeEnd> getAllPipeEnds() {
        List<PipeEnd> ends = new ArrayList<>();

        for (Pipe p : map.getAllPipes()) {
            ends.add(p.getEnd1());
            ends.add(p.getEnd2());
        }

        return ends;
    }
}
