package utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import reports.ExtentManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import enums.ScreenshotType;

public class ScreenshotUtils {

    // Each thread gets its own screenshot counter
    private static final ThreadLocal<Integer> screenshotCounter =
            ThreadLocal.withInitial(() -> 1);

    /**
     * Captures a screenshot and returns its absolute file path.
     *
     * @param driver WebDriver instance
     * @param keywordName Name of the keyword or test
     * @param status Pass or Fail
     * @return Screenshot absolute path, or null if capture fails
     */
    public static String capture(WebDriver driver,
            String keywordName,
            ScreenshotType status)	 {

        if (driver == null) {
            return null;
        }

        try {

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            File folder = new File(
                    ExtentManager.getReportDirectory()
                    + File.separator
                    + "Screenshots"
                    + File.separator
                    + status.name());

            if (!folder.exists()) {
                folder.mkdirs();
            }

            int count = screenshotCounter.get();
            String sequence = String.format("%03d", count);
            screenshotCounter.set(count + 1);

            String fileName = sequence + "_" + keywordName + "_" + timeStamp + ".png";

            File destinationFile = new File(folder, fileName);

            FileUtils.copyFile(source, destinationFile);

            // Return the screenshot path for Extent Report
            return destinationFile.getAbsolutePath();

        } catch (Exception e) {

            FrameworkLogger.fail("Unable to Capture Screenshot.");
            FrameworkLogger.debug(e.getMessage());

            return null;
        }
    }

    // Reset counter before every test execution
    public static void resetCounter() {
        screenshotCounter.set(1);
    }
}