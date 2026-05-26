package software.project.models;

import software.project.map.Element;
import software.project.map.interfaces.ICarriable;

public class Inventory {

    private final ICarriable[] inventory;
    private int size;
    private int capacity;

    public Inventory(int capacity) {
        inventory = new ICarriable[capacity];
        size = 0;
        this.capacity = capacity;
    }

    public ICarriable[] getInventory() {
        return inventory;
    }
    public ICarriable get(int index) {
        if (index < 0 || index >= inventory.length) {
            return null;
        }

        return inventory[index];
    }
    public ICarriable get(ICarriable item) {
        for(int i = 0; i < inventory.length; i++) {
            if(item == inventory[i]) return item;
        }
        return null;
    }

    public boolean add(ICarriable item) {
        if (size == inventory.length) {
            return false;
        }

        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                inventory[i] = item;
                size++;
                return true;
            }
        }

        return false;
    }

    public ICarriable remove(int index) {
        if (index < 0 || index >= inventory.length) {
            return null;
        }

        ICarriable item = inventory[index];

        if (item != null) {
            inventory[index] = null;
            size--;
        }

        return item;
    }

    public boolean isFull() {
        return size == capacity;
    }
    public int getSize() {
        return size;
    }
    public boolean removeItem(ICarriable item) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == item) {
                inventory[i] = null;
                size--;
                return true;
            }
        }
        return false;
    }

    /**
     * Removes an item from inventory by its unique ID.
     * @param id the unique identifier of the item to remove
     * @return the removed item, or null if not found
     */
    public ICarriable removeById(String id) {
        for (int i = 0; i < inventory.length; i++) {
            ICarriable item = inventory[i];
            if (item instanceof Element element && element.getId().equals(id)) {
                inventory[i] = null;
                size--;
                return item;
            }
        }
        return null;
    }

    /**
     * Removes an item from inventory by object reference.
     * @param item the item to remove
     * @return true if removed, false if not found
     */


    /**
     * Removes an item from inventory by slot index.
     * @param index the slot index to remove
     * @return the removed item, or null if invalid index or empty slot
     */
    public ICarriable removeByIndex(int index) {
        if (index < 0 || index >= inventory.length) {
            return null;
        }
        ICarriable item = inventory[index];
        if (item != null) {
            inventory[index] = null;
            size--;
        }
        return item;
    }

    /**
     * Finds an item in inventory by its unique ID.
     * @param id the unique identifier to search for
     * @return the item if found, null otherwise
     */
    public ICarriable findById(String id) {
        for (ICarriable item : inventory) {
            if (item instanceof Element element && element.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
}
