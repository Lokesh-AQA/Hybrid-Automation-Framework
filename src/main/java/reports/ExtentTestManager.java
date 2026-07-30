package reports;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public final class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<>();
    private static final ThreadLocal<Integer> STEP_NUMBER =ThreadLocal.withInitial(() -> 1);

    private ExtentTestManager() {
    }

    public static void setTest(ExtentTest test) {
        EXTENT_TEST.set(test);
    }

    public static ExtentTest getTest() {
        return EXTENT_TEST.get();
    }
    
    public static void resetStepNumber() {
        STEP_NUMBER.set(1);
    }
    
    private static String getStepPrefix() {

        int step = STEP_NUMBER.get();
        STEP_NUMBER.set(step + 1);

        return String.format("<b>%02d ▶ </b>", step);
    }

    public static void info(String message) {

        if (getTest() != null) {

            getTest().log(Status.INFO, getStepPrefix() + message);

        }
    }

    public static void pass(String message) {

        if (getTest() != null) {

            getTest().pass(getStepPrefix() + message);

        }
    }

    public static void fail(String message) {

        if (getTest() != null) {

            getTest().fail(getStepPrefix() + message);

        }
    }

    public static void warning(String message) {

        if (getTest() != null) {

            getTest().warning(getStepPrefix() + message);

        }
    }

    public static void unload() {
        EXTENT_TEST.remove();
    }
}