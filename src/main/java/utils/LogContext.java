package utils;

import org.apache.logging.log4j.ThreadContext;

public class LogContext {

    private static final String TEST_NAME = "testName";
    private static final String BROWSER = "browser";
    private static final String THREAD_ID = "threadId";

    private LogContext() {
        // Prevent instantiation
    }

    public static void setTestName(String testName) {
        ThreadContext.put(TEST_NAME, testName);
    }

    public static String getTestName() {
        return ThreadContext.get(TEST_NAME);
    }

    public static void setBrowser(String browser) {
        ThreadContext.put(BROWSER, browser);
    }

    public static String getBrowser() {
        return ThreadContext.get(BROWSER);
    }

    public static void setThreadId() {
        ThreadContext.put(THREAD_ID,
                String.valueOf(Thread.currentThread().threadId()));
    }

    public static String getThreadId() {
        return ThreadContext.get(THREAD_ID);
    }

    public static void clear() {
        ThreadContext.clearAll();
    }
}