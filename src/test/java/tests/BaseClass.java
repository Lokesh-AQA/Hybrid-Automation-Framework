package tests;

import java.lang.reflect.Method;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import executor.KeywordExecutor;
import reports.ExtentTestManager;
import utils.AllureManager;
import utils.ConfigUtils;
import utils.FrameworkLogger;
import utils.FrameworkStatistics;
import utils.LogContext;
import utils.ScreenshotUtils;

public class BaseClass {

	protected KeywordExecutor keywordExecutor;

	@BeforeMethod
	@Parameters("browser")
	public void setUp(@Optional("") String browser, Method method) {

		// ======================================================
		// Browser
		// ======================================================

		if (browser == null || browser.trim().isEmpty()) {

			browser = ConfigUtils.getRequiredProperty("browser");
		}

		// ======================================================
		// Test Context
		// ======================================================

		LogContext.setTestName(method.getName());

		LogContext.setThreadId();

		LogContext.setBrowser(browser);

		// ======================================================
		// Framework Statistics
		// ======================================================

		FrameworkStatistics.reset();

		FrameworkStatistics.startExecution();

		// ======================================================
		// Allure
		// ======================================================

		AllureManager.initialize();

		// ======================================================
		// Screenshot
		// ======================================================

		ScreenshotUtils.resetCounter();

		// ======================================================
		// Framework
		// ======================================================

		FrameworkLogger.info("Framework Started");

		// ======================================================
		// Keyword Executor
		// ======================================================

		keywordExecutor = new KeywordExecutor();

		keywordExecutor.openBrowser(browser);
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() {

		try {

			if (keywordExecutor != null) {

				keywordExecutor.closeBrowser();
			}

		} finally {

			FrameworkStatistics.endExecution();

			FrameworkStatistics.printSummary();

			/*
			 * IMPORTANT:
			 *
			 * Do NOT flush ExtentReports here.
			 *
			 * ExtentSuiteListener will flush the single ExtentReports instance at suite
			 * completion.
			 */

			ExtentTestManager.unload();

			LogContext.clear();
		}
	}
}