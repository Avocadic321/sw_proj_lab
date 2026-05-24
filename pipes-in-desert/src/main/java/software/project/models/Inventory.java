package software.project.models;

import software.project.map.interfaces.ICarriable;

public class Inventory {

    private final ICarriable[] inventory;
    private int size;

    public Inventory(int capacity) {
        inventory = new ICarriable[capacity];
        size = 0;
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

    public int getSize() {
        return size;
    }
}
