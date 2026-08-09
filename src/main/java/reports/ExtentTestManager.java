package reports;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;

public final class ExtentTestManager {

	/*
	 * Each execution thread gets its own ExtentTest. This is required for parallel
	 * execution.
	 */
	private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<>();

	/*
	 * Each thread maintains its own step number.
	 */
	private static final ThreadLocal<Integer> STEP_NUMBER = ThreadLocal.withInitial(() -> 1);

	private ExtentTestManager() {
	}

	// ==========================================================
	// Test Management
	// ==========================================================

	public static void setTest(ExtentTest test) {

		EXTENT_TEST.set(test);
	}

	public static ExtentTest getTest() {

		return EXTENT_TEST.get();
	}

	// ==========================================================
	// Step Number
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
		 * Screenshot disabled or unavailable.
		 *
		 * Create normal PASS row.
		 */
		if (screenshotPath == null || screenshotPath.isBlank()) {

			getTest().pass(finalMessage);

			return;
		}

		/*
		 * Screenshot exists.
		 *
		 * Attach it directly to THIS PASS log entry.
		 */
		getTest().pass(finalMessage, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
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
		 * Screenshot disabled or unavailable.
		 */
		if (screenshotPath == null || screenshotPath.isBlank()) {

			getTest().fail(finalMessage);

			return;
		}

		/*
		 * Screenshot exists.
		 *
		 * Attach it directly to THIS FAIL log entry.
		 */
		getTest().fail(finalMessage, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
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
	// Cleanup
	// ==========================================================

	public static void unload() {

		EXTENT_TEST.remove();
		STEP_NUMBER.remove();
	}
}