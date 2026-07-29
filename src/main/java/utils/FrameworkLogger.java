package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import reports.ExtentTestManager;

public final class FrameworkLogger {

    private static final Logger logger = LogManager.getLogger(FrameworkLogger.class);

    // Prevent instantiation
    private FrameworkLogger() {
    }

    // ===========================
    // Generic Log Methods
    // ===========================

    // INFO
    public static void info(String message) {
        logger.info("[INFO] " + message);
        ExtentTestManager.info(message);
    }

    // STEP
    public static void step(String message) {
        logger.info("[STEP] " + message);
        ExtentTestManager.info("STEP : " + message);
    }

    // PASS
    public static void pass(String message) {
        logger.info("[PASS] " + message);
        ExtentTestManager.pass(message);
    }

    // FAIL (Business/Test Failure)
    public static void fail(String message) {
        logger.error("[FAIL] " + message);
        ExtentTestManager.fail(message);
    }

    // ERROR (Framework/Infrastructure Failure)
    public static void error(String message) {
        logger.error("[ERROR] " + message);
        ExtentTestManager.fail(message);
    }

    // WARNING
    public static void warn(String message) {
        logger.warn("[WARN] " + message);
        ExtentTestManager.warning(message);
    }

    // DEBUG
    public static void debug(String message) {
        logger.debug("[DEBUG] " + message);
    }

    // ===========================
    // Keyword Specific Log Methods
    // ===========================

    public static void browserOpened(String browser) {
        pass(browser + " Browser Opened Successfully.");
    }

    public static void browserClosed() {
        pass("Browser Closed Successfully.");
    }

    public static void urlLaunched(String url) {
        pass("URL Launched Successfully. URL : " + url);
    }

    public static void valueEntered(String field, String value) {
        pass(field + " Entered Successfully. Value : " + value);
    }

    public static void elementClicked(String element) {
        pass(element + " Clicked Successfully.");
    }

    public static void elementCleared(String element) {
        pass(element + " Cleared Successfully.");
    }

    public static void keyPressed(String key) {
        pass(key + " Key Pressed.");
    }
}