	package keywords;
	
	import java.util.ArrayList;
	import utils.DriverManager;
	import org.openqa.selenium.By;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.WindowType;
	
	import utils.FrameworkLogger;
	import utils.KeyboardUtils;
	import utils.LocatorUtils;
	import utils.ScreenshotUtils;
	import utils.WaitUtils;
	import drivers.BrowserManager;
	import utils.ConfigUtils;
	
	public class Keywords {
	
	    // ==========================================================
	    // Global Variables
	    // ==========================================================
	
	  
	    private ArrayList<String> tabs;
	    
	    private WebDriver driver() {
	        return DriverManager.getDriver();
	    }
	
	    // ==========================================================
	    // Browser Keywords
	    // ==========================================================
	
	    public void openBrowser(String browser) {

	        BrowserManager.getDriver(browser);

	        FrameworkLogger.browserOpened(browser);
	    }
	
	    public void navigate(String urlKey) {
	
	    	String url = ConfigUtils.getRequiredProperty(urlKey);
	
	    	driver().get(url);
	    	
	    	WaitUtils.waitForPageLoad(driver());
	
	    	FrameworkLogger.urlLaunched(url);
	    }
	
	    public void openNewTab() {
	
			driver().switchTo().newWindow(WindowType.TAB);
	
			FrameworkLogger.pass("New Browser Tab Opened Successfully.");
	
		}
	
	    public void switchTab(String testData) {
	
			tabs = new ArrayList<>(driver().getWindowHandles());
	
			int index = Integer.parseInt(testData);
	
			if (index >= 0 && index < tabs.size()) {
	
				driver().switchTo().window(tabs.get(index));
	
				FrameworkLogger.pass("Switched to Tab : " + index);
	
				try {
	
					int delay = ConfigUtils.getIntProperty("tab.switch.delay");
	
					Thread.sleep(delay);
	
				} catch (InterruptedException e) {
	
					Thread.currentThread().interrupt();
					FrameworkLogger.fail("Tab switch was interrupted.");
					FrameworkLogger.debug(e.getMessage());
					throw new IllegalStateException("Tab switch was interrupted.", e);
				}
			} else {
	
				FrameworkLogger.fail("Invalid Tab Index : " + index);
				throw new IllegalArgumentException("Invalid Tab Index : " + index);
			}
		}
	
	    public void closeBrowser() {
	
	        try {
	
	            DriverManager.quitDriver();
	
	            FrameworkLogger.browserClosed();
	
	        } catch (Exception e) {
	
	            FrameworkLogger.fail("Unable to close browser.");
	            FrameworkLogger.debug(e.getMessage());
	
	            throw new IllegalStateException("Unable to close browser.", e);
	        }
	    }
	
	    // ==========================================================
	    // Action Keywords
	    // ==========================================================
	
	    public void input(String testData, String objectName) {
	
			By locator = LocatorUtils.getLocator(objectName);
	
			WaitUtils.waitForElement(driver(), locator).sendKeys(testData);
	
			FrameworkLogger.valueEntered(objectName, testData);
		}
	
	    public void click(String objectName) {
	
			By locator = LocatorUtils.getLocator(objectName);
	
			WaitUtils.waitForClickable(driver(), locator).click();
	
			FrameworkLogger.elementClicked(objectName);
		}
	
	    public void clear(String objectName) {
	
			By locator = LocatorUtils.getLocator(objectName);
	
			WaitUtils.waitForElement(driver(), locator).clear();
	
			FrameworkLogger.elementCleared(objectName);
		}
	
	    public void pressKey(String testData) {
	
			KeyboardUtils.pressKey(driver(), testData);
	
			FrameworkLogger.pressKey(testData);
		}
	
	    // ==========================================================
	    // Get Keywords
	    // ==========================================================
	
	    public String getAttribute(String objectName, String attributeName) {
	
			By locator = LocatorUtils.getLocator(objectName);
	
			return WaitUtils.waitForElement(driver(), locator).getAttribute(attributeName);
		}
	
	    // ==========================================================
	    // Verification Keywords
	    // ==========================================================
	
	    public void verifyTitle(String expectedTitle) {
	
			String actualTitle = driver().getTitle();
	
			if (actualTitle.contains(expectedTitle)) {
	
				FrameworkLogger.pass("Page Title Verified Successfully.");
				FrameworkLogger.info("Expected Title : " + expectedTitle);
				FrameworkLogger.info("Actual Title : " + actualTitle);
	
			} else {
	
				FrameworkLogger.fail("Page Title Verification Failed.");
				FrameworkLogger.info("Expected Title : " + expectedTitle);
				FrameworkLogger.info("Actual Title : " + actualTitle);
	
				ScreenshotUtils.capture(driver(), "verifyTitle", "Fail");
	
				throw new RuntimeException("Expected Title : " + expectedTitle + " | Actual Title : " + actualTitle);
			}
		}
	
	    public void verifyUrl(String expectedUrl) {
	
			String actualUrl = driver().getCurrentUrl();
	
			if (actualUrl.equals(expectedUrl)) {
	
				FrameworkLogger.pass("Page URL Verified Successfully.");
				FrameworkLogger.info("Expected URL : " + expectedUrl);
				FrameworkLogger.info("Actual URL   : " + actualUrl);
	
			} else {
	
				FrameworkLogger.fail("Page URL Verification Failed.");
				FrameworkLogger.info("Expected URL : " + expectedUrl);
				FrameworkLogger.info("Actual URL   : " + actualUrl);
	
				ScreenshotUtils.capture(driver(), "verifyUrl", "Fail");
	
				throw new RuntimeException("Expected URL : " + expectedUrl + "\nActual URL   : " + actualUrl);
			}
		}
	
	    public void verifyText(String expectedText, String objectName) {
	
			By locator = LocatorUtils.getLocator(objectName);
	
			WebElement element = WaitUtils.waitForElement(driver(), locator);
	
			String actualText = element.getText();
	
			if (actualText.equals(expectedText)) {
	
				FrameworkLogger.pass("Text Verified Successfully.");
				FrameworkLogger.info("Expected Text : " + expectedText);
				FrameworkLogger.info("Actual Text   : " + actualText);
	
			} else {
	
				FrameworkLogger.fail("Text Verification Failed.");
				FrameworkLogger.info("Expected Text : " + expectedText);
				FrameworkLogger.info("Actual Text   : " + actualText);
	
				ScreenshotUtils.capture(driver(), "verifyText", "Fail");
	
				throw new RuntimeException("Expected Text : " + expectedText + "\nActual Text   : " + actualText);
			}
		}
	
	    public void verifyValue(String expectedValue, String objectName) {
	
			By locator = LocatorUtils.getLocator(objectName);
	
			WebElement element = WaitUtils.waitForElement(driver(), locator);
	
			String actualValue = element.getAttribute("value");
	
			if (actualValue.equals(expectedValue)) {
	
				FrameworkLogger.pass("Value Verified Successfully.");
				FrameworkLogger.info("Expected Value : " + expectedValue);
				FrameworkLogger.info("Actual Value   : " + actualValue);
	
			} else {
	
				FrameworkLogger.fail("Value Verification Failed.");
				FrameworkLogger.info("Expected Value : " + expectedValue);
				FrameworkLogger.info("Actual Value   : " + actualValue);
	
				ScreenshotUtils.capture(driver(), "verifyValue", "Fail");
	
				throw new RuntimeException("Expected Value : " + expectedValue + "\nActual Value   : " + actualValue);
			}
		}
	
	    public void verifyAttribute(String attributeName, String objectName) {
	
			By locator = LocatorUtils.getLocator(objectName);
	
			WebElement element = WaitUtils.waitForElement(driver(), locator);
	
			String attributeValue = element.getAttribute(attributeName);
	
			if (attributeValue != null && !attributeValue.isEmpty()) {
	
				FrameworkLogger.pass("Attribute Verified Successfully.");
				FrameworkLogger.info("Element   : " + objectName);
				FrameworkLogger.info("Attribute : " + attributeName);
				FrameworkLogger.info("Value     : " + attributeValue);
	
			} else {
	
				FrameworkLogger.fail("Attribute Verification Failed.");
	
				ScreenshotUtils.capture(driver(), "verifyAttribute", "Fail");
	
				throw new RuntimeException("Attribute '" + attributeName + "' not found for element : " + objectName);
			}
		}
	
	    public void verifyDisplayed(String objectName) {
	
			By locator = LocatorUtils.getLocator(objectName);
	
			WebElement element = WaitUtils.waitForElement(driver(), locator);
	
			if (element.isDisplayed()) {
	
				FrameworkLogger.pass(objectName + " is Displayed.");
	
			} else {
	
				FrameworkLogger.fail(objectName + " is Not Displayed.");
	
				ScreenshotUtils.capture(driver(), "verifyDisplayed", "Fail");
	
				throw new RuntimeException(objectName + " is Not Displayed.");
			}
		}
	
	    public void verifyEnabled(String objectName) {
	
			By locator = LocatorUtils.getLocator(objectName);
	
			WebElement element = WaitUtils.waitForElement(driver(), locator);
	
			if (element.isEnabled()) {
	
				FrameworkLogger.pass(objectName + " is Enabled.");
	
			} else {
	
				FrameworkLogger.fail(objectName + " is Disabled.");
	
				ScreenshotUtils.capture(driver(), "verifyEnabled", "Fail");
	
				throw new RuntimeException(objectName + " is Disabled.");
			}
		}
	
	    public void verifyDisabled(String objectName) {
	
			By locator = LocatorUtils.getLocator(objectName);
	
			WebElement element = WaitUtils.waitForElement(driver(), locator);
	
			if (!element.isEnabled()) {
	
				FrameworkLogger.pass(objectName + " is Disabled.");
	
			} else {
	
				FrameworkLogger.fail(objectName + " is Enabled.");
				ScreenshotUtils.capture(driver(), objectName, "Fail");
	
				throw new RuntimeException(objectName + " should be disabled.");
			}
		}
	
	    public void verifySelected(String objectName) {
	
			By locator = LocatorUtils.getLocator(objectName);
	
			WebElement element = WaitUtils.waitForElement(driver(), locator);
	
			if (element.isSelected()) {
	
				FrameworkLogger.pass(objectName + " is Selected.");
	
			} else {
	
				FrameworkLogger.fail(objectName + " is Not Selected.");
	
				ScreenshotUtils.capture(driver(), "verifySelected", "Fail");
	
				throw new RuntimeException(objectName + " is Not Selected.");
			}
		}
	
	    // ==========================================================
	    // Utility Methods
	    // ==========================================================
	
	    public WebDriver getDriver() {
	        return driver();
	    }
	}