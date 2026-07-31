package listeners;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ConfigUtils;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import reports.ExtentManager;
import reports.ExtentTestManager;
import utils.DriverManager;
import utils.FrameworkLogger;
import utils.ScreenshotUtils;

public class FrameworkListener implements ITestListener {

	@Override
	public void onTestStart(ITestResult result) {

		FrameworkLogger.step("Listener : Test Started : " + result.getName());

		String browser = result.getTestContext()
		        .getCurrentXmlTest()
		        .getParameter("browser");

		if (browser == null || browser.isBlank()) {
		    browser = ConfigUtils.getRequiredProperty("browser");
		}

		ExtentTest test = ExtentManager.getExtentReports()
				.createTest(result.getTestContext().getName() + " [" + browser + "]");
		
		test.assignAuthor(
		        ConfigUtils.getRequiredProperty("author"));

		test.assignDevice(browser);

		test.assignCategory(
		        ConfigUtils.getRequiredProperty("testing.type"));

		ExtentTestManager.setTest(test);
		ExtentTestManager.resetStepNumber();

		test.log(Status.INFO, "Test Execution Started");

	}

	@Override
	public void onTestSuccess(ITestResult result) {

		FrameworkLogger.pass("Listener : Test Passed : " + result.getName());

		if (ExtentTestManager.getTest() != null) {
			ExtentTestManager.getTest().pass("Test Passed");
			ExtentTestManager.unload();
		}
	}

	@Override
	public void onTestFailure(ITestResult result) {

		FrameworkLogger.fail("Listener : Test Failed : " + result.getName());

		Throwable throwable = result.getThrowable();

		if (throwable != null) {

			FrameworkLogger.error("Reason : " + throwable.getMessage());

			if (ExtentTestManager.getTest() != null) {
				
				ExtentTestManager.getTest().fail(throwable);
			}

		}

		WebDriver driver = DriverManager.getDriver();

		if (driver != null) {

			try {

				String screenshotPath = ScreenshotUtils.capture(driver, result.getName(), "Fail");

				if (screenshotPath != null) {

					if (ExtentTestManager.getTest() != null) {
						
					    ExtentTestManager.getTest().addScreenCaptureFromPath(screenshotPath);
					}

				}

			} catch (Exception e) {

				FrameworkLogger.warn("Unable to capture failure screenshot : " + e.getMessage());

			}

		} else {

			FrameworkLogger.warn("Screenshot skipped. Driver is null.");

		}

		ExtentTestManager.unload();

	}

	@Override
	public void onTestSkipped(ITestResult result) {

		FrameworkLogger.warn("Listener : Test Skipped : " + result.getName());

		if (ExtentTestManager.getTest() != null) {
			
			ExtentTestManager.getTest().skip("Test Skipped");
			ExtentTestManager.unload();
		}
	}

	@Override
	public void onFinish(ITestContext context) {

		FrameworkLogger.info("========================================");
		FrameworkLogger.info("Test Finished : " + context.getName());
		FrameworkLogger.info("Suite Finished : " + context.getSuite().getName());
		FrameworkLogger.info("========================================");
	}
}