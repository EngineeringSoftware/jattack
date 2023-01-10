package org.csutil.log;

/**
 * Utility class for logging.
 */
public class Log {

    public enum Level {
        DEBUG("DEBUG", 2),
        INFO("INFO", 1),
        ERROR("ERROR", 0);

        private final String name;
        private final int order;

        Level(String name, int order) {
            this.name = name;
            this.order = order;
        }
    }

    private static final String MSG_PREFIX = "CSUTIL:";

    private static Level level = Level.ERROR;

    public static void setLevel(String newLevel) {
        switch (newLevel) {
        case "debug":
            setLevel(Level.DEBUG);
            break;
        case "info":
            setLevel(Level.INFO);
            break;
        case "error":
            setLevel(Level.ERROR);
            break;
        default:
            throw new RuntimeException("Unrecognized logging level: " + newLevel + "!");
        }
    }

    public static void setLevel(Level newLevel) {
        level = newLevel;
    }

    public static Level getLevel() {
        return level;
    }

    public static void debug(Object msg) {
        log(msg, Level.DEBUG);
    }

    public static void info(Object msg) {
        log(msg, Level.INFO);
    }

    public static void error(Object msg) {
        log(msg, Level.ERROR);
    }

    // We want to use this method in tests so I made it
    // package-private.
    static String getFullLogMessage(Object msg, Level logLevel) {
        return MSG_PREFIX + logLevel.name + ": " + msg + System.lineSeparator();
    }

    private static void log(Object msg, Level logLevel) {
        if (level.order < logLevel.order) {
            return;
        }
        System.out.print(getFullLogMessage(msg, logLevel));
    }
}
