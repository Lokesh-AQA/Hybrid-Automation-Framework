package utils;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {

	private static final int PAGE_LOAD_TIMEOUT = ConfigUtils.getIntProperty("page.load.timeout");

	private static final int ELEMENT_TIMEOUT = ConfigUtils.getIntProperty("element.timeout");

	// Common Wait
	private static WebDriverWait getWait(WebDriver driver) {

		return new WebDriverWait(driver, Duration.ofSeconds(ELEMENT_TIMEOUT));

	}

	// Page Load Wait
	public static void waitForPageLoad(WebDriver driver) {

		new WebDriverWait(driver, Duration.ofSeconds(PAGE_LOAD_TIMEOUT))
				.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
						.equals("complete"));

	}

	// Wait until element is visible.
	public static WebElement waitForElement(WebDriver driver, By locator) {

		return getWait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));

	}

	// Wait until element is clickable.
	public static WebElement waitForClickable(WebDriver driver, By locator) {

		return getWait(driver).until(ExpectedConditions.elementToBeClickable(locator));

	}

	// Wait until element is present in DOM.
	public static WebElement waitForPresence(WebDriver driver, By locator) {

		return getWait(driver).until(ExpectedConditions.presenceOfElementLocated(locator));

	}

	// Wait until element becomes invisible.
	public static boolean waitForInvisibility(WebDriver driver, By locator) {

		return getWait(driver).until(ExpectedConditions.invisibilityOfElementLocated(locator));

	}

	// Wait until title contains expected text.
	public static boolean waitForTitle(WebDriver driver, String title) {

		return getWait(driver).until(ExpectedConditions.titleContains(title));

	}

	// Wait until URL contains expected value.
	public static boolean waitForUrl(WebDriver driver, String url) {

		return getWait(driver).until(ExpectedConditions.urlContains(url));

	}

	// Wait until alert is displayed.
	public static Alert waitForAlert(WebDriver driver) {

		return getWait(driver).until(ExpectedConditions.alertIsPresent());

	}

	// Wait until frame is available and switch to it.
	public static void waitForFrame(WebDriver driver, By locator) {

		getWait(driver).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));

	}

	// Wait until element text is present.
	public static boolean waitForText(WebDriver driver, By locator, String text) {

		return getWait(driver).until(ExpectedConditions.textToBePresentInElementLocated(locator, text));

	}

	// Wait until attribute contains expected value.
	public static boolean waitForAttribute(WebDriver driver, By locator, String attribute, String value) {

		return getWait(driver)
				.until(ExpectedConditions.attributeContains(locator, attribute, value));

	}

}