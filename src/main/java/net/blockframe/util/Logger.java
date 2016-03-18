package net.blockframe.util;

/**
 * A logger system for BlockFrame.
 */
public final class Logger {
    /**
     * Logs a pure message to console.
     * @param msg the message
     */
    public static void log(String msg) {
        System.out.println(msg);
    }

    /**
     * Logs a message w/ the blockframe brand.
     * @param msg the message
     */
    public static void brandLog(String msg) {
        log("BlockFrame: " + msg);
    }

    /**
     * Logs a error message using brand format to console.
     * @param msg the message
     */
    public static void error(String msg) {
        brandLog("Error: " + msg);
    }

    /**
     * Logs a info message using brand format to console.
     * @param msg the message
     */
    public static void info(String msg) {
        brandLog("Info: " + msg);
    }

    /**
     * Logs a warning message using brand format to console.
     * @param msg the message
     */
    public static void warn(String msg) {
        brandLog("Warning: " + msg);
    }
}
