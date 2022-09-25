package sketchy.driver;

/**
 * Defines the search strategies.
 */
public enum SearchStrategy {

    /**
     * Systematic explore the whole search space across holes.
     */
    SYSTEMATIC,

    /**
     * Randomly explore each hole independently.
     */
    RANDOM,

    /**
     * Systematically explore each hole independently.
     */
    SMART
}
