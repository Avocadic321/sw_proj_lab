package software.project.models;

import software.project.utils.IdGenerator;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final List<Element> elements = new ArrayList<>();
    private Element spawnPoint;

    public GameMap() {
        buildMap();
    }

    public boolean addElement(Element element) {
        if (element != null) {
            elements.add(element);
            return true;
        }
        return false;
    }

    public boolean addElements(List<Element> elements) {
        for (Element element : elements) {
            addElement(element);
        }
        return true;
    }

    public boolean isPositionOccupied(int x, int y) {
        if (x < 0 || y < 0) return false;
        for (Element e : elements) {
            if (e.getX() == x && e.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public void removeElement(Element element) {
        elements.remove(element);
    }

    public Element getSpawnPoint() {
        return spawnPoint;
    }

    public Element getElement(String id) {
        for (Element element : elements) {
            if (element.getId().equals(id)) {
                return element;
            }
        }
        return null;
    }

    public <T extends Element> T getElement(String id, Class<T> type) {
        Element element = getElement(id);
        if (type.isInstance(element)) {
            return type.cast(element);
        }
        return null;
    }

    public List<Element> getElements() {
        return elements;
    }

    public <T extends Element> List<T> getElementsByType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Element element : elements) {
            if (type.isInstance(element)) {
                result.add(type.cast(element));
            }
        }
        return result;
    }

    public List<Spring> getAllSprings() {
        return getElementsByType(Spring.class);
    }

    public List<Cistern> getAllCisterns() {
        return getElementsByType(Cistern.class);
    }

    public List<Pump> getAllPumps() {
        return getElementsByType(Pump.class);
    }

    public List<Pipe> getAllPipes() {
        return getElementsByType(Pipe.class);
    }

    /*           S1        S2
     *           ||
     *           B1
     *           ||
     * FE===B2===P1===B3===P3===B9===FE
     *           ||        ||
     *           B4        B8
     *           ||        ||
     * C1===B5===P2===B6===P4===B7===C2
     *
     * Labels:
     * FE - Free End
     * B# - PIPE# - maybe you get it why is B :)
     * P# - PUMP#
     * C# - CISTERN#
     * S# - SPRING#
     * === or || - Pipes
     *
     * Spawn point: P1
     */
    private void buildMap() {
        IdGenerator.reset();
        elements.clear();

        // Active Elements
        Spring spring1 = new Spring( 2, 0);
        Spring spring2 = new Spring( 4, 0);
        Cistern cistern1 = new Cistern(0, 4);
        Cistern cistern2 = new Cistern(6, 4);
        Pump pump1 = new Pump(2,2);
        Pump pump2 = new Pump(2,4);
        Pump pump3 = new Pump(4,2);
        Pump pump4 = new Pump(4,4);

        // Pipes
        Pipe pipe1 = new Pipe(2,1);
        Pipe pipe2 = new Pipe(1,2);
        Pipe pipe3 = new Pipe(3,2);
        Pipe pipe4 = new Pipe(2,3);
        Pipe pipe5 = new Pipe(1,4);
        Pipe pipe6 = new Pipe(3,4);
        Pipe pipe7 = new Pipe(5,4);
        Pipe pipe8 = new Pipe(4,3);
        Pipe pipe9 = new Pipe(5,2);

        // Connections
        pipe1.connectBothEnds(spring1, pump1);
        pipe2.connectBothEnds(null, pump1);
        pipe3.connectBothEnds(pump1, pump3);
        pipe4.connectBothEnds(pump1, pump2);
        pipe5.connectBothEnds(cistern1, pump2);
        pipe6.connectBothEnds(pump2, pump4);
        pipe7.connectBothEnds(pump4, cistern2);
        pipe8.connectBothEnds(pump3, pump4);
        pipe9.connectBothEnds(pump3, null);

        pump1.setDirection(pipe1.getEnd2(),pipe4.getEnd1());
        pump2.setDirection(pipe4.getEnd2(),pipe5.getEnd2());
        // Register all elements
        addElements(List.of(
            spring1, spring2,
            cistern1, cistern2,
            pump1, pump2, pump3, pump4,
            pipe1, pipe2, pipe3, pipe4, pipe5, pipe6, pipe7, pipe8, pipe9
        ));

        // Spawn point
        spawnPoint = pump1;

    }

}