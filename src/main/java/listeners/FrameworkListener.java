package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import utils.DriverManager;
import utils.FrameworkLogger;
import utils.ScreenshotUtils;

public class FrameworkListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
    	FrameworkLogger.step("Listener : Test Started : " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    	FrameworkLogger.pass("Listener : Test Passed : " + result.getName());
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        FrameworkLogger.warn("Listener : Test Skipped : " + result.getName());
    }
    
    @Override
    public void onStart(ITestContext context) {
        FrameworkLogger.info("========== Test Suite Started : "
                + context.getName() + " ==========");
    }
    
    @Override
    public void onFinish(ITestContext context) {
        FrameworkLogger.info("========== Test Suite Finished : "
                + context.getName() + " ==========");
    }

    @Override
    public void onTestFailure(ITestResult result) {

        FrameworkLogger.error("Listener : Test Failed : " + result.getName());

        Throwable throwable = result.getThrowable();

        if (throwable != null) {
            FrameworkLogger.error("Reason : " + throwable.getMessage());
        }

        ScreenshotUtils.capture(
                DriverManager.getDriver(),
                result.getName(),
                "Fail");
    }
}