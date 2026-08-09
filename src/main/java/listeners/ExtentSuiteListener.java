package listeners;

import org.testng.ISuite;
import org.testng.ISuiteListener;

import reports.ExtentManager;
import utils.FrameworkLogger;

public class ExtentSuiteListener implements ISuiteListener {

	// ==========================================================
	// SUITE START
	// ==========================================================

	@Override
	public void onStart(ISuite suite) {

		FrameworkLogger.info("========================================");

		FrameworkLogger.info("Extent Report Initialization Started");

		FrameworkLogger.info("Suite : " + suite.getName());

		FrameworkLogger.info("========================================");

		/*
		 * Create ONE execution directory for the complete Extent execution.
		 *
		 * Example:
		 *
		 * Reports/ └── 2026-08-09_10-00-00/ ├── ExtentReport.html └── Screenshots/
		 *
		 * IMPORTANT:
		 *
		 * This listener is responsible ONLY for Extent Reports.
		 *
		 * It does NOT create, move, clean, or manage Allure results.
		 */
		ExtentManager.initializeExecutionDirectory();

		/*
		 * Initialize ONE ExtentReports instance.
		 */
		ExtentManager.initializeExtentReports();
	}

	// ==========================================================
	// SUITE FINISH
	// ==========================================================

	@Override
	public void onFinish(ISuite suite) {

		FrameworkLogger.info("========================================");

		FrameworkLogger.info("Flushing Extent Report");

		FrameworkLogger.info("Suite : " + suite.getName());

		FrameworkLogger.info("========================================");

		/*
		 * Flush the SINGLE ExtentReports instance.
		 *
		 * Allure is NOT handled here.
		 */
		ExtentManager.flush();
	}
}