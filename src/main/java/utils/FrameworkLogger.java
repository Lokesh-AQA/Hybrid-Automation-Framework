package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FrameworkLogger {

	private static final Logger logger = LogManager.getLogger(FrameworkLogger.class);

	// INFO
	public static void info(String message) {
		logger.info(message);
	}

	// PASS
	public static void pass(String message) {
		logger.info("[PASS] " + message);
	}

	// FAIL
	public static void fail(String message) {
		logger.error("[FAIL] " + message);
	}

	// WARNING
	public static void warn(String message) {
		logger.warn("[WARN] " + message);
	}

	// DEBUG
	public static void debug(String message) {
		logger.debug(message);
	}

	// ===========================
	// Framework Log Methods
	// ===========================

	public static void browserOpened(String browser) {
		pass(browser + " Browser Opened Successfully.");
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

	public static void browserClosed() {
		pass("Browser Closed Successfully.");
	}

	public static void elementCleared(String element) {
		pass(element + " Cleared Successfully.");
		
	}

	public static void pressKey(String testData) {
		pass(testData + " Key Entered.");
		
	}

}