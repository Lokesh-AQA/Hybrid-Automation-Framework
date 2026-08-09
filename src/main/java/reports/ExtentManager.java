package reports;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import utils.ConfigUtils;
import utils.GitUtils;

public final class ExtentManager {

    /*
     * ==========================================================
     * EXECUTION DIRECTORY
     * ==========================================================
     *
     * Example:
     *
     * Reports/
     * └── 2026-08-08_19-45-30/
     *     └── ExtentReport.html
     *
     */

    private static String executionDirectory;

    /*
     * ONE ExtentReports instance for the complete execution.
     *
     * ExtentReports is shared across parallel tests.
     * ExtentTest itself is managed using ThreadLocal
     * inside ExtentTestManager.
     */
    private static ExtentReports extentReports;

    private ExtentManager() {
        // Utility class
    }

    // ==========================================================
    // INITIALIZE EXECUTION DIRECTORY
    // ==========================================================

    public static synchronized void initializeExecutionDirectory() {

        if (executionDirectory != null) {
            return;
        }

        String timestamp =
                new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                        .format(new Date());

        executionDirectory =
                System.getProperty("user.dir")
                + File.separator
                + "Reports"
                + File.separator
                + timestamp;

        File directory = new File(executionDirectory);

        if (!directory.exists()) {

            if (!directory.mkdirs()) {

                throw new IllegalStateException(
                        "Unable to create execution directory : "
                        + executionDirectory);
            }
        }
    }

    // ==========================================================
    // INITIALIZE EXTENT REPORT
    // ==========================================================

    public static synchronized void initializeExtentReports() {

        /*
         * Prevent duplicate initialization.
         */
        if (extentReports != null) {
            return;
        }

        /*
         * Make sure execution directory exists.
         */
        initializeExecutionDirectory();

        /*
         * Extent report will be created directly inside
         * the execution folder.
         *
         * Example:
         *
         * Reports/
         * └── 2026-08-08_19-45-30/
         *     └── ExtentReport.html
         */
        String reportPath =
                executionDirectory
                + File.separator
                + "ExtentReport.html";

        ExtentSparkReporter sparkReporter =
                new ExtentSparkReporter(reportPath);

        // ======================================================
        // REPORT CONFIGURATION
        // ======================================================

        sparkReporter.config()
                .setDocumentTitle("Automation Test Report");

        sparkReporter.config()
                .setReportName("Hybrid Automation Framework");

        sparkReporter.config()
                .setTheme(Theme.DARK);

        // ======================================================
        // CREATE EXTENT REPORT
        // ======================================================

        extentReports = new ExtentReports();

        extentReports.attachReporter(sparkReporter);

        // ======================================================
        // SYSTEM INFORMATION
        // ======================================================

        extentReports.setSystemInfo(
                "Framework",
                "Hybrid Automation Framework");

        extentReports.setSystemInfo(
                "Automation",
                "Selenium + TestNG");

        extentReports.setSystemInfo(
                "Language",
                "Java");

        extentReports.setSystemInfo(
                "Author",
                ConfigUtils.getRequiredProperty("author"));

        extentReports.setSystemInfo(
                "Testing Type",
                ConfigUtils.getRequiredProperty("testing.type"));

        extentReports.setSystemInfo(
                "Environment",
                ConfigUtils.getRequiredProperty("environment"));

        extentReports.setSystemInfo(
                "Application",
                ConfigUtils.getRequiredProperty("application.name"));

        extentReports.setSystemInfo(
                "Build Version",
                ConfigUtils.getRequiredProperty("build.version"));

        extentReports.setSystemInfo(
                "Git Branch",
                GitUtils.getBranchName());

        extentReports.setSystemInfo(
                "Git Commit",
                GitUtils.getCommitId());

        extentReports.setSystemInfo(
                "Executed By",
                System.getProperty("user.name"));

        extentReports.setSystemInfo(
                "Operating System",
                System.getProperty("os.name"));

        extentReports.setSystemInfo(
                "Java Version",
                System.getProperty("java.version"));
    }

    // ==========================================================
    // GET EXTENT REPORTS
    // ==========================================================

    public static ExtentReports getExtentReports() {

        if (extentReports == null) {

            throw new IllegalStateException(
                    "Extent Report has not been initialized. "
                    + "Make sure ExtentSuiteListener.onStart() "
                    + "initializes the report first.");
        }

        return extentReports;
    }

    // ==========================================================
    // GET EXECUTION DIRECTORY
    // ==========================================================

    public static String getExecutionDirectory() {

        if (executionDirectory == null) {

            throw new IllegalStateException(
                    "Execution directory has not been initialized.");
        }

        return executionDirectory;
    }

    // ==========================================================
    // FLUSH REPORT
    // ==========================================================

    public static synchronized void flush() {

        if (extentReports != null) {

            extentReports.flush();
        }
    }

    // ==========================================================
    // CLEANUP
    // ==========================================================

    public static synchronized void reset() {

        /*
         * Used only if the framework itself needs to reset
         * the Extent manager between completely separate
         * JVM executions.
         */
        extentReports = null;
        executionDirectory = null;
    }
}