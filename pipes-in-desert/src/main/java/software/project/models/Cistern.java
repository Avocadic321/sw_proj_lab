package software.project.models;

import java.util.List;

import software.project.interfaces.IConnectable;

/**
 * Storage element that receives water and can produce new components.
 */
public class Cistern extends ActiveElement implements IConnectable {
    /** Current amount of water stored. */
    private int storedWater;
    /** Maximum water capacity. */
    private int capacity;

    /** Currently stored pipe */
    private Pipe storedPipe;

    /** Currently stored pump */
    private Pump storedPump;



    /**
     * Accepts incoming water.
     *
   //  * @param amount amount of water received
     */
    @Override
    public void receiveAndTransferWater() {

        if(isFull()) return;
      //  int newAmount = storedWater + amount;
    //    storedWater = newAmount > capacity ? capacity : storedWater + newAmount;


    }

    /**
     * Indicates whether the cistern is full.
     *
     * @return true if full
     */
    public boolean isFull() {
        System.out.println("[Cistern] isFull()");
        return storedWater >=  capacity;
    }

    /**
     * Produces a new pipe component.
     *
     * @return new pipe instance
     */
    public void producePipe() {
        if(storedPipe != null) return;
        System.out.println("[Cistern] producePipe()");
       Pipe pipe = new Pipe();
       this.connect(pipe.getEnd1());
       this.storedPipe = pipe;
    }

    public Pipe pickUpPipe() {
        Pipe pipe = storedPipe;
        pipe.getEnd1().disconnect();
        this.disconnect(pipe.getEnd1());
        storedPipe = null;
        return pipe;
    }
    /**
     * Produces a new pump component.
     *
     * @return new pump instance
     */
    public void producePump() {
        System.out.println("[Cistern] producePump()");
        this.storedPump = new Pump();
    }

    public Pump pickUpPump() {
        Pump pump = storedPump;
        storedPump = null;
        return pump;
    }



    /**
     * Connects a pipe end to this cistern.
     *
     * @param end pipe end to connect
     */
    @Override
    public void connect(PipeEnd end) {
        System.out.println("[Cistern] connect()");
        super.connect(end);
    }

    /**
     * Disconnects a pipe end from this cistern.
     *
     * @param end pipe end to disconnect
     */
    @Override
    public void disconnect(PipeEnd end) {
        System.out.println("[Cistern] disconnect()");
        super.disconnect(end);
    }

    /**
     * Returns currently connected pipe ends.
     *
     * @return list of connected pipe ends
     */
    @Override
    public List<PipeEnd> getConnections() {
        System.out.println("[Cistern] getConnections()");
        return super.getConnections();
    }
}
