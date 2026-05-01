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
     * B# - Pipe - maybe you get it why is B :)
     * P# - Pump
     * C# - Cistern
     * S# - Spring
     * === or || - Pipes
     *
     * Spawn point: P1
     */
    private void buildMap() {
        IdGenerator.reset();


    }

}
