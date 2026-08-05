package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import io.qameta.allure.Step;

public final class AllureManager {

	private static final String RESULTS_FOLDER = "allure-results";
	private static final ThreadLocal<Boolean> ALLURE_TEST_RUNNING = ThreadLocal.withInitial(() -> false);

	private AllureManager() {
	}

	public static void initialize() {

		createResultsFolder();

		createEnvironmentFile();
		createExecutorFile();
		createCategoriesFile();

	}

	private static void createResultsFolder() {

		File folder = new File(RESULTS_FOLDER);

		if (!folder.exists()) {
			folder.mkdirs();
		}

	}

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

			try (FileOutputStream output = new FileOutputStream(RESULTS_FOLDER + "/environment.properties")) {

				properties.store(output, "Allure Environment");

			}

		} catch (IOException e) {

			FrameworkLogger.error("Unable to create Allure environment file : " + e.getMessage());

		}

	}

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

			try (FileWriter writer = new FileWriter(RESULTS_FOLDER + "/executor.json")) {

				writer.write(json.toString());

			}

		} catch (IOException e) {

			FrameworkLogger.error("Unable to create Allure executor file : " + e.getMessage());

		}

	}

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

			try (FileWriter writer = new FileWriter(RESULTS_FOLDER + "/categories.json")) {

				writer.write(json);

			}

		} catch (IOException e) {

			FrameworkLogger.error("Unable to create Allure categories file : " + e.getMessage());

		}

	}
	// ==========================================================
	// Allure Logging
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

	public static void logInfo(String message) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step(message);
	}

	public static void logStep(String message) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step(message);
	}

	public static void logPass(String message) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step(message, Status.PASSED);
	}

	public static void logWarn(String message) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step(message, Status.BROKEN);
	}

	public static void logFail(String message) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step(message, Status.FAILED);
	}

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

		Allure.addAttachment("Exception", throwable.toString());

	}

	public static void logDebug(String message) {

		if (!isTestRunning()) {
			return;
		}

		Allure.step("[DEBUG] " + message);
	}

	public static void attachScreenshot(String attachmentName,
            String screenshotPath) {

		if (screenshotPath == null || screenshotPath.isBlank()) {
			return;
		}

		try {

			Allure.addAttachment(
			        attachmentName,
			        new FileInputStream(screenshotPath));

		} catch (FileNotFoundException e) {

			FrameworkLogger.error("Unable to attach screenshot.", e);

		}

	}

	@Step("{stepName}")
	public static void step(String stepName) {

		Allure.step(stepName);

	}

	public static String buildKeywordStep(String keyword, String objectName, String testData) {

	    String displayName = (objectName == null || objectName.isBlank())
	            ? ""
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
	        return Character.toUpperCase(keyword.charAt(0))
	                + keyword.substring(1);
	    }
	}

}