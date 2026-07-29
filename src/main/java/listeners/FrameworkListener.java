package listeners;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import reports.ExtentManager;
import reports.ExtentTestManager;
import utils.DriverManager;
import utils.FrameworkLogger;
import utils.ScreenshotUtils;

public class FrameworkListener implements ITestListener {

    private ExtentReports extent;

    @Override
    public void onStart(ITestContext context) {

        extent = ExtentManager.getExtentReports();

        FrameworkLogger.info("========================================");
        FrameworkLogger.info("Suite Started : " + context.getSuite().getName());
        FrameworkLogger.info("Test Started  : " + context.getName());
        FrameworkLogger.info("========================================");
    }

    @Override
    public void onTestStart(ITestResult result) {

        FrameworkLogger.step("Listener : Test Started : " + result.getName());

        ExtentTest test = extent.createTest(result.getName());

        ExtentTestManager.setTest(test);

        test.log(Status.INFO, "Test Execution Started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {

        FrameworkLogger.pass("Listener : Test Passed : " + result.getName());

        ExtentTestManager.getTest().pass("Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {

        FrameworkLogger.fail("Listener : Test Failed : " + result.getName());

        Throwable throwable = result.getThrowable();

        if (throwable != null) {

            FrameworkLogger.error("Reason : " + throwable.getMessage());

            ExtentTestManager.getTest().fail(throwable);

        }

        WebDriver driver = DriverManager.getDriver();

        if (driver != null) {

            try {

                String screenshotPath =
                        ScreenshotUtils.capture(driver, result.getName(), "Fail");

                if (screenshotPath != null) {

                    ExtentTestManager.getTest()
                            .addScreenCaptureFromPath(screenshotPath);

                }

            } catch (Exception e) {

                FrameworkLogger.warn(
                        "Unable to capture failure screenshot : "
                                + e.getMessage());

            }

        } else {

            FrameworkLogger.warn("Screenshot skipped. Driver is null.");

        }

    }

    @Override
    public void onTestSkipped(ITestResult result) {

        FrameworkLogger.warn("Listener : Test Skipped : " + result.getName());

        ExtentTestManager.getTest().skip("Test Skipped");
    }

    @Override
    public void onFinish(ITestContext context) {

        if (extent != null) {

            extent.flush();

        }

        ExtentTestManager.unload();

        FrameworkLogger.info("========================================");
        FrameworkLogger.info("Test Finished : " + context.getName());
        FrameworkLogger.info("Suite Finished : " + context.getSuite().getName());
        FrameworkLogger.info("========================================");
    }
}