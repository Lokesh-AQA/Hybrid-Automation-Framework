package tests;

import java.lang.reflect.Method;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import executor.KeywordExecutor;
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

		// Use config.properties if browser is not passed from TestNG
		if (browser == null || browser.trim().isEmpty()) {
			browser = ConfigUtils.getRequiredProperty("browser");
		}

		LogContext.setTestName(method.getName());
		LogContext.setThreadId();
		LogContext.setBrowser(browser);

		FrameworkStatistics.reset();
		FrameworkStatistics.startExecution();
		ScreenshotUtils.resetCounter();

		FrameworkLogger.info("Framework Started");

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
			LogContext.clear();
		}
	}
}