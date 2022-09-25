package org.csutil.log;

public class Log {

    public enum Level {
        INFO("INFO", 2),
        DEBUG("DEBUG", 1),
        ERROR("ERROR", 0);

        private final String name;
        private final int order;
        Level(String name, int order) {
            this.name = name;
            this.order = order;
        }
    }

    private static final String MSG_PREFIX = "Sketchy:";

    public static Level level = Level.ERROR;

    public static void info(Object msg) {
        if (level.order < Level.INFO.order) {
            return;
        }
        log(msg, "INFO");
    }

    public static void debug(Object msg) {
        if (level.order < Level.DEBUG.order) {
            return;
        }
        log(msg, "DEBUG");
    }

    public static void error(Object msg) {
        if (level.order < Level.ERROR.order) {
            return;
        }
        log(msg, "ERROR");
    }

    private static void log(Object msg, String level) {
        System.out.println(MSG_PREFIX + level + ": " + msg);
    }
}
