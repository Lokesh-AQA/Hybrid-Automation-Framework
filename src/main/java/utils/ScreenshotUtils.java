package utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotUtils {

	// Screenshot sequence number
	private static int screenshotCounter = 1;

	public static void capture(WebDriver driver, String keywordName, String status) {

		if (driver == null) {
			return;
		}

		try {

			File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

			// Pass / Fail Folder
			File folder = new File("Screenshots/" + status);

			if (!folder.exists()) {
				folder.mkdirs();
			}

			// 001, 002, 003...
			String sequence = String.format("%03d", screenshotCounter++);

			// Screenshot Name
			String fileName = sequence + "_" + keywordName + "_" + timeStamp + ".png";

			File destinationFile = new File(folder, fileName);

			FileUtils.copyFile(source, destinationFile);

		} catch (Exception e) {

			FrameworkLogger.fail("Unable to Capture Screenshot.");
			FrameworkLogger.debug(e.getMessage());

		}
	}

	// Reset counter before every test execution
	public static void resetCounter() {
		screenshotCounter = 1;
	}
}