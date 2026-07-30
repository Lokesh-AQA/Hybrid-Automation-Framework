package listeners;

import org.testng.ISuite;
import org.testng.ISuiteListener;

import reports.ExtentManager;
import utils.FrameworkLogger;

public class ExtentSuiteListener implements ISuiteListener {

	@Override
	public void onStart(ISuite suite) {

		FrameworkLogger.info("========================================");
		FrameworkLogger.info("Extent Report Initialization Started");
		FrameworkLogger.info("Suite : " + suite.getName());
		FrameworkLogger.info("========================================");

		// Create Extent Report once for the entire suite
		ExtentManager.getExtentReports();
	}

	@Override
	public void onFinish(ISuite suite) {
		
		FrameworkLogger.info("========================================");
		FrameworkLogger.info("Flushing Extent Report");
		FrameworkLogger.info("Suite : " + suite.getName());
		FrameworkLogger.info("========================================");

		// Flush only once after all tests complete
		ExtentManager.getExtentReports().flush();
	}
}