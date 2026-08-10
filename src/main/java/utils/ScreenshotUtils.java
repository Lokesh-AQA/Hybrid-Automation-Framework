package utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import enums.ScreenshotType;
import reports.ExtentManager;

public final class ScreenshotUtils {

	/*
	 * Each execution thread maintains its own screenshot counter.
	 *
	 * Every test attempt starts from 001.
	 */
	private static final ThreadLocal<Integer> SCREENSHOT_COUNTER = ThreadLocal.withInitial(() -> 1);

	/*
	 * Current test case name for the execution thread.
	 */
	private static final ThreadLocal<String> CURRENT_TEST_CASE = new ThreadLocal<>();

	private ScreenshotUtils() {
		// Utility class
	}

	// ==========================================================
	// START TEST
	// ==========================================================

	/**
	 * Starts screenshot handling for a test attempt.
	 *
	 * IMPORTANT:
	 *
	 * There is NO Temp folder.
	 *
	 * Screenshots are stored directly inside:
	 *
	 * Screenshots/ ├── Pass/ └── Fail/
	 *
	 * Before every retry attempt, screenshots belonging to the previous attempt are
	 * deleted.
	 */
	public static void startTest(String testCaseName) {

		if (testCaseName == null || testCaseName.isBlank()) {

			FrameworkLogger.warn("Screenshot tracking skipped. " + "Test case name is empty.");

			return;
		}

		try {

			CURRENT_TEST_CASE.set(testCaseName);

			/*
			 * Every new attempt starts from 001.
			 */
			SCREENSHOT_COUNTER.set(1);

			/*
			 * Remove screenshots from any previous attempt.
			 *
			 * This prevents retry screenshots from being mixed.
			 */
			deletePreviousScreenshots(testCaseName);

		} catch (Exception e) {

			FrameworkLogger.error("Unable to initialize screenshot tracking for test : " + testCaseName, e);
		}
	}

	// ==========================================================
	// CAPTURE SCREENSHOT
	// ==========================================================

