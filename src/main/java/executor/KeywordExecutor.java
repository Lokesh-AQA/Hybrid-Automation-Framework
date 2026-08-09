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
	// Constructor
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
	// Browser Lifecycle
	// ==========================================================

	public void openBrowser(String browser) {

		keyword.openBrowser(browser);
	}

	public void closeBrowser() {

		keyword.closeBrowser();
	}

	// ==========================================================
	// Execute Keyword
	// ==========================================================

	public void execute(TestStep step) {

		String keywordName = step.getKeyword();

		String stepName = AllureManager.buildKeywordStep(keywordName, step.getObjectName(), step.getTestData());

		// ======================================================
		// Statistics
		// ======================================================

		FrameworkStatistics.incrementTotal();

		// ======================================================
		// Find Keyword Action
		// ======================================================

		KeywordAction action = keywordMap.get(keywordName.toLowerCase());

		if (action == null) {

			FrameworkStatistics.incrementFailed();

			throw new KeywordExecutionException(keywordName,
					new IllegalArgumentException("Unknown Keyword : " + keywordName));
		}

		/*
		 * Start keyword logging context.
		 *
		 * FrameworkLogger.pass() and fail() will now temporarily store their messages.
		 */
		FrameworkLogger.startKeywordExecution();

		try {

			// ==================================================
			// ALLURE
			// ==================================================

			/*
			 * Keep the Allure keyword step open while:
			 *
			 * 1. Keyword executes 2. Screenshot is captured 3. Screenshot is attached
			 */
			Allure.step(stepName, () -> {

				try {

					// ==================================
					// 1. Execute Keyword
					// ==================================

					action.execute(step);

					// ==================================
					// 2. PASS Screenshot
					// ==================================

					String screenshotPath = null;

					boolean capturePassScreenshot = "true"
							.equalsIgnoreCase(ConfigUtils.getProperty("capture.pass.screenshot"));

					if (keyword.getDriver() != null && capturePassScreenshot) {

						screenshotPath = ScreenshotUtils.capture(keyword.getDriver(), keywordName, ScreenshotType.PASS);
					}

					// ==================================
					// 3. PASS Message
					// ==================================

					String passMessage = FrameworkLogger.getLastPassMessage();

					/*
					 * If the keyword did not generate its own FrameworkLogger.pass() message, use
					 * the standard keyword description.
					 */
					if (passMessage == null || passMessage.isBlank()) {

						passMessage = stepName;
					}

					// ==================================
					// 4. EXTENT PASS
					// ==================================

					/*
					 * IMPORTANT:
					 *
					 * Screenshot is attached directly to this PASS log entry.
					 *
					 * It will NOT appear separately at the top of the report.
					 */
					ExtentTestManager.pass(passMessage, screenshotPath);

					// ==================================
					// 5. Statistics
					// ==================================

					FrameworkStatistics.incrementPassed();

				} catch (Exception e) {

					// ================================
					// FAIL Screenshot
					// ================================

					String screenshotPath = null;

					boolean captureFailScreenshot = "true"
							.equalsIgnoreCase(ConfigUtils.getProperty("capture.fail.screenshot"));

					if (keyword.getDriver() != null && captureFailScreenshot) {

						screenshotPath = ScreenshotUtils.capture(keyword.getDriver(), keywordName, ScreenshotType.FAIL);
					}

					// ================================
					// FAIL Message
					// ================================

					String failMessage = FrameworkLogger.getLastFailMessage();

					if (failMessage == null || failMessage.isBlank()) {

						failMessage = "Keyword Failed : " + keywordName + " | Reason : " + e.getMessage();
					}

					// ================================
					// EXTENT FAIL
					// ================================

					/*
					 * One FAIL row + its screenshot.
					 */
					ExtentTestManager.fail(failMessage, screenshotPath);

					/*
					 * Allure will mark this keyword step as failed because the exception is
					 * re-thrown.
					 */
					throw e;
				}
			});

		} catch (Exception e) {

			/*
			 * Do NOT call FrameworkLogger.fail() here.
			 *
			 * The FAIL Extent row was already created inside the Allure keyword step.
			 */

			FrameworkStatistics.incrementFailed();

			throw new KeywordExecutionException(keywordName, e);

		} finally {

			/*
			 * Clear the keyword logging context.
			 */
			FrameworkLogger.endKeywordExecution();
		}
	}
}