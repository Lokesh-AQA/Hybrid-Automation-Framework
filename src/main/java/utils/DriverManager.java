package utils;

import org.openqa.selenium.WebDriver;

public class DriverManager {

    // Each thread gets its own WebDriver instance
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    /**
     * Stores the WebDriver for the current thread.
     *
     * @param driver WebDriver instance
     */
    public static void setDriver(WebDriver driver) {
        DRIVER.set(driver);
    }

    /**
     * Returns the WebDriver associated with the current thread.
     *
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    /**
     * Closes the browser and removes the WebDriver
     * from the current thread.
     */
    public static void quitDriver() {

        WebDriver driver = DRIVER.get();

        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }

}