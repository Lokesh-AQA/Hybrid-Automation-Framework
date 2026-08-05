package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private final int maxRetry = ConfigUtils.getIntProperty("retry.count");

    @Override
    public boolean retry(ITestResult result) {

        if (retryCount < maxRetry) {
        	retryCount++;

        	FrameworkLogger.warn(
        	    "Retrying Test : " + result.getName()
        	    + " | Attempt : " + retryCount
        	    + "/" + maxRetry);

        	return true;
        }

        return false;
    }
}