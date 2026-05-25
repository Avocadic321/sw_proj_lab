package software.project.map;

import software.project.utils.IdGenerator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class GameMap {
    private final List<Element> elements = new ArrayList<>();
    private Element spawnPoint;

    public GameMap() {
        buildMap();
    }

    public GameMap(int choice) {
        switch (choice) {
            case 1 -> buildMap1();
            case 2 -> buildMap2();
            case 3 -> buildMap3();
            default -> buildMap();
        }
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
        if (x < 0 || y < 0) {
            return false;
        }
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

    public Element getElementAt(int x, int y) {
        for (Element element : elements) {
            if (element.getX() == x && element.getY() == y) {
                return element;
            }
        }
        return null;
    }

    public boolean isEmpty(int x, int y) {
        return getElementAt(x, y) == null;
    }

    // ---------- Adjacency helpers ----------
    public Element getNorthOf(Element e) {
        if (e == null) {
            return null;
        }
        return getElementAt(e.getX(), e.getY() - 1);
    }

    public Element getSouthOf(Element e) {
        if (e == null) {
            return null;
        }
        return getElementAt(e.getX(), e.getY() + 1);
    }

    public Element getEastOf(Element e) {
        if (e == null) {
            return null;
        }
        return getElementAt(e.getX() + 1, e.getY());
    }

    public Element getWestOf(Element e) {
        if (e == null) {
            return null;
        }
        return getElementAt(e.getX() - 1, e.getY());
    }

    public List<Element> getAdjacentElements(Element e) {
        List<Element> adj = new ArrayList<>();
        Element north = getNorthOf(e);
        if (north != null) {
            adj.add(north);
        }
        Element south = getSouthOf(e);
        if (south != null) {
            adj.add(south);
        }
        Element east = getEastOf(e);
        if (east != null) {
            adj.add(east);
        }
        Element west = getWestOf(e);
        if (west != null) {
            adj.add(west);
        }
        return adj;
    }

    public List<Directions> getAdjacentEmptyDirections(Element e) {
        if (e == null) {
            return new ArrayList<>();
        }
        List<Directions> empty = new ArrayList<>();
        if (isEmpty(e.getX(), e.getY() - 1)) {
            empty.add(Directions.NORTH);
        }
        if (isEmpty(e.getX(), e.getY() + 1)) {
            empty.add(Directions.SOUTH);
        }
        if (isEmpty(e.getX() + 1, e.getY())) {
            empty.add(Directions.EAST);
        }
        if (isEmpty(e.getX() - 1, e.getY())) {
            empty.add(Directions.WEST);
        }
        return empty;
    }

    public List<Point> getAdjacentEmptyPositions(Element e) {
        if (e == null) {
            return new ArrayList<>();
        }
        List<Point> empty = new ArrayList<>();
        int x = e.getX();
        int y = e.getY();
        if (isEmpty(x, y - 1)) {
            empty.add(new Point(x, y - 1));
        }
        if (isEmpty(x, y + 1)) {
            empty.add(new Point(x, y + 1));
        }
        if (isEmpty(x + 1, y)) {
            empty.add(new Point(x + 1, y));
        }
        if (isEmpty(x - 1, y)) {
            empty.add(new Point(x - 1, y));
        }
        return empty;
    }

    public Directions getDirection(Element from, Element to) {
        if (from == null || to == null) {
            return null;
        }
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        if (dx == 0 && dy == -1) {
            return Directions.NORTH;
        }
        if (dx == 0 && dy == 1) {
            return Directions.SOUTH;
        }
        if (dx == 1 && dy == 0) {
            return Directions.EAST;
        }
        if (dx == -1 && dy == 0) {
            return Directions.WEST;
        }
        return null;
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

    public List<ActiveElement> getActiveElements() {
        List<ActiveElement> result = new ArrayList<>();
        for (Element element : elements) {
            if (element instanceof ActiveElement active) {
                result.add(active);
            }
        }
        return result;
    }

    public boolean areConnected(Element from, Element to) {
        if (from == null || to == null) {
            return false;
        }

        if (from instanceof Pipe pipe) {
            return (pipe.getEnd1().connectedTo == to) ||
                (pipe.getEnd2().connectedTo == to);
        }

        if (from instanceof ActiveElement active) {
            for (PipeEnd end : active.getConnections()) {
                if (end.pipe == to) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * S1 → P1 → PipeX ← P2 ← S2
     */
    private void buildMap3() {
        IdGenerator.reset();
        elements.clear();

        Spring spring1 = new Spring(0, 0);
        Spring spring2 = new Spring(6, 0);
        Cistern cistern1 = new Cistern(0, 4);
        Cistern cistern2 = new Cistern(6, 4);
        Pump pump1 = new Pump(2, 0);
        Pump pump2 = new Pump(4, 0);

        Pipe pipe1 = new Pipe(1, 0);
        Pipe pipe2 = new Pipe(5, 0);
        Pipe pipeX = new Pipe(3, 0);
        Pipe pipe3 = new Pipe(1, 2);
        Pipe pipe4 = new Pipe(5, 2);

        pipe1.connectBothEnds(spring1, pump1);
        pipe2.connectBothEnds(spring2, pump2);
        pipeX.connectBothEnds(pump1, pump2);
        pipe3.connectBothEnds(pump1, cistern1);
        pipe4.connectBothEnds(pump2, cistern2);

        pump1.setDirection(pipe1.getEnd2(), pipeX.getEnd1());
        pump2.setDirection(pipe2.getEnd2(), pipeX.getEnd2());

        addElements(List.of(
            spring1, spring2, cistern1, cistern2,
            pump1, pump2, pipeX, pipe1, pipe2, pipe3, pipe4
        ));
        spawnPoint = pump1;
    }

    /**
     * S1 → P1 → FE S2 → P2 → FE
     */
    private void buildMap2() {
        IdGenerator.reset();
        elements.clear();

        Spring spring1 = new Spring(0, 0);
        Spring spring2 = new Spring(0, 4);
        Pump pump1 = new Pump(2, 0);
        Pump pump2 = new Pump(2, 4);

        Pipe pipe1 = new Pipe(1, 0);
        Pipe pipe2 = new Pipe(1, 4);
        Pipe pipe3 = new Pipe(3, 0);
        Pipe pipe4 = new Pipe(3, 4);

        pipe1.connectBothEnds(spring1, pump1);
        pipe2.connectBothEnds(spring2, pump2);
        pipe3.connectBothEnds(pump1, null);
        pipe4.connectBothEnds(pump2, null);

        pump1.setDirection(pipe1.getEnd2(), pipe3.getEnd1());
        pump2.setDirection(pipe2.getEnd2(), pipe4.getEnd1());

        addElements(List.of(
            spring1, spring2,
            pump1, pump2,
            pipe1, pipe2, pipe3, pipe4
        ));
        spawnPoint = pump1;
    }

    /**
     * S1 → P1 → C1
     */
    private void buildMap1() {
        IdGenerator.reset();
        elements.clear();

        Spring spring1 = new Spring(0, 0);
        Cistern cistern1 = new Cistern(4, 0);
        Pump pump1 = new Pump(2, 0);

        Pipe pipe1 = new Pipe(1, 0);
        Pipe pipe2 = new Pipe(3, 0);

        pipe1.connectBothEnds(spring1, pump1);
        pipe2.connectBothEnds(pump1, cistern1);

        pump1.setDirection(pipe1.getEnd2(), pipe2.getEnd1());

        addElements(List.of(spring1, cistern1, pump1, pipe1, pipe2));
        spawnPoint = pump1;
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
     */
    private void buildMap() {
        IdGenerator.reset();
        elements.clear();

        Spring spring1 = new Spring(2, 0);
        Spring spring2 = new Spring(4, 0);
        Cistern cistern1 = new Cistern(0, 4);
        Cistern cistern2 = new Cistern(6, 4);
        Pump pump1 = new Pump(2, 2);
        Pump pump2 = new Pump(2, 4);
        Pump pump3 = new Pump(4, 2);
        Pump pump4 = new Pump(4, 4);

        Pipe pipe1 = new Pipe(2, 1);
        Pipe pipe2 = new Pipe(1, 2);
        Pipe pipe3 = new Pipe(3, 2);
        Pipe pipe4 = new Pipe(2, 3);
        Pipe pipe5 = new Pipe(1, 4);
        Pipe pipe6 = new Pipe(3, 4);
        Pipe pipe7 = new Pipe(5, 4);
        Pipe pipe8 = new Pipe(4, 3);
        Pipe pipe9 = new Pipe(5, 2);

        pipe1.connectBothEnds(spring1, pump1);
        pipe2.connectBothEnds(null, pump1);
        pipe3.connectBothEnds(pump1, pump3);
        pipe4.connectBothEnds(pump1, pump2);
        pipe5.connectBothEnds(cistern1, pump2);
        pipe6.connectBothEnds(pump2, pump4);
        pipe7.connectBothEnds(pump4, cistern2);
        pipe8.connectBothEnds(pump3, pump4);
        pipe9.connectBothEnds(pump3, null);

        pump1.setDirection(pipe1.getEnd2(), pipe4.getEnd1());
        pump2.setDirection(pipe4.getEnd2(), pipe5.getEnd2());
        pump3.setDirection(pipe3.getEnd2(), pipe9.getEnd1());

        addElements(List.of(
            spring1, spring2,
            cistern1, cistern2,
            pump1, pump2, pump3, pump4,
            pipe1, pipe2, pipe3, pipe4, pipe5, pipe6, pipe7, pipe8, pipe9
        ));

        spawnPoint = pump1;
    }

    private record GraphNode(Element element, List<Element> neighbors) {
    }

    private GraphNode buildNode(Element element) {
        List<Element> neighbors = new ArrayList<>();

        if (element instanceof Pipe pipe) {
            if (pipe.getEnd1().connectedTo != null) {
                neighbors.add(pipe.getEnd1().connectedTo);
            }
            if (pipe.getEnd2().connectedTo != null) {
                neighbors.add(pipe.getEnd2().connectedTo);
            }
        } else if (element instanceof ActiveElement active) {
            for (PipeEnd end : active.getConnections()) {
                if (end.pipe != null) {
                    neighbors.add(end.pipe);
                }
            }
        }

        return new GraphNode(element, neighbors);
    }

    public List<Element> buildPathToDestination(Element src, Element dest) {
        List<Element> path = new ArrayList<>();

        if (src == null || dest == null) {
            return path;
        }
        if (!dest.canOccupy()) {
            return path;
        }

        Queue<Element> queue = new LinkedList<>();
        Set<Element> visited = new HashSet<>();
        Map<Element, Element> parent = new HashMap<>();

        queue.offer(src);
        visited.add(src);

        while (!queue.isEmpty()) {
            Element current = queue.poll();
            if (current.equals(dest)) {
                break;
            }

            GraphNode node = buildNode(current);
            for (Element neighbor : node.neighbors()) {
                if (visited.contains(neighbor)) {
                    continue;
                }
                if (!neighbor.canOccupy() && !neighbor.equals(dest)) {
                    continue;
                }
                visited.add(neighbor);
                parent.put(neighbor, current);
                queue.offer(neighbor);
            }
        }

        if (!parent.containsKey(dest) && !src.equals(dest)) {
            return path;
        }

        for (Element at = dest; at != null; at = parent.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        return path;
    }
}