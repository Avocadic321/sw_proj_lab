package software.project.map;

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

import software.project.utils.IdGenerator;

public class GameMap {
    private final List<Element> elements = new ArrayList<>();
    private Element spawnPoint;

    public GameMap(int numberOfPlumbers, int numberOfSaboteurs) {
        buildMapWithCisterns(Math.max(numberOfSaboteurs, numberOfPlumbers));
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

    /*
     * Legacy
     *
     * S1 S2
     * ||
     * B1
     * ||
     * FE===B2===P1===B3===P3===B9===FE
     * || ||
     * B4 B8
     * || ||
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
                pipe1, pipe2, pipe3, pipe4, pipe5, pipe6, pipe7, pipe8, pipe9));

        spawnPoint = pump1;
    }

    private void buildMapWithCisterns(int cisternCount) {
        IdGenerator.reset();
        elements.clear();

        int segments = Math.max(1, cisternCount / 2);
        int springY = 0;
        int pumpY = segments * 2;
        int cisternY = pumpY + segments * 2;

        List<Spring> springs = new ArrayList<>();
        List<Pump> pumps = new ArrayList<>();
        List<Cistern> cisterns = new ArrayList<>();
        List<Pipe> pipes = new ArrayList<>();
        List<Pump> junctions = new ArrayList<>();
        List<Integer> columnXs = new ArrayList<>();

        int nextX = 0;
        for (int i = 0; i < cisternCount; i++) {
            int x = nextX;
            columnXs.add(x);
            nextX += (i % 2 == 0) ? 8 : 4;

            Spring spring = new Spring(x, springY);
            Pump pump = new Pump(x, pumpY);
            Cistern cistern = new Cistern(x, cisternY);

            List<Pipe> upChain = createPipeChainBetween(spring, pump);
            List<Pipe> downChain = createPipeChainBetween(pump, cistern);

            if (!upChain.isEmpty() && !downChain.isEmpty()) {
                pump.setDirection(upChain.get(upChain.size() - 1).getEnd2(), downChain.get(0).getEnd1());
            }

            springs.add(spring);
            pumps.add(pump);
            cisterns.add(cistern);
            pipes.addAll(upChain);
            pipes.addAll(downChain);
        }

        for (int i = 0; i < pumps.size() - 1; i++) {
            int leftX = columnXs.get(i);
            int rightX = columnXs.get(i + 1);
            int gap = rightX - leftX;
            boolean useJunction = gap >= 8 && i % 3 == 0;

            if (useJunction) {
                int junctionX = leftX + (gap / 2);
                Pump junction = new Pump(junctionX, pumpY);
                List<Pipe> leftChain = createPipeChainBetween(pumps.get(i), junction);
                List<Pipe> rightChain = createPipeChainBetween(junction, pumps.get(i + 1));

                if (!leftChain.isEmpty() && !rightChain.isEmpty()) {
                    junction.setDirection(leftChain.get(leftChain.size() - 1).getEnd2(), rightChain.get(0).getEnd1());
                }

                junctions.add(junction);
                pipes.addAll(leftChain);
                pipes.addAll(rightChain);
                addSideBranch(i, junction, pumpY, springs, cisterns, pipes);
            } else {
                List<Pipe> chain = createPipeChainBetween(pumps.get(i), pumps.get(i + 1));
                pipes.addAll(chain);
            }
        }

        List<Element> all = new ArrayList<>();
        all.addAll(springs);
        all.addAll(cisterns);
        all.addAll(pumps);
        all.addAll(junctions);
        all.addAll(pipes);
        addElements(all);

        spawnPoint = pumps.isEmpty() ? null : pumps.get(0);
    }

    private void addSideBranch(
            int index,
            Pump junction,
            int pumpY,
            List<Spring> springs,
            List<Cistern> cisterns,
            List<Pipe> pipes) {
        int junctionX = junction.getX();
        if (index % 2 == 0) {
            Spring sideSpring = new Spring(junctionX, pumpY - 2);
            Pipe branch = new Pipe(junctionX, pumpY - 1);
            branch.connectBothEnds(sideSpring, junction);
            springs.add(sideSpring);
            pipes.add(branch);
        } else {
            Cistern sideCistern = new Cistern(junctionX, pumpY + 2);
            Pipe branch = new Pipe(junctionX, pumpY + 1);
            branch.connectBothEnds(junction, sideCistern);
            cisterns.add(sideCistern);
            pipes.add(branch);
        }
    }

    private List<Pipe> createPipeChainBetween(ActiveElement start, ActiveElement end) {
        List<Pipe> chainPipes = new ArrayList<>();
        List<ActiveElement> chain = new ArrayList<>();

        chain.add(start);

        if (start.getX() == end.getX()) {
            int step = start.getY() < end.getY() ? 1 : -1;
            for (int y = start.getY() + step; y != end.getY(); y += step) {
                Pipe pipe = new Pipe(start.getX(), y);
                chain.add(pipe);
                chainPipes.add(pipe);
            }
        } else if (start.getY() == end.getY()) {
            int step = start.getX() < end.getX() ? 1 : -1;
            for (int x = start.getX() + step; x != end.getX(); x += step) {
                Pipe pipe = new Pipe(x, start.getY());
                chain.add(pipe);
                chainPipes.add(pipe);
            }
        }

        chain.add(end);

        for (int i = 1; i < chain.size() - 1; i++) {
            Pipe pipe = (Pipe) chain.get(i);
            ActiveElement prev = chain.get(i - 1);
            ActiveElement next = chain.get(i + 1);
            pipe.connectBothEnds(prev, next);
        }

        return chainPipes;
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