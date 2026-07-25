package drivers;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.ConfigUtils;

public class BrowserManager {

	public static WebDriver getDriver() {

		String browser = ConfigUtils.getRequiredProperty("browser");

		switch (browser.toLowerCase()) {

		case "chrome":

			WebDriverManager.chromedriver().setup();

			WebDriver chromeDriver = new ChromeDriver(getChromeOptions());

			if (!isTrue("browser.headless") && isTrue("browser.maximize"))
				chromeDriver.manage().window().maximize();

			return chromeDriver;

		case "edge":

			WebDriverManager.edgedriver().setup();

			WebDriver edgeDriver = new EdgeDriver(getEdgeOptions());

			if (!isTrue("browser.headless") && isTrue("browser.maximize"))
				edgeDriver.manage().window().maximize();

			return edgeDriver;

		case "firefox":

			WebDriverManager.firefoxdriver().setup();

			WebDriver firefoxDriver = new FirefoxDriver(getFirefoxOptions());

			if (!isTrue("browser.headless") && isTrue("browser.maximize"))
				firefoxDriver.manage().window().maximize();

			return firefoxDriver;

		default:

			throw new RuntimeException("Unsupported Browser : " + browser);

		}
	}

	// ================= Chrome =================

	private static ChromeOptions getChromeOptions() {

		ChromeOptions options = new ChromeOptions();

		options.setPageLoadStrategy(PageLoadStrategy.EAGER);

		if (isTrue("browser.private"))
			options.addArguments("--incognito");

		if (isTrue("browser.disable.extensions"))
			options.addArguments("--disable-extensions");

		if (isTrue("browser.disable.popup.blocking"))
			options.addArguments("--disable-popup-blocking");

		if (isTrue("browser.disable.notifications"))
			options.addArguments("--disable-notifications");

		if (isTrue("browser.headless")) {

			options.addArguments("--headless=new");

			options.addArguments("--disable-gpu");

			options.addArguments("--window-size="
					+ ConfigUtils.getIntProperty("browser.window.width")
					+ ","
					+ ConfigUtils.getIntProperty("browser.window.height"));

		}

		return options;

	}

	// ================= Edge =================

	private static EdgeOptions getEdgeOptions() {

		EdgeOptions options = new EdgeOptions();

		options.setPageLoadStrategy(PageLoadStrategy.EAGER);

		if (isTrue("browser.private"))
			options.addArguments("--inprivate");

		if (isTrue("browser.disable.extensions"))
			options.addArguments("--disable-extensions");

		if (isTrue("browser.disable.popup.blocking"))
			options.addArguments("--disable-popup-blocking");

		if (isTrue("browser.disable.notifications"))
			options.addArguments("--disable-notifications");

		if (isTrue("browser.headless")) {

			options.addArguments("--headless=new");

			options.addArguments("--disable-gpu");

			options.addArguments("--window-size="
					+ ConfigUtils.getIntProperty("browser.window.width")
					+ ","
					+ ConfigUtils.getIntProperty("browser.window.height"));

		}

		return options;

	}

	// ================= Firefox =================

	private static FirefoxOptions getFirefoxOptions() {

		FirefoxOptions options = new FirefoxOptions();

		options.setPageLoadStrategy(PageLoadStrategy.EAGER);

		if (isTrue("browser.private"))
			options.addArguments("-private");

		if (isTrue("browser.disable.notifications"))
			options.addPreference("dom.webnotifications.enabled", false);

		if (isTrue("browser.headless")) {

			options.addArguments("-headless");

			options.addArguments("--width="
					+ ConfigUtils.getIntProperty("browser.window.width"));

			options.addArguments("--height="
					+ ConfigUtils.getIntProperty("browser.window.height"));

		}

		return options;

	}

	// ================= Common =================

	private static boolean isTrue(String key) {

		return ConfigUtils.getBooleanProperty(key);

	}

}