	/**
	 * Captures a screenshot and stores it directly inside Pass or Fail folder.
	 *
	 * Example:
	 *
	 * Screenshots/ └── Pass/ ├── TestCaseName_001_navigate_20260809_120000_123.png
	 * ├── TestCaseName_002_input_20260809_120001_456.png └── ...
	 *
	 * @param driver      WebDriver instance
	 * @param keywordName Keyword name
	 * @param status      PASS or FAIL
	 *
	 * @return Absolute screenshot path, or null if capture fails
	 */
	public static String capture(WebDriver driver, String keywordName, ScreenshotType status) {

		if (driver == null) {

			FrameworkLogger.warn("Screenshot skipped. WebDriver is null.");

			return null;
		}

		try {

			// ==================================================
			// TEST CASE VALIDATION
			// ==================================================

			String testCaseName = CURRENT_TEST_CASE.get();

			if (testCaseName == null || testCaseName.isBlank()) {

				FrameworkLogger.warn("Screenshot skipped. " + "Test case context is not initialized.");

				return null;
			}

			// ==================================================
			// CAPTURE
			// ==================================================

			File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

			// ==================================================
			// TIMESTAMP
			// ==================================================

			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());

			// ==================================================
			// EXECUTION DIRECTORY
			// ==================================================

			String executionDirectory = ExtentManager.getExecutionDirectory();

			File executionFolder = new File(executionDirectory);

			if (!executionFolder.exists()) {

				if (!executionFolder.mkdirs() && !executionFolder.exists()) {

					throw new IllegalStateException("Unable to create execution directory : " + executionDirectory);
				}
			}

			// ==================================================
			// PASS / FAIL DIRECTORY
			// ==================================================

			String statusFolder;

			if (status == ScreenshotType.PASS) {

				statusFolder = "Pass";

			} else {

				statusFolder = "Fail";
			}

			File screenshotFolder = new File(executionFolder, "Screenshots" + File.separator + statusFolder);

			if (!screenshotFolder.exists()) {

				if (!screenshotFolder.mkdirs() && !screenshotFolder.exists()) {

					throw new IllegalStateException(
							"Unable to create screenshot directory : " + screenshotFolder.getAbsolutePath());
				}
			}

			// ==================================================
			// SCREENSHOT SEQUENCE
			// ==================================================

			int count = SCREENSHOT_COUNTER.get();

			String sequence = String.format("%03d", count);

			SCREENSHOT_COUNTER.set(count + 1);

			// ==================================================
			// SAFE NAMES
			// ==================================================

			String safeTestCaseName = sanitizeFileName(testCaseName);

			String safeKeywordName = sanitizeFileName(keywordName);

			// ==================================================
			// FILE NAME
			// ==================================================

			/*
			 * Example:
			 *
			 * executeKeywordDrivenScenario_001_navigate_ 20260809_193000_123.png
			 */
			String fileName = safeTestCaseName + "_" + sequence + "_" + safeKeywordName + "_" + timeStamp + ".png";

			File destinationFile = new File(screenshotFolder, fileName);

			// ==================================================
			// COPY SCREENSHOT
			// ==================================================

			FileUtils.copyFile(source, destinationFile);

			// ==================================================
			// RETURN PATH
			// ==================================================

			return destinationFile.getAbsolutePath();

		} catch (Exception e) {

			/*
			 * Screenshot failure must NEVER hide the original automation failure.
			 */
			System.err.println("Unable to capture screenshot : " + e.getMessage());

			return null;
		}
	}

	// ==========================================================
	// MOVE TEST CASE SCREENSHOTS
	// ==========================================================

	/**
	 * Moves all screenshots belonging to a test case from one result folder to
	 * another.
	 *
	 * Example:
	 *
	 * Pass/ TestCase_001_navigate.png TestCase_002_input.png
	 *
	 * becomes:
	 *
	 * Fail/ TestCase_001_navigate.png TestCase_002_input.png
	 *
	 * This is required when:
	 *
	 * capture.pass.screenshot=true capture.fail.screenshot=true
	 *
	 * and the test eventually fails.
	 */
	public static void moveTestCaseScreenshots(String testCaseName, String fromFolder, String toFolder) {

		if (testCaseName == null || testCaseName.isBlank()) {

			return;
		}

		if (fromFolder == null || fromFolder.isBlank() || toFolder == null || toFolder.isBlank()) {

			return;
		}

		try {

			String executionDirectory = ExtentManager.getExecutionDirectory();

			File screenshotsDirectory = new File(executionDirectory, "Screenshots");

			File sourceDirectory = new File(screenshotsDirectory, fromFolder);

			File destinationDirectory = new File(screenshotsDirectory, toFolder);

			if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {

				return;
			}

			if (!destinationDirectory.exists()) {

				if (!destinationDirectory.mkdirs() && !destinationDirectory.exists()) {

					throw new IllegalStateException("Unable to create destination screenshot directory : "
							+ destinationDirectory.getAbsolutePath());
				}
			}

			String safeTestCaseName = sanitizeFileName(testCaseName);

			File[] screenshots = sourceDirectory
					.listFiles(file -> file.isFile() && file.getName().startsWith(safeTestCaseName + "_")
							&& file.getName().toLowerCase().endsWith(".png"));

			if (screenshots == null || screenshots.length == 0) {

				return;
			}

			for (File screenshot : screenshots) {

				File destination = new File(destinationDirectory, screenshot.getName());

				/*
				 * FileUtils.moveFile() is safer than File.renameTo(), especially when moving
				 * between different file systems.
				 */
				FileUtils.moveFile(screenshot, destination);
			}

			FrameworkLogger
					.info("Screenshots moved from " + fromFolder + " to " + toFolder + " for test : " + testCaseName);

		} catch (Exception e) {

			FrameworkLogger.error("Unable to move screenshots for test : " + testCaseName, e);
		}
	}

	// ==========================================================
	// DELETE PREVIOUS ATTEMPT SCREENSHOTS
	// ==========================================================

	/**
	 * Deletes screenshots belonging to the current test case from both Pass and
	 * Fail folders.
	 *
	 * Called at the beginning of every attempt.
	 */
	private static void deletePreviousScreenshots(String testCaseName) {

		try {

			String executionDirectory = ExtentManager.getExecutionDirectory();

			File screenshotsDirectory = new File(executionDirectory, "Screenshots");

			if (!screenshotsDirectory.exists()) {

				return;
			}

			String safeTestCaseName = sanitizeFileName(testCaseName);

			deleteTestCaseScreenshotsFromFolder(new File(screenshotsDirectory, "Pass"), safeTestCaseName);

			deleteTestCaseScreenshotsFromFolder(new File(screenshotsDirectory, "Fail"), safeTestCaseName);

		} catch (Exception e) {

			FrameworkLogger.error("Unable to remove previous screenshots for test : " + testCaseName, e);
		}
	}

	// ==========================================================
	// DELETE TEST CASE SCREENSHOTS
	// ==========================================================

	/**
	 * Deletes all PNG screenshots belonging to a test case from both Pass and Fail
	 * folders.
	 *
	 * The Word document is NOT deleted.
	 */
	public static void deleteTestCaseScreenshots(String testCaseName) {

		if (testCaseName == null || testCaseName.isBlank()) {

			return;
		}

		try {

			String executionDirectory = ExtentManager.getExecutionDirectory();

			File screenshotsDirectory = new File(executionDirectory, "Screenshots");

			String safeTestCaseName = sanitizeFileName(testCaseName);

			deleteTestCaseScreenshotsFromFolder(new File(screenshotsDirectory, "Pass"), safeTestCaseName);

			deleteTestCaseScreenshotsFromFolder(new File(screenshotsDirectory, "Fail"), safeTestCaseName);

		} catch (Exception e) {

			FrameworkLogger.error("Unable to delete screenshots for test : " + testCaseName, e);
		}
	}

	// ==========================================================
	// DELETE SCREENSHOTS FROM ONE FOLDER
	// ==========================================================

	private static void deleteTestCaseScreenshotsFromFolder(File folder, String safeTestCaseName) {

		if (!folder.exists() || !folder.isDirectory()) {

			return;
		}

		File[] files = folder.listFiles();

		if (files == null) {

			return;
		}

		for (File file : files) {

			if (!file.isFile()) {

				continue;
			}

			if (file.getName().startsWith(safeTestCaseName + "_") && file.getName().toLowerCase().endsWith(".png")) {

				try {

					FileUtils.deleteQuietly(file);

				} catch (Exception ignored) {

					/*
					 * Screenshot cleanup must never interrupt test execution.
					 */
				}
			}
		}
	}

	// ==========================================================
	// RESET COUNTER
	// ==========================================================

	/**
	 * Resets screenshot sequence.
	 */
	public static void resetCounter() {

		SCREENSHOT_COUNTER.set(1);
	}

	// ==========================================================
	// GET CURRENT TEST CASE
	// ==========================================================

	public static String getCurrentTestCase() {

		return CURRENT_TEST_CASE.get();
	}

	// ==========================================================
	// REMOVE THREAD CONTEXT
	// ==========================================================

	/**
	 * Removes ThreadLocal data after test execution.
	 */
	public static void removeContext() {

		SCREENSHOT_COUNTER.remove();

		CURRENT_TEST_CASE.remove();
	}

	// ==========================================================
	// SANITIZE FILE NAME
	// ==========================================================

	/**
	 * Makes names safe for Windows file names.
	 */
	private static String sanitizeFileName(String name) {

		if (name == null || name.isBlank()) {

			return "Screenshot";
		}

		/*
		 * Windows invalid characters:
		 *
		 * \ / : * ? " < > |
		 */
		return name.trim().replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s+", "_");
	}
}