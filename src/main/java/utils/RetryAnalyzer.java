package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    private final int maxRetry =
            ConfigUtils.getIntProperty("retry.count");

    private static final ThreadLocal<Integer> CURRENT_RETRY =
            ThreadLocal.withInitial(() -> 0);

    // ==========================================================
    // RETRY
    // ==========================================================

    @Override
    public boolean retry(ITestResult result) {

        if (retryCount < maxRetry) {

            retryCount++;

            CURRENT_RETRY.set(retryCount);

            FrameworkLogger.warn(
                    "Retrying Test : "
                            + result.getName()
                            + " | Retry Attempt : "
                            + retryCount
                            + "/" + maxRetry);

            return true;
        }

        CURRENT_RETRY.set(retryCount);

        return false;
    }

    // ==========================================================
    // CURRENT RETRY COUNT
    // ==========================================================

    public static int getCurrentRetryCount() {

        return CURRENT_RETRY.get();
    }

    // ==========================================================
    // MAX RETRY
    // ==========================================================

    public int getMaxRetry() {

        return maxRetry;
    }

    // ==========================================================
    // RESET
    // ==========================================================

    public static void reset() {

        CURRENT_RETRY.remove();
    }
}