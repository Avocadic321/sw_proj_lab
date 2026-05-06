package software.project.utils;

public class Helper {

    private Helper() {
    }

    public static ElementWaterState waterToBePumpedOut(int amount, int waterPerTurn, int currentWater, int capacity, Runnable onCapacityExceeded) {
        int toBePumped = Math.min(waterPerTurn, amount + currentWater);
        currentWater = amount + currentWater - toBePumped;
        if (currentWater > capacity) {
            onCapacityExceeded.run();
        }
        return new ElementWaterState(toBePumped, currentWater);
    }
}
