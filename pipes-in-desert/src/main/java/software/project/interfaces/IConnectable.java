package software.project.interfaces;

import software.project.models.PipeEnd;

import java.util.List;

/**
 * Defines the behavior for game elements that can serve as connection points in the pipe network.
 * <p>
 * Classes implementing this interface (specifically {@link software.project.models.Pump},
 * {@link software.project.models.Cistern}, and {@link software.project.models.Spring})
 * can have {@link PipeEnd} objects attached to or detached from them, thereby forming
 * the links that constitute the water transport infrastructure.
 * </p>
 * <p>
 * This interface provides a uniform way to manage the network structure across
 * different types of active elements. Each connectable element has a maximum
 * number of connections it can support (e.g., a pump's maxConnections limit).
 * </p>
 */
public interface IConnectable {
    /**
     * Attaches the specified pipe end to this element.
     * <p>
     * The operation succeeds only if the element has not yet reached its
     * connection limit (e.g., a pump's maxConnections) and the pipe end
     * is currently free.
     * </p>
     *
     * @param end the pipe end to attach to this element
     */
    void connect(PipeEnd end);

    /**
     * Detaches the specified pipe end from this element.
     * <p>
     * The pipe end must be currently connected to this element.
     * After disconnection, the pipe end becomes free and the element
     * removes the pipe end from its connections list.
     * </p>
     *
     * @param end the pipe end to detach from this element
     */
    void disconnect(PipeEnd end);

    /**
     * Returns the list of pipe ends currently connected to this element.
     *
     * @return a list containing all pipe ends attached to this element
     */
    List<PipeEnd> getConnections();
}
