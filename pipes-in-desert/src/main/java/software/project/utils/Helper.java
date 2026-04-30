package software.project.utils;

public class Helper {

    public static int waterToBePumpedOut(int amount, int waterPerTurn, int currentWater, int capacity, Runnable onCapacityExceeded) {
        int toBePumped = Math.min(waterPerTurn, amount + currentWater);
        currentWater = amount + currentWater - toBePumped;
        if(currentWater > capacity) {
            onCapacityExceeded.run();
        }
        return currentWater;
    }
}
