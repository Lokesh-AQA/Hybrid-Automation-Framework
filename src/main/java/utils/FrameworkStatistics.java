package utils;

public class FrameworkStatistics {

    private static int totalSteps = 0;
    private static int passedSteps = 0;
    private static int failedSteps = 0;
    private static int skippedSteps = 0;

    private static long startTime;
    private static long endTime;

    public static void reset() {
        totalSteps = 0;
        passedSteps = 0;
        failedSteps = 0;
        skippedSteps = 0;
        startTime = 0;
        endTime = 0;
    }

    public static void startExecution() {
        startTime = System.currentTimeMillis();
    }

    public static void endExecution() {
        endTime = System.currentTimeMillis();
    }

    public static void incrementTotal() {
        totalSteps++;
    }

    public static void incrementPassed() {
        passedSteps++;
    }

    public static void incrementFailed() {
        failedSteps++;
    }

    public static void incrementSkipped() {
        skippedSteps++;
    }

    public static void printSummary() {

        double executionTime = (endTime - startTime) / 1000.0;

        FrameworkLogger.info("====================================================");
        FrameworkLogger.info("Framework Execution Summary");
        FrameworkLogger.info("====================================================");
        FrameworkLogger.info("Total Steps      : " + totalSteps);
        FrameworkLogger.info("Passed           : " + passedSteps);
        FrameworkLogger.info("Failed           : " + failedSteps);
        FrameworkLogger.info("Skipped          : " + skippedSteps);
        FrameworkLogger.info("Execution Time   : " + executionTime + " Seconds");
        FrameworkLogger.info("====================================================");
    }
}
