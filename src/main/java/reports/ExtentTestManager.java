package reports;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;

public final class ExtentTestManager {

	/*
	 * Each execution thread gets its own ExtentTest.
	 *
	 * Required for parallel execution.
	 */
	private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<>();

	/*
	 * Each execution thread maintains its own step number.
	 */
	private static final ThreadLocal<Integer> STEP_NUMBER = ThreadLocal.withInitial(() -> 1);

	private ExtentTestManager() {
	}

	// ==========================================================
	// TEST MANAGEMENT
	// ==========================================================

	public static void setTest(ExtentTest test) {

		EXTENT_TEST.set(test);
	}

	public static ExtentTest getTest() {

		return EXTENT_TEST.get();
	}

	// ==========================================================
	// STEP NUMBER
	// ==========================================================

	public static void resetStepNumber() {

		STEP_NUMBER.set(1);
	}

	private static String getStepPrefix() {

		int step = STEP_NUMBER.get();

		STEP_NUMBER.set(step + 1);

		return String.format("<b>%02d ▶ </b>", step);
	}

	// ==========================================================
	// INFO
	// ==========================================================

	public static void info(String message) {

		if (getTest() == null) {
			return;
		}

		getTest().log(Status.INFO, getStepPrefix() + message);
	}

	// ==========================================================
	// PASS - WITHOUT SCREENSHOT
	// ==========================================================

	public static void pass(String message) {

		if (getTest() == null) {
			return;
		}

		getTest().pass(getStepPrefix() + message);
	}

	// ==========================================================
	// PASS - WITH OPTIONAL SCREENSHOT
	// ==========================================================

	public static void pass(String message, String screenshotPath) {

		if (getTest() == null) {
			return;
		}

		String finalMessage = getStepPrefix() + message;

		/*
		 * No screenshot.
		 */
		if (screenshotPath == null || screenshotPath.isBlank()) {

			getTest().pass(finalMessage);

			return;
		}

		/*
		 * Create a permanent copy specifically for Extent Report.
		 */
		String extentScreenshotPath = createExtentScreenshotCopy(screenshotPath);

		/*
		 * If screenshot copy failed, still keep the test PASS.
		 */
		if (extentScreenshotPath == null) {

			getTest().pass(finalMessage);

			return;
		}

		/*
		 * Attach the permanent Extent screenshot.
		 */
		getTest().pass(finalMessage, MediaEntityBuilder.createScreenCaptureFromPath(extentScreenshotPath).build());
	}

	// ==========================================================
	// FAIL - WITHOUT SCREENSHOT
	// ==========================================================

	public static void fail(String message) {

		if (getTest() == null) {
			return;
		}

		getTest().fail(getStepPrefix() + message);
	}

	// ==========================================================
	// FAIL - WITH OPTIONAL SCREENSHOT
	// ==========================================================

	public static void fail(String message, String screenshotPath) {

		if (getTest() == null) {
			return;
		}

		String finalMessage = getStepPrefix() + message;

		/*
		 * No screenshot.
		 */
		if (screenshotPath == null || screenshotPath.isBlank()) {

			getTest().fail(finalMessage);

			return;
		}

		/*
		 * Create a permanent copy specifically for Extent Report.
		 */
		String extentScreenshotPath = createExtentScreenshotCopy(screenshotPath);

		/*
		 * If screenshot copy failed, still keep the test FAIL.
		 */
		if (extentScreenshotPath == null) {

			getTest().fail(finalMessage);

			return;
		}

		/*
		 * Attach the permanent Extent screenshot.
		 */
		getTest().fail(finalMessage, MediaEntityBuilder.createScreenCaptureFromPath(extentScreenshotPath).build());
	}

	// ==========================================================
	// CREATE EXTENT SCREENSHOT COPY
	// ==========================================================

	/**
	 * Creates a permanent copy of the screenshot specifically for Extent Report.
	 *
	 * Original:
	 *
	 * Screenshots/Pass/ OR Screenshots/Fail/
	 *
	 * Extent copy:
	 *
	 * Extent/Screenshots/
	 *
	 * The original screenshot can therefore be moved or deleted without breaking
	 * Extent.
	 */
	private static String createExtentScreenshotCopy(String screenshotPath) {

		try {

			// ==================================================
			// SOURCE FILE
			// ==================================================

			File sourceFile = new File(screenshotPath);

			if (!sourceFile.exists() || !sourceFile.isFile()) {

				System.err.println("Extent screenshot source file does not exist : " + screenshotPath);

				return null;
			}

			// ==================================================
			// EXTENT SCREENSHOT DIRECTORY
			// ==================================================

			String extentScreenshotDirectory = ExtentManager.getExtentScreenshotDirectory();

			File extentFolder = new File(extentScreenshotDirectory);

			if (!extentFolder.exists()) {

				if (!extentFolder.mkdirs() && !extentFolder.exists()) {

					throw new IllegalStateException(
							"Unable to create Extent screenshot directory : " + extentFolder.getAbsolutePath());
				}
			}

			// ==================================================
			// DESTINATION FILE
			// ==================================================

			File destinationFile = new File(extentFolder, sourceFile.getName());

			// ==================================================
			// COPY SCREENSHOT
			// ==================================================

			FileUtils.copyFile(sourceFile, destinationFile);

			// ==================================================
			// RETURN EXTENT SCREENSHOT PATH
			// ==================================================

			return destinationFile.getAbsolutePath();

		} catch (Exception e) {

			System.err.println("Unable to create Extent screenshot copy : " + e.getMessage());

			return null;
		}
	}

	// ==========================================================
	// WARNING
	// ==========================================================

	public static void warning(String message) {

		if (getTest() == null) {
			return;
		}

		getTest().warning(getStepPrefix() + message);
	}

	// ==========================================================
	// CLEANUP
	// ==========================================================

	public static void unload() {

		EXTENT_TEST.remove();

		STEP_NUMBER.remove();
	}
}