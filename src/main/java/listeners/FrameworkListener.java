package listeners;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;

import enums.ScreenshotType;
import reports.ExtentManager;
import reports.ExtentTestManager;
import utils.AllureManager;
import utils.ConfigUtils;
import utils.DriverManager;
import utils.RetryAnalyzer;
import utils.ScreenshotUtils;

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
		 * IMPORTANT:
		 *
		 * New Extent architecture:
		 *
		 * Reports/ └── Date-Time/ └── ExtentReport.html
		 *
		 * There is NO testcase-specific ExtentReports object anymore.
		 */
		ExtentTest test = ExtentManager.getExtentReports()
				.createTest(result.getTestContext().getName() + " [" + browser + "]");

		test.assignAuthor(ConfigUtils.getRequiredProperty("author"));

		test.assignDevice(browser);

		test.assignCategory(ConfigUtils.getRequiredProperty("testing.type"));

		/*
		 * Store current ExtentTest in ThreadLocal.
		 */
		ExtentTestManager.setTest(test);

		ExtentTestManager.resetStepNumber();

		/*
		 * Do not use FrameworkLogger here.
		 *
		 * Otherwise this becomes a numbered execution row.
		 */
		test.log(Status.INFO, "Test Execution Started");

		/*
		 * Current execution attempt.
		 *
		 * Original execution = Attempt 1 First retry = Attempt 2 Second retry = Attempt
		 * 3
		 */
		int retryCount = RetryAnalyzer.getCurrentRetryCount();

		int currentAttempt = retryCount + 1;

		String attemptMessage = "Execution Attempt : " + currentAttempt;

		if (retryCount > 0) {

			attemptMessage += " | Retry Attempt : " + retryCount;
		}

		test.log(Status.INFO, attemptMessage);

		/*
		 * Allure.
		 */
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

		/*
		 * Final PASS.
		 */
		if (ExtentTestManager.getTest() != null) {

			ExtentTestManager.getTest().pass("Test Passed");
		}

		AllureManager.logPass("Test Passed");

		/*
		 * Retry information.
		 */
		addRetryInformation(retryCount, maxRetry, totalAttempts, "PASSED");

		/*
		 * Cleanup.
		 *
		 * IMPORTANT:
		 *
		 * Do NOT call:
		 *
		 * ExtentManager.removeCurrentContext();
		 *
		 * because the new ExtentManager does not have testcase-level ThreadLocal
		 * ExtentReports.
		 */
		ExtentTestManager.unload();

		AllureManager.stopTest();

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

		/*
		 * Check whether another retry is available.
		 */
		boolean retryPossible = retryCount < maxRetry;

		/*
		 * Failure message.
		 */
		String failureMessage = "Test Failed : " + result.getName();

		Throwable throwable = result.getThrowable();

		if (throwable != null && throwable.getMessage() != null) {

			failureMessage += " | Reason : " + throwable.getMessage();
		}

		/*
		 * Tell the report that another retry is scheduled.
		 */
		if (retryPossible) {

			failureMessage += " | Retry scheduled";
		}

		// ======================================================
		// RETRY INFORMATION
		// ======================================================

		addRetryInformation(retryCount, maxRetry, totalAttempts, retryPossible ? "RETRYING" : "FAILED");

		// ======================================================
		// FAILURE SCREENSHOT
		// ======================================================

		WebDriver driver = DriverManager.getDriver();

		String screenshotPath = null;

		if (driver != null && "true".equalsIgnoreCase(ConfigUtils.getProperty("capture.fail.screenshot"))) {

			try {

				screenshotPath = ScreenshotUtils.capture(driver, result.getName(), ScreenshotType.FAIL);

			} catch (Exception e) {

				AllureManager.logError("Unable to capture failure screenshot.", e);
			}
		}

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

			/*
			 * Exception details.
			 */
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

		/*
		 * Attach screenshot to Allure.
		 */
		if (screenshotPath != null && !screenshotPath.isBlank()) {

			AllureManager.attachScreenshot("Failed Test Screenshot", screenshotPath);
		}

		// ======================================================
		// CLEANUP
		// ======================================================

		/*
		 * If another retry is going to happen, do NOT reset RetryAnalyzer yet.
		 *
		 * TestNG will call onTestStart() again.
		 */
		if (!retryPossible) {

			ExtentTestManager.unload();

			AllureManager.stopTest();

			RetryAnalyzer.reset();
		}
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

		if (ExtentTestManager.getTest() != null) {

			ExtentTestManager.getTest().skip(message);
		}

		AllureManager.logWarn(message);

		/*
		 * Retry information.
		 */
		addRetryInformation(retryCount, maxRetry, totalAttempts, "SKIPPED");

		/*
		 * Cleanup.
		 */
		ExtentTestManager.unload();

		AllureManager.stopTest();

		RetryAnalyzer.reset();
	}

	// ==========================================================
	// RETRY INFORMATION
	// ==========================================================

	private void addRetryInformation(int retryCount, int maxRetry, int totalAttempts, String finalResult) {

		boolean retryAttempted = retryCount > 0;

		String retryStatus = retryAttempted ? "ATTEMPTED" : "NOT ATTEMPTED";

		/*
		 * Short single-line format.
		 *
		 * Example:
		 *
		 * Retry: ATTEMPTED | Retries: 2/2 | Attempts: 3 | Result: FAILED
		 */
		String retryMessage = "Retry: " + retryStatus + " | Retries: " + retryCount + "/" + maxRetry + " | Attempts: "
				+ totalAttempts + " | Result: " + finalResult;

		/*
		 * Extent.
		 *
		 * Do NOT use ExtentTestManager.info() because that would consume a step number.
		 */
		if (ExtentTestManager.getTest() != null) {

			ExtentTestManager.getTest().log(Status.INFO, retryMessage);
		}

		/*
		 * Allure.
		 */
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
}