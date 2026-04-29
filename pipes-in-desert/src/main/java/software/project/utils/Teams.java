package software.project.utils;

/**
 * Identifies the two opposing teams in the game.
 * <p>
 * The team type determines the role-specific rules, available actions,
 * and scoring criteria for all players belonging to that team.
 * </p>
 * <p>
 * The two teams have opposing objectives:
 * <ul>
 * <li>{@link #PLUMBERS} - Guardians of the water infrastructure, focused on
 * delivering water to cisterns and maintaining the pipe network</li>
 * <li>{@link #SABOTEURS} - Disruptors who aim to cause water leakage and
 * prevent water from reaching its destination</li>
 * </ul>
 * </p>
 *
 * @see software.project.models.Team
 * @see software.project.models.Plumber
 * @see software.project.models.Saboteur
 * @since 1.0
 */
public enum Teams {
    PLUMBERS, SABOTEURS
}
