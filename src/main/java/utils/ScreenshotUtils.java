package utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import enums.ScreenshotType;
import reports.ExtentManager;

public final class ScreenshotUtils {

    /*
     * Each execution thread maintains its own screenshot counter.
     *
     * Example:
     *
     * 001_Login_20260808_203015_123.png
     * 002_Click_20260808_203016_456.png
     */
    private static final ThreadLocal<Integer> SCREENSHOT_COUNTER =
            ThreadLocal.withInitial(() -> 1);

    private ScreenshotUtils() {
        // Utility class
    }

    // ==========================================================
    // CAPTURE SCREENSHOT
    // ==========================================================

    /**
     * Captures a screenshot and stores it inside the current
     * execution directory.
     *
     * Structure:
     *
     * Reports/
     * └── Date_Time/
     *     ├── ExtentReport.html
     *     ├── Screenshots/
     *     │   ├── Pass/
     *     │   └── Fail/
     *     ├── allure-results/
     *     └── allure-report/
     *
     * @param driver      WebDriver instance
     * @param keywordName Keyword name
     * @param status      PASS or FAIL
     *
     * @return Absolute screenshot path, or null if capture fails
     */
    public static String capture(
            WebDriver driver,
            String keywordName,
            ScreenshotType status) {

        // ======================================================
        // DRIVER VALIDATION
        // ======================================================

        if (driver == null) {

            FrameworkLogger.warn(
                    "Screenshot skipped. WebDriver is null.");

            return null;
        }

        try {

            // ==================================================
            // CAPTURE SCREENSHOT
            // ==================================================

            File source =
                    ((TakesScreenshot) driver)
                            .getScreenshotAs(OutputType.FILE);

            // ==================================================
            // TIMESTAMP
            // ==================================================

            String timeStamp =
                    new SimpleDateFormat(
                            "yyyyMMdd_HHmmss_SSS")
                            .format(new Date());

            // ==================================================
            // CURRENT EXECUTION DIRECTORY
            // ==================================================

            String executionDirectory =
                    ExtentManager.getExecutionDirectory();

            /*
             * Example:
             *
             * Reports/
             * └── 2026-08-08_20-30-15/
             */
            File executionFolder =
                    new File(executionDirectory);

            if (!executionFolder.exists()) {

                if (!executionFolder.mkdirs()
                        && !executionFolder.exists()) {

                    throw new IllegalStateException(
                            "Unable to create execution directory : "
                            + executionDirectory);
                }
            }

            // ==================================================
            // SCREENSHOT TYPE DIRECTORY
            // ==================================================

            String statusFolder;

            if (status == ScreenshotType.PASS) {

                statusFolder = "Pass";

            } else {

                statusFolder = "Fail";
            }

            /*
             * Final structure:
             *
             * Screenshots/
             * ├── Pass/
             * └── Fail/
             */
            File screenshotFolder =
                    new File(
                            executionFolder,
                            "Screenshots"
                                    + File.separator
                                    + statusFolder);

            if (!screenshotFolder.exists()) {

                if (!screenshotFolder.mkdirs()
                        && !screenshotFolder.exists()) {

                    throw new IllegalStateException(
                            "Unable to create screenshot directory : "
                                    + screenshotFolder.getAbsolutePath());
                }
            }

            // ==================================================
            // SCREENSHOT SEQUENCE
            // ==================================================

            int count = SCREENSHOT_COUNTER.get();

            String sequence =
                    String.format("%03d", count);

            SCREENSHOT_COUNTER.set(count + 1);

            // ==================================================
            // SAFE KEYWORD NAME
            // ==================================================

            String safeKeywordName =
                    sanitizeFileName(keywordName);

            // ==================================================
            // FILE NAME
            // ==================================================

            String fileName =
                    sequence
                    + "_"
                    + safeKeywordName
                    + "_"
                    + timeStamp
                    + ".png";

            File destinationFile =
                    new File(
                            screenshotFolder,
                            fileName);

            // ==================================================
            // COPY SCREENSHOT
            // ==================================================

            FileUtils.copyFile(
                    source,
                    destinationFile);

            // ==================================================
            // RETURN ABSOLUTE PATH
            // ==================================================

            return destinationFile.getAbsolutePath();

        } catch (Exception e) {

            /*
             * Screenshot failure must NEVER hide the
             * original automation failure.
             */
            System.err.println(
                    "Unable to capture screenshot : "
                            + e.getMessage());

            return null;
        }
    }

    // ==========================================================
    // RESET COUNTER
    // ==========================================================

    /**
     * Resets screenshot numbering before every test execution.
     */
    public static void resetCounter() {

        SCREENSHOT_COUNTER.set(1);
    }

    // ==========================================================
    // REMOVE THREAD CONTEXT
    // ==========================================================

    /**
     * Removes ThreadLocal counter after execution.
     */
    public static void removeContext() {

        SCREENSHOT_COUNTER.remove();
    }

    // ==========================================================
    // SANITIZE FILE NAME
    // ==========================================================

    /**
     * Makes keyword names safe for Windows file names.
     */
    private static String sanitizeFileName(String name) {

        if (name == null || name.isBlank()) {

            return "Screenshot";
        }

        /*
         * Windows invalid characters:
         *
         * \ / : * ? " < > |
         */
        return name.trim()
                .replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("\\s+", "_");
    }
}