package software.project.map.interfaces;

/**
 * Marks elements that a plumber can pick up and carry.
 * <p>
 * This is a marker interface with no methods. Only pipes and pumps implement this interface, as they are the only
 * components that can be transported by plumbers across the pipe network.
 * </p>
 * <p>
 * Carriable elements can be picked up from cisterns, carried while the plumber moves, and then placed elsewhere in the
 * network to extend or modify the pipe system.
 * </p>
 */
public interface ICarriable {
}
