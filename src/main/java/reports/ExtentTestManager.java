package reports;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public final class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<>();

    private ExtentTestManager() {
    }

    public static void setTest(ExtentTest test) {
        EXTENT_TEST.set(test);
    }

    public static ExtentTest getTest() {
        return EXTENT_TEST.get();
    }

    public static void info(String message) {
        if (getTest() != null) {
            getTest().log(Status.INFO, message);
        }
    }

    public static void pass(String message) {
        if (getTest() != null) {
            getTest().pass(message);
        }
    }

    public static void fail(String message) {
        if (getTest() != null) {
            getTest().fail(message);
        }
    }

    public static void warning(String message) {
        if (getTest() != null) {
            getTest().warning(message);
        }
    }

    public static void unload() {
        EXTENT_TEST.remove();
    }
}