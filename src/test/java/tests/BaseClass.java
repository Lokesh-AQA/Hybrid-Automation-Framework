package tests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import executor.KeywordExecutor;
import utils.FrameworkLogger;
import utils.FrameworkStatistics;
import utils.ScreenshotUtils;

public class BaseClass {

    protected KeywordExecutor keywordExecutor;

    @BeforeMethod
    public void setUp() {

        FrameworkStatistics.reset();
        FrameworkStatistics.startExecution();
        ScreenshotUtils.resetCounter();

        FrameworkLogger.info("Framework Started");

        keywordExecutor = new KeywordExecutor();
        keywordExecutor.openBrowser();
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
        }
    }
}