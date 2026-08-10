package listeners;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;

import reports.ExtentManager;
import reports.ExtentTestManager;
import utils.AllureManager;
import utils.ConfigUtils;
import utils.FrameworkLogger;
import utils.RetryAnalyzer;
import utils.ScreenshotUtils;
import utils.ScreenshotWordUtils;

public class FrameworkListener implements ITestListener {

	private static final Set<String> EXECUTED_BROWSERS = new ConcurrentSkipListSet<>();

	// ==========================================================
	// TEST START
	// ==========================================================

	@Override
	public void onTestStart(ITestResult result) {

		AllureManager.startTest();

		String browser = getBrowser(result);

		EXECUTED_BROWSERS.add(browser);

		/*
		 * Start screenshot handling for this test attempt.
		 *
		 * This:
		 *
		 * 1. Stores current test case name 2. Resets screenshot counter 3. Removes
		 * screenshots from previous retry
		 */
		ScreenshotUtils.startTest(result.getName());

		// ======================================================
		// EXTENT TEST
		// ======================================================

		ExtentTest test = ExtentManager.getExtentReports()
				.createTest(result.getTestContext().getName() + " [" + browser + "]");

		test.assignAuthor(ConfigUtils.getRequiredProperty("author"));

		test.assignDevice(browser);

		test.assignCategory(ConfigUtils.getRequiredProperty("testing.type"));

		ExtentTestManager.setTest(test);

		ExtentTestManager.resetStepNumber();

		test.log(Status.INFO, "Test Execution Started");

		// ======================================================
		// RETRY INFORMATION
		// ======================================================

		int retryCount = RetryAnalyzer.getCurrentRetryCount();

		int currentAttempt = retryCount + 1;

		String attemptMessage = "Execution Attempt : " + currentAttempt;

		if (retryCount > 0) {

			attemptMessage += " | Retry Attempt : " + retryCount;
		}

		test.log(Status.INFO, attemptMessage);

		// ======================================================
		// ALLURE
		// ======================================================

		AllureManager.logInfo(
				"Test Started : " + result.getName() + " | Browser : " + browser + " | Attempt : " + currentAttempt);
	}

	// ==========================================================
	// TEST SUCCESS
	// ==========================================================

	@Override
	public void onTestSuccess(ITestResult result) {

		int retryCount = RetryAnalyzer.getCurrentRetryCount();

		int maxRetry = ConfigUtils.getIntProperty("retry.count");

		int totalAttempts = retryCount + 1;

		// ======================================================
		// EXTENT
		// ======================================================

		if (ExtentTestManager.getTest() != null) {

			ExtentTestManager.getTest().pass("Test Passed");
		}

		// ======================================================
		// ALLURE
		// ======================================================

		AllureManager.logPass("Test Passed");

		// ======================================================
		// RETRY INFORMATION
		// ======================================================

		addRetryInformation(retryCount, maxRetry, totalAttempts, "PASSED");

		// ======================================================
		// PASS SCREENSHOT CONFIGURATION
		// ======================================================

		boolean capturePassScreenshot = isScreenshotEnabled("capture.pass.screenshot");

		if (capturePassScreenshot) {

			/*
			 * Final result = PASS
			 *
			 * PASS screenshot collection is enabled.
			 *
			 * Screenshots are already inside:
			 *
			 * Screenshots/Pass/
			 *
			 * Create:
			 *
			 * TestCaseName.docx
			 */
			createFinalScreenshotWord(result, "Pass");

		} else {

			/*
			 * Final result = PASS
			 *
			 * PASS screenshot collection is disabled.
			 *
			 * Remove any screenshots.
			 *
			 * No Pass Word document.
			 */
			ScreenshotUtils.deleteTestCaseScreenshots(result.getName());
		}

		// ======================================================
		// CLEANUP
		// ======================================================

		ExtentTestManager.unload();

		AllureManager.stopTest();

		ScreenshotUtils.removeContext();

		RetryAnalyzer.reset();
	}

	// ==========================================================
	// TEST FAILURE
	// ==========================================================

	@Override
	public void onTestFailure(ITestResult result) {

		int retryCount = RetryAnalyzer.getCurrentRetryCount();

		int maxRetry = ConfigUtils.getIntProperty("retry.count");

		int totalAttempts = retryCount + 1;

		boolean retryPossible = retryCount < maxRetry;

		// ======================================================
		// FAILURE MESSAGE
		// ======================================================

		String failureMessage = "Test Failed : " + result.getName();

		Throwable throwable = result.getThrowable();

		if (throwable != null && throwable.getMessage() != null) {

			failureMessage += " | Reason : " + throwable.getMessage();
		}

		if (retryPossible) {

			failureMessage += " | Retry scheduled";
		}

		// ======================================================
		// RETRY INFORMATION
		// ======================================================

		addRetryInformation(retryCount, maxRetry, totalAttempts, retryPossible ? "RETRYING" : "FAILED");

		// ======================================================
		// FIND LATEST FAILURE SCREENSHOT
		// ======================================================

		/*
		 * KeywordExecutor already captures the failed keyword screenshot when:
		 *
		 * capture.fail.screenshot=true
		 *
		 * We don't capture another screenshot here.
		 */
		String screenshotPath = findLatestScreenshot(result.getName());

		// ======================================================
		// EXTENT FAILURE
		// ======================================================

		if (ExtentTestManager.getTest() != null) {

			if (screenshotPath != null && !screenshotPath.isBlank()) {

				ExtentTestManager.getTest().fail(failureMessage,
						MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());

			} else {

				ExtentTestManager.getTest().fail(failureMessage);
			}

			if (throwable != null) {

				ExtentTestManager.getTest().fail(throwable);
			}
		}

		// ======================================================
		// ALLURE FAILURE
		// ======================================================

		AllureManager.logFail(failureMessage);

		if (throwable != null) {

			AllureManager.logError("Failure Exception", throwable);
		}

		// ======================================================
		// ALLURE SCREENSHOT
		// ======================================================

		if (screenshotPath != null && !screenshotPath.isBlank()) {

			AllureManager.attachScreenshot("Failed Test Screenshot", screenshotPath);
		}

		// ======================================================
		// RETRY
		// ======================================================

		if (retryPossible) {

			/*
			 * IMPORTANT:
			 *
			 * Do NOT create the final Fail Word yet.
			 *
			 * TestNG will execute the next attempt.
			 *
			 * ScreenshotUtils.startTest() will remove screenshots from this failed attempt.
			 */

			ExtentTestManager.unload();

			AllureManager.stopTest();

			ScreenshotUtils.removeContext();

			return;
		}

		// ======================================================
		// FINAL FAILURE
		// ======================================================

		boolean captureFailScreenshot = isScreenshotEnabled("capture.fail.screenshot");

		if (captureFailScreenshot) {

			/*
			 * Final result = FAIL
			 *
			 * Failure screenshots are enabled.
			 *
			 * Case:
			 *
			 * capture.pass.screenshot=true capture.fail.screenshot=true
			 *
			 * Successful screenshots are currently in:
			 *
			 * Screenshots/Pass/
			 *
			 * Failed step screenshot is in:
			 *
			 * Screenshots/Fail/
			 *
			 * Therefore move the complete successful flow from Pass -> Fail.
			 */
			boolean capturePassScreenshot = isScreenshotEnabled("capture.pass.screenshot");

			if (capturePassScreenshot) {

				ScreenshotUtils.moveTestCaseScreenshots(result.getName(), "Pass", "Fail");
			}

			/*
			 * Now Fail contains:
			 *
			 * 001_navigate 002_input 003_click 004_failedStep
			 *
			 * Create the final Word document.
			 */
			createFinalScreenshotWord(result, "Fail");

		} else {

			/*
			 * Final result = FAIL
			 *
			 * Failure screenshot collection is disabled.
			 *
			 * Remove all screenshots.
			 *
			 * No Fail folder should remain with screenshots. No Word document.
			 */
			ScreenshotUtils.deleteTestCaseScreenshots(result.getName());
		}

		// ======================================================
		// CLEANUP
		// ======================================================

		ExtentTestManager.unload();

		AllureManager.stopTest();

		ScreenshotUtils.removeContext();

		RetryAnalyzer.reset();
	}

	// ==========================================================
	// TEST SKIPPED
	// ==========================================================

	@Override
	public void onTestSkipped(ITestResult result) {

		int retryCount = RetryAnalyzer.getCurrentRetryCount();

		int maxRetry = ConfigUtils.getIntProperty("retry.count");

		int totalAttempts = retryCount + 1;

		String message = "Test Skipped : " + result.getName();

		// ======================================================
		// EXTENT
		// ======================================================

		if (ExtentTestManager.getTest() != null) {

			ExtentTestManager.getTest().skip(message);
		}

		// ======================================================
		// ALLURE
		// ======================================================

		AllureManager.logWarn(message);

		// ======================================================
		// RETRY INFORMATION
		// ======================================================

		addRetryInformation(retryCount, maxRetry, totalAttempts, "SKIPPED");

		/*
		 * No screenshot Word document for skipped tests.
		 */
		ScreenshotUtils.deleteTestCaseScreenshots(result.getName());

		// ======================================================
		// CLEANUP
		// ======================================================

		ExtentTestManager.unload();

		AllureManager.stopTest();

		ScreenshotUtils.removeContext();

		RetryAnalyzer.reset();
	}

	// ==========================================================
	// CREATE FINAL SCREENSHOT WORD
	// ==========================================================

	private void createFinalScreenshotWord(ITestResult result, String resultFolder) {

		try {

			String testCaseName = result.getName();

			if (testCaseName == null || testCaseName.isBlank()) {

				testCaseName = "TestCase";
			}

			/*
			 * ScreenshotWordUtils reads:
			 *
			 * Screenshots/Pass/
			 *
			 * OR
			 *
			 * Screenshots/Fail/
			 */
			ScreenshotWordUtils.createWord(testCaseName, resultFolder);

			/*
			 * Word document has now been created.
			 *
			 * Remove individual PNG files.
			 *
			 * Final folder will contain only:
			 *
			 * TestCaseName.docx
			 */
			deleteScreenshotImages(testCaseName, resultFolder);

		} catch (Exception e) {

			FrameworkLogger.error("Unable to generate final screenshot Word " + "for test : " + result.getName(), e);
		}
	}

	// ==========================================================
	// FIND LATEST SCREENSHOT
	// ==========================================================

	/**
	 * Finds the latest screenshot belonging to the test case across BOTH Pass and
	 * Fail folders.
	 *
	 * This is important because:
	 *
	 * PASS screenshots can be in Pass/ FAILED keyword screenshot can be in Fail/
	 *
	 * We must select the newest screenshot regardless of which folder contains it.
	 */
	private String findLatestScreenshot(String testCaseName) {

		try {

			String executionDirectory = ExtentManager.getExecutionDirectory();

			File screenshotsDirectory = new File(executionDirectory, "Screenshots");

			String safeTestCaseName = sanitizeFileName(testCaseName);

			File passFolder = new File(screenshotsDirectory, "Pass");

			File failFolder = new File(screenshotsDirectory, "Fail");

			File latestPass = findLatestInFolder(passFolder, safeTestCaseName);

			File latestFail = findLatestInFolder(failFolder, safeTestCaseName);

			// ==================================================
			// BOTH EXIST
			// ==================================================

			if (latestPass != null && latestFail != null) {

				if (latestFail.lastModified() > latestPass.lastModified()) {

					return latestFail.getAbsolutePath();
				}

				return latestPass.getAbsolutePath();
			}

			// ==================================================
			// ONLY PASS EXISTS
			// ==================================================

			if (latestPass != null) {

				return latestPass.getAbsolutePath();
			}

			// ==================================================
			// ONLY FAIL EXISTS
			// ==================================================

			if (latestFail != null) {

				return latestFail.getAbsolutePath();
			}

			return null;

		} catch (Exception e) {

			FrameworkLogger.warn("Unable to find latest screenshot : " + e.getMessage());

			return null;
		}
	}

	// ==========================================================
	// FIND LATEST SCREENSHOT IN FOLDER
	// ==========================================================

	private File findLatestInFolder(File folder, String testCaseName) {

		if (!folder.exists() || !folder.isDirectory()) {

			return null;
		}

		File[] files = folder.listFiles(file -> file.isFile() && file.getName().startsWith(testCaseName + "_")
				&& file.getName().toLowerCase().endsWith(".png"));

		if (files == null || files.length == 0) {

			return null;
		}

		File latest = files[0];

		for (File file : files) {

			if (file.lastModified() > latest.lastModified()) {

				latest = file;
			}
		}

		return latest;
	}

	// ==========================================================
	// DELETE SCREENSHOT IMAGES
	// ==========================================================

	private void deleteScreenshotImages(String testCaseName, String resultFolder) {

		try {

			String executionDirectory = ExtentManager.getExecutionDirectory();

			File folder = new File(executionDirectory, "Screenshots" + File.separator + resultFolder);

			if (!folder.exists()) {

				return;
			}

			String safeTestCaseName = sanitizeFileName(testCaseName);

			File[] files = folder.listFiles(file -> file.isFile() && file.getName().startsWith(safeTestCaseName + "_")
					&& file.getName().toLowerCase().endsWith(".png"));

			if (files == null) {

				return;
			}

			for (File file : files) {

				if (!file.delete()) {

					FrameworkLogger.warn("Unable to delete screenshot : " + file.getAbsolutePath());
				}
			}

		} catch (Exception e) {

			FrameworkLogger.warn("Unable to delete screenshot images : " + e.getMessage());
		}
	}

	// ==========================================================
	// RETRY INFORMATION
	// ==========================================================

	private void addRetryInformation(int retryCount, int maxRetry, int totalAttempts, String finalResult) {

		boolean retryAttempted = retryCount > 0;

		String retryStatus = retryAttempted ? "ATTEMPTED" : "NOT ATTEMPTED";

		String retryMessage = "Retry: " + retryStatus + " | Retries: " + retryCount + "/" + maxRetry + " | Attempts: "
				+ totalAttempts + " | Result: " + finalResult;

		if (ExtentTestManager.getTest() != null) {

			ExtentTestManager.getTest().log(Status.INFO, retryMessage);
		}

		AllureManager.logInfo(retryMessage);
	}

	// ==========================================================
	// GET BROWSER
	// ==========================================================

	private String getBrowser(ITestResult result) {

		String browser = result.getTestContext().getCurrentXmlTest().getParameter("browser");

		if (browser == null || browser.isBlank()) {

			browser = ConfigUtils.getRequiredProperty("browser");
		}

		return browser;
	}

	// ==========================================================
	// TEST FINISH
	// ==========================================================

	@Override
	public void onFinish(ITestContext context) {

		System.out.println("Test Finished : " + context.getName() + " | Suite : " + context.getSuite().getName());
	}

	// ==========================================================
	// EXECUTED BROWSERS
	// ==========================================================

	public static String getExecutedBrowsers() {

		return String.join(", ", EXECUTED_BROWSERS);
	}

	// ==========================================================
	// SCREENSHOT CONFIGURATION
	// ==========================================================

	private boolean isScreenshotEnabled(String propertyName) {

		return "true".equalsIgnoreCase(ConfigUtils.getProperty(propertyName));
	}

	// ==========================================================
	// SANITIZE FILE NAME
	// ==========================================================

	private String sanitizeFileName(String name) {

		if (name == null || name.isBlank()) {

			return "TestCase";
		}

		return name.trim().replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s+", "_");
	}
}