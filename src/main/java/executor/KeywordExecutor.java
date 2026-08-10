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

import io.qameta.allure.Allure;

public class KeywordExecutor {

	private final Keywords keyword = new Keywords();

	private final Map<String, KeywordAction> keywordMap = new HashMap<>();

	// ==========================================================
	// CONSTRUCTOR
	// ==========================================================

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

			FrameworkLogger.pass("Attribute Retrieved Successfully. " + "Field : " + step.getObjectName()
					+ " | Attribute : " + step.getTestData() + " | Value : " + value);
		});
	}

	// ==========================================================
	// BROWSER LIFECYCLE
	// ==========================================================

	public void openBrowser(String browser) {

		keyword.openBrowser(browser);
	}

	public void closeBrowser() {

		keyword.closeBrowser();
	}

	// ==========================================================
	// EXECUTE KEYWORD
	// ==========================================================

	public void execute(TestStep step) {

		String keywordName = step.getKeyword();

		String stepName = AllureManager.buildKeywordStep(keywordName, step.getObjectName(), step.getTestData());

		// ======================================================
		// STATISTICS
		// ======================================================

		FrameworkStatistics.incrementTotal();

		// ======================================================
		// FIND KEYWORD
		// ======================================================

		KeywordAction action = keywordMap.get(keywordName.toLowerCase());

		if (action == null) {

			FrameworkStatistics.incrementFailed();

			throw new KeywordExecutionException(keywordName,
					new IllegalArgumentException("Unknown Keyword : " + keywordName));
		}

		/*
		 * Start keyword logging context.
		 */
		FrameworkLogger.startKeywordExecution();

		try {

			// ==================================================
			// ALLURE
			// ==================================================

			Allure.step(stepName, () -> {

				try {

					// ==========================================
					// 1. EXECUTE KEYWORD
					// ==========================================

					action.execute(step);

					// ==========================================
					// 2. SCREENSHOT CONFIGURATION
					// ==========================================

					boolean capturePassScreenshot = "true"
							.equalsIgnoreCase(ConfigUtils.getProperty("capture.pass.screenshot"));

					boolean captureFailScreenshot = "true"
							.equalsIgnoreCase(ConfigUtils.getProperty("capture.fail.screenshot"));

					/*
					 * Important:
					 *
					 * If either PASS or FAIL screenshot collection is enabled, we need the
					 * successful step screenshot.
					 *
					 * Why?
					 *
					 * Example:
					 *
					 * PASS screenshot = false FAIL screenshot = true
					 *
					 * If the test later fails, we need:
					 *
					 * navigate input click failed step
					 *
					 * Therefore successful steps must also be captured when fail screenshots are
					 * enabled.
					 */
					boolean captureSuccessfulStep = capturePassScreenshot || captureFailScreenshot;

					String screenshotPath = null;

					if (keyword.getDriver() != null && captureSuccessfulStep) {

						/*
						 * If PASS screenshot is enabled, initially store the screenshot in Pass.
						 *
						 * If the final test later fails, FrameworkListener will move the complete
						 * testcase to Fail.
						 */
						ScreenshotType screenshotType;

						if (capturePassScreenshot) {

							screenshotType = ScreenshotType.PASS;

						} else {

							/*
							 * PASS screenshot disabled, but FAIL screenshot enabled.
							 *
							 * Store the working screenshots in Fail because this test may eventually fail.
							 */
							screenshotType = ScreenshotType.FAIL;
						}

						screenshotPath = ScreenshotUtils.capture(keyword.getDriver(), keywordName, screenshotType);
					}

					// ==========================================
					// 3. PASS MESSAGE
					// ==========================================

					String passMessage = FrameworkLogger.getLastPassMessage();

					if (passMessage == null || passMessage.isBlank()) {

						passMessage = stepName;
					}

					// ==========================================
					// 4. EXTENT PASS
					// ==========================================

					ExtentTestManager.pass(passMessage, screenshotPath);

					// ==========================================
					// 5. STATISTICS
					// ==========================================

					FrameworkStatistics.incrementPassed();

				} catch (Exception e) {

					// ==========================================
					// FAILURE SCREENSHOT CONFIGURATION
					// ==========================================

					boolean captureFailScreenshot = "true"
							.equalsIgnoreCase(ConfigUtils.getProperty("capture.fail.screenshot"));

					String screenshotPath = null;

					/*
					 * Only capture the failed step when fail screenshot collection is enabled.
					 */
					if (keyword.getDriver() != null && captureFailScreenshot) {

						screenshotPath = ScreenshotUtils.capture(keyword.getDriver(), keywordName, ScreenshotType.FAIL);
					}

					// ==========================================
					// FAIL MESSAGE
					// ==========================================

					String failMessage = FrameworkLogger.getLastFailMessage();

					if (failMessage == null || failMessage.isBlank()) {

						failMessage = "Keyword Failed : " + keywordName + " | Reason : " + e.getMessage();
					}

					// ==========================================
					// EXTENT FAIL
					// ==========================================

					ExtentTestManager.fail(failMessage, screenshotPath);

					/*
					 * Allure marks this keyword as failed because the exception is re-thrown.
					 */
					throw e;
				}
			});

		} catch (Exception e) {

			/*
			 * The FAIL Extent row has already been created inside the Allure keyword step.
			 */

			FrameworkStatistics.incrementFailed();

			throw new KeywordExecutionException(keywordName, e);

		} finally {

			/*
			 * Clear keyword logging context.
			 */
			FrameworkLogger.endKeywordExecution();
		}
	}
}