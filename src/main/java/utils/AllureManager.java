package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.model.Status;

public final class AllureManager {

	// ==========================================================
	// ALLURE DIRECTORY CONFIGURATION
	// ==========================================================

	/*
	 * Allure is completely independent from Extent Reports.
	 *
	 * Final structure:
	 *
	 * Allure-Reports/ └── 2026-08-09_09-30-00/ ├── Allure-results/ └──
	 * Allure-report/
	 *
	 * Extent remains:
	 *
	 * Reports/ └── 2026-08-09_09-30-00/ ├── ExtentReport.html └── Screenshots/
	 */

	private static final String ALLURE_ROOT_FOLDER = "Allure-Reports";

	private static final String RESULTS_FOLDER = "Allure-results";

	/*
	 * ONE Allure execution directory for the complete suite.
	 *
	 * This is initialized only once.
	 */
	private static String executionDirectory;

	/*
	 * Each execution thread maintains its own Allure test state.
	 */
	private static final ThreadLocal<Boolean> ALLURE_TEST_RUNNING = ThreadLocal.withInitial(() -> false);

	private AllureManager() {
		// Utility class
	}

	// ==========================================================
	// INITIALIZE
	// ==========================================================

	public static synchronized void initialize() {

		/*
		 * Create the Allure execution directory once.
		 */
		getExecutionDirectory();

		/*
		 * Create Allure-results and framework metadata.
		 */
		createResultsFolder();

		createEnvironmentFile();

		createExecutorFile();

		createCategoriesFile();
	}

	// ==========================================================
	// GET ALLURE EXECUTION DIRECTORY
	// ==========================================================

	/**
	 * Returns the single Allure execution directory for the current TestNG suite.
	 *
	 * Example:
	 *
	 * Allure-Reports/ └── 2026-08-09_09-30-00/
	 */
	public static synchronized String getExecutionDirectory() {

		/*
		 * IMPORTANT:
		 *
		 * Do NOT create a new timestamp every time this method is called.
		 *
		 * One execution = one directory.
		 */
		if (executionDirectory != null) {

			return executionDirectory;
		}

		String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

		File directory = new File(System.getProperty("user.dir"), ALLURE_ROOT_FOLDER + File.separator + timestamp);

		if (!directory.exists()) {

			if (!directory.mkdirs() && !directory.exists()) {

				throw new IllegalStateException(
						"Unable to create Allure execution directory : " + directory.getAbsolutePath());
			}
		}

		executionDirectory = directory.getAbsolutePath();

		return executionDirectory;
	}

	// ==========================================================
	// GET ALLURE RESULTS DIRECTORY
	// ==========================================================

	/**
	 * Returns:
	 *
	 * Allure-Reports/ └── <Date-Time>/ └── Allure-results/
	 */
	public static synchronized String getResultsDirectory() {

		File resultsDirectory = new File(getExecutionDirectory(), RESULTS_FOLDER);

		if (!resultsDirectory.exists()) {

			if (!resultsDirectory.mkdirs() && !resultsDirectory.exists()) {

				throw new IllegalStateException(
						"Unable to create Allure results directory : " + resultsDirectory.getAbsolutePath());
			}
		}

		return resultsDirectory.getAbsolutePath();
	}

	// ==========================================================
	// CREATE RESULTS FOLDER
	// ==========================================================

	private static void createResultsFolder() {

		File folder = new File(getResultsDirectory());

		if (!folder.exists()) {

			if (!folder.mkdirs() && !folder.exists()) {

				throw new IllegalStateException("Unable to create Allure results folder : " + folder.getAbsolutePath());
			}
		}
	}

	// ==========================================================
	// ENVIRONMENT FILE
	// ==========================================================

	private static void createEnvironmentFile() {

		try {

			Properties properties = new Properties();

			properties.setProperty("Framework", "Hybrid Automation Framework");

			properties.setProperty("Execution", "Local");

			String environment = ConfigUtils.getProperty("environment");

			if (environment == null || environment.trim().isEmpty()) {

				environment = "QA";
			}

			properties.setProperty("Environment", environment);

			properties.setProperty("Browser", ConfigUtils.getRequiredProperty("browser"));

			properties.setProperty("Operating System", System.getProperty("os.name"));

			properties.setProperty("OS Version", System.getProperty("os.version"));

			properties.setProperty("Java Version", System.getProperty("java.version"));

			properties.setProperty("User", System.getProperty("user.name"));

			File environmentFile = new File(getResultsDirectory(), "environment.properties");

			try (FileOutputStream output = new FileOutputStream(environmentFile)) {

				properties.store(output, "Allure Environment");
			}

		} catch (IOException e) {

			FrameworkLogger.error("Unable to create Allure environment file : " + e.getMessage());
		}
	}

	// ==========================================================
	// EXECUTOR FILE
	// ==========================================================

	private static void createExecutorFile() {

		try {

			StringBuilder json = new StringBuilder();

			json.append("{\n");
			json.append("  \"name\": \"Local Machine\",\n");
			json.append("  \"type\": \"local\",\n");
			json.append("  \"buildName\": \"Hybrid Automation Framework\",\n");
			json.append("  \"buildOrder\": 1,\n");
			json.append("  \"reportName\": \"Automation Execution Report\"\n");
			json.append("}");

			File executorFile = new File(getResultsDirectory(), "executor.json");

			try (FileWriter writer = new FileWriter(executorFile)) {

				writer.write(json.toString());
			}

		} catch (IOException e) {

			FrameworkLogger.error("Unable to create Allure executor file : " + e.getMessage());
		}
	}

	// ==========================================================
	// CATEGORIES FILE
	// ==========================================================

	private static void createCategoriesFile() {

		try {

			String json = """
					[
					  {
					    "name": "Assertion Failure",
					    "matchedStatuses": ["failed"],
					    "messageRegex": ".*Assertion.*"
					  },
					  {
					    "name": "Element Not Found",
					    "matchedStatuses": ["failed"],
					    "traceRegex": ".*NoSuchElementException.*"
					  },
					  {
					    "name": "Timeout",
					    "matchedStatuses": ["failed"],
					    "traceRegex": ".*TimeoutException.*"
					  },
					  {
					    "name": "Stale Element",
					    "matchedStatuses": ["failed"],
					    "traceRegex": ".*StaleElementReferenceException.*"
					  },
					  {
					    "name": "WebDriver Error",
					    "matchedStatuses": ["broken"],
					    "traceRegex": ".*WebDriverException.*"
					  },
					  {
					    "name": "Framework Error",
					    "matchedStatuses": ["broken"],
					    "traceRegex": ".*RuntimeException.*"
					  }
					]
					""";

			File categoriesFile = new File(getResultsDirectory(), "categories.json");

			try (FileWriter writer = new FileWriter(categoriesFile)) {

				writer.write(json);
			}

		} catch (IOException e) {

			FrameworkLogger.error("Unable to create Allure categories file : " + e.getMessage());
		}
	}

	// ==========================================================
	// ALLURE TEST STATE
	// ==========================================================

	public static void startTest() {

		ALLURE_TEST_RUNNING.set(true);
	}

	public static void stopTest() {

		ALLURE_TEST_RUNNING.set(false);
	}

	private static boolean isTestRunning() {

		return ALLURE_TEST_RUNNING.get();
	}

	// ==========================================================
	// INFO
	// ==========================================================

	public static void logInfo(String message) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step(message);
	}

	// ==========================================================
	// STEP
	// ==========================================================

	public static void logStep(String message) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step(message);
	}

	// ==========================================================
	// PASS
	// ==========================================================

	public static void logPass(String message) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step(message, Status.PASSED);
	}

	// ==========================================================
	// WARNING
	// ==========================================================

	public static void logWarn(String message) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step(message, Status.BROKEN);
	}

	// ==========================================================
	// FAIL
	// ==========================================================

	public static void logFail(String message) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step(message, Status.FAILED);
	}

	// ==========================================================
	// ERROR
	// ==========================================================

	public static void logError(String message) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step("[ERROR] " + message, Status.BROKEN);
	}

	public static void logError(String message, Throwable throwable) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step("[ERROR] " + message, Status.BROKEN);

		if (throwable != null) {

			Allure.addAttachment("Exception", throwable.toString());
		}
	}

	// ==========================================================
	// DEBUG
	// ==========================================================

	public static void logDebug(String message) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step("[DEBUG] " + message);
	}

	// ==========================================================
	// SCREENSHOT ATTACHMENT
	// ==========================================================

	public static void attachScreenshot(String attachmentName, String screenshotPath) {

		if (screenshotPath == null || screenshotPath.isBlank()) {

			return;
		}

		try {

			Allure.addAttachment(attachmentName, new FileInputStream(screenshotPath));

		} catch (FileNotFoundException e) {

			FrameworkLogger.error("Unable to attach screenshot.", e);
		}
	}

	// ==========================================================
	// RETRY INFORMATION
	// ==========================================================

	public static void addRetryInformation(boolean retryAttempted, int retryCount, int maxRetry, int totalAttempts,
			String finalResult) {

		if (!isTestRunning()) {
			return;
		}

		String message = "Retry Information\n" + "Retry Analyzer : " + (retryAttempted ? "ATTEMPTED" : "NOT ATTEMPTED")
				+ "\n" + "Retry Count    : " + retryCount + "\n" + "Max Retry      : " + maxRetry + "\n"
				+ "Total Attempts : " + totalAttempts + "\n" + "Final Result   : " + finalResult;

		Allure.step(message);
	}

	// ==========================================================
	// ALLURE STEP
	// ==========================================================

	@Step("{stepName}")
	public static void step(String stepName) {

		Allure.step(stepName);
	}

	// ==========================================================
	// KEYWORD DISPLAY NAME
	// ==========================================================

	public static String buildKeywordStep(String keyword, String objectName, String testData) {

		String displayName = (objectName == null || objectName.isBlank()) ? ""
				: PropertyUtils.getDisplayName(objectName);

		switch (keyword.toLowerCase()) {

		case "navigate":
			return "Navigate to \"" + testData + "\"";

		case "input":
			return "Enter \"" + testData + "\" into " + displayName;

		case "click":
			return "Click " + displayName;

		case "clear":
			return "Clear " + displayName;

		case "presskey":
			return "Press " + testData + " key";

		case "verifydisplayed":
			return "Verify " + displayName + " is displayed";

		case "verifyenabled":
			return "Verify " + displayName + " is enabled";

		case "verifydisabled":
			return "Verify " + displayName + " is disabled";

		case "verifyselected":
			return "Verify " + displayName + " is selected";

		case "verifytitle":
			return "Verify page title is \"" + testData + "\"";

		case "verifyurl":
			return "Verify current URL is \"" + testData + "\"";

		case "verifytext":
			return "Verify text of " + displayName + " is \"" + testData + "\"";

		case "verifyvalue":
			return "Verify value of " + displayName + " is \"" + testData + "\"";

		case "verifyattribute":
			return "Verify attribute \"" + testData + "\" of " + displayName;

		case "getattribute":
			return "Get attribute \"" + testData + "\" from " + displayName;

		case "opennewtab":
			return "Open new browser tab";

		case "switchtab":
			return "Switch to tab \"" + testData + "\"";

		default:

			return Character.toUpperCase(keyword.charAt(0)) + keyword.substring(1);
		}
	}
}