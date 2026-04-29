package software.project.models;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final List<Element> elements = new ArrayList<>();

    public GameMap() {
        // TODO: Create Mock Map for Prototype
    }

    public void addElement(Element element) {
        if (element != null) {
            elements.add(element);
        }
    }

    public void removeElement(Element element) {
        elements.remove(element);
    }

    public Element getElement(String id) {
        for (Element element : elements) {
            if (element.getId().equals(id)) {
                return element;
            }
        }
        return null;
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


}
