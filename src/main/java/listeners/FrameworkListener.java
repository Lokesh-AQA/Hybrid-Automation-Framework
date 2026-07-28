package listeners;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import utils.DriverManager;
import utils.FrameworkLogger;
import utils.ScreenshotUtils;

public class FrameworkListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        FrameworkLogger.info("========================================");
        FrameworkLogger.info("Suite Started : " + context.getSuite().getName());
        FrameworkLogger.info("Test Started  : " + context.getName());
        FrameworkLogger.info("========================================");
    }

    @Override
    public void onTestStart(ITestResult result) {
        FrameworkLogger.step("Listener : Test Started : " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        FrameworkLogger.pass("Listener : Test Passed : " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {

        FrameworkLogger.fail("Listener : Test Failed : " + result.getName());

        Throwable throwable = result.getThrowable();

        if (throwable != null) {
            FrameworkLogger.error("Reason : " + throwable.getMessage());
        }

        WebDriver driver = DriverManager.getDriver();

        if (driver != null) {
            try {
                ScreenshotUtils.capture(driver, result.getName(), "Fail");
            } catch (Exception e) {
                FrameworkLogger.warn("Unable to capture failure screenshot : " + e.getMessage());
            }
        } else {
            FrameworkLogger.warn("Screenshot skipped. Driver is null.");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        FrameworkLogger.warn("Listener : Test Skipped : " + result.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        FrameworkLogger.info("========================================");
        FrameworkLogger.info("Test Finished : " + context.getName());
        FrameworkLogger.info("Suite Finished : " + context.getSuite().getName());
        FrameworkLogger.info("========================================");
    }
}