package modloader.konstruct.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class logger {
    private static final Logger LOGGER = LogManager.getLogger("Konstruct");

    public static void info(String msg) {
        LOGGER.info(msg);
    }

    public static void warn(String msg) {
        LOGGER.warn(msg);
    }

    public static void error(String msg) {
        LOGGER.error(msg);
    }

    public static void debug(String msg) {
        LOGGER.debug(msg);
    }

    public static void trace(String msg) {
        LOGGER.trace(msg);
    }

    // Overloads with Throwable for error and warn
    public static void error(String msg, Throwable t) {
        LOGGER.error(msg, t);
    }

    public static void warn(String msg, Throwable t) {
        LOGGER.warn(msg, t);
    }
}
