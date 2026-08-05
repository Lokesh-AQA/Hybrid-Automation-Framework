package executor;

import java.util.HashMap;
import java.util.Map;

import enums.ScreenshotType;
import keywords.Keywords;
import model.TestStep;
import reports.ExtentTestManager;
import utils.AllureManager;
import utils.ConfigUtils;
import utils.FrameworkLogger;
import utils.FrameworkStatistics;
import utils.ScreenshotUtils;

public class KeywordExecutor {

	private final Keywords keyword = new Keywords();
	private final Map<String, KeywordAction> keywordMap = new HashMap<>();

	public KeywordExecutor() {

		keywordMap.put("navigate", step -> keyword.navigate(step.getTestData()));

		keywordMap.put("input", step -> keyword.input(step.getTestData(), step.getObjectName()));

		keywordMap.put("click", step -> keyword.click(step.getObjectName()));

		keywordMap.put("presskey", step -> keyword.pressKey(step.getTestData()));

		keywordMap.put("clear", step -> keyword.clear(step.getObjectName()));

		keywordMap.put("verifydisabled", step -> keyword.verifyDisabled(step.getObjectName()));

		keywordMap.put("verifytitle", step -> keyword.verifyTitle(step.getTestData()));

		keywordMap.put("verifyurl", step -> keyword.verifyUrl(step.getTestData()));

		keywordMap.put("verifytext", step -> keyword.verifyText(step.getTestData(), step.getObjectName()));

		keywordMap.put("verifyvalue", step -> keyword.verifyValue(step.getTestData(), step.getObjectName()));

		keywordMap.put("verifydisplayed", step -> keyword.verifyDisplayed(step.getObjectName()));

		keywordMap.put("verifyenabled", step -> keyword.verifyEnabled(step.getObjectName()));

		keywordMap.put("opennewtab", step -> keyword.openNewTab());

		keywordMap.put("verifyselected", step -> keyword.verifySelected(step.getObjectName()));

		keywordMap.put("verifyattribute", step -> keyword.verifyAttribute(step.getTestData(), step.getObjectName()));

		keywordMap.put("switchtab", step -> keyword.switchTab(step.getTestData()));

		keywordMap.put("getattribute", step -> {

			String value = keyword.getAttribute(step.getObjectName(), step.getTestData());

			FrameworkLogger.pass("Attribute Retrieved Successfully. Field : " + step.getObjectName() + " | Attribute : "
					+ step.getTestData() + " | Value : " + value);

		});
	}

	public void openBrowser(String browser) {
		keyword.openBrowser(browser);
	}

	public void closeBrowser() {
		keyword.closeBrowser();
	}

	public void execute(TestStep step) {

		String keywordName = step.getKeyword();

		try {

			FrameworkStatistics.incrementTotal();

			KeywordAction action = keywordMap.get(keywordName.toLowerCase());
			
			//System.out.println("Action = "+ action);

			if (action == null) {
				throw new IllegalArgumentException("Unknown Keyword : " + keywordName);
			}

			AllureManager.step(
			        AllureManager.buildKeywordStep(
			                keywordName,
			                step.getObjectName(),
			                step.getTestData()));
			
			action.execute(step);

			FrameworkStatistics.incrementPassed();

			// PASS Screenshot
			if (keyword.getDriver() != null
			        && "true".equalsIgnoreCase(ConfigUtils.getProperty("capture.pass.screenshot"))) {

			    String screenshotPath = ScreenshotUtils.capture(
			            keyword.getDriver(),
			            keywordName,
			            ScreenshotType.PASS);

			    if (screenshotPath != null) {

			        // Extent Report
			        if (ExtentTestManager.getTest() != null) {
			            ExtentTestManager.getTest()
			                    .addScreenCaptureFromPath(screenshotPath);
			        }

			        // Allure Report
			        AllureManager.attachScreenshot(
			                "Screenshot - " +
			                AllureManager.buildKeywordStep(
			                        keywordName,
			                        step.getObjectName(),
			                        step.getTestData()),
			                screenshotPath);
			    }
			}

		} catch (Exception e) {

			// FAIL Screenshot
			if (keyword.getDriver() != null
			        && "true".equalsIgnoreCase(ConfigUtils.getProperty("capture.fail.screenshot"))) {

			    String screenshotPath = ScreenshotUtils.capture(
			            keyword.getDriver(),
			            keywordName,
			            ScreenshotType.FAIL);

			    if (screenshotPath != null) {

			        if (ExtentTestManager.getTest() != null) {
			            ExtentTestManager.getTest()
			                    .addScreenCaptureFromPath(screenshotPath);
			        }

			        AllureManager.attachScreenshot(
			                "Failed Screenshot - " +
			                AllureManager.buildKeywordStep(
			                        keywordName,
			                        step.getObjectName(),
			                        step.getTestData()),
			                screenshotPath);
			    }
			}

			FrameworkLogger.fail("Keyword Failed : " + keywordName);
			FrameworkLogger.fail("Reason : " + e.getMessage());
			FrameworkStatistics.incrementFailed();

			throw new KeywordExecutionException(keywordName, e);

		}
	}
}
