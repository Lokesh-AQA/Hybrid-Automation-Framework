package reports;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import utils.ConfigUtils;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import utils.GitUtils;

public final class ExtentManager {

	private static ExtentReports extent;
	private static String reportDirectory;

	private ExtentManager() {
	}

	public static synchronized ExtentReports getExtentReports() {

		if (extent == null) {

			String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

			reportDirectory = System.getProperty("user.dir") + File.separator + "Reports" + File.separator + timestamp;

			new File(reportDirectory).mkdirs();

			String reportPath = reportDirectory + File.separator + "ExtentReport.html";

			ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

			sparkReporter.config().setDocumentTitle("Automation Test Report");
			sparkReporter.config().setReportName("Framework01 Execution Report");
			sparkReporter.config().setTheme(Theme.DARK);

			extent = new ExtentReports();

			extent.attachReporter(sparkReporter);

			extent.setSystemInfo("Framework", "Framework01");
			extent.setSystemInfo("Automation", "Selenium + TestNG");
			extent.setSystemInfo("Language", "Java");

			extent.setSystemInfo("Author", ConfigUtils.getRequiredProperty("author"));

			extent.setSystemInfo("Testing Type", ConfigUtils.getRequiredProperty("testing.type"));

			extent.setSystemInfo("Environment", ConfigUtils.getRequiredProperty("environment"));

			extent.setSystemInfo("Application", ConfigUtils.getRequiredProperty("application.name"));

			extent.setSystemInfo("Build Version", ConfigUtils.getRequiredProperty("build.version"));

			extent.setSystemInfo("Git Branch", GitUtils.getBranchName());

			extent.setSystemInfo("Git Commit", GitUtils.getCommitId());

			extent.setSystemInfo("Executed By", System.getProperty("user.name"));
			extent.setSystemInfo("Operating System", System.getProperty("os.name"));
			extent.setSystemInfo("Java Version", System.getProperty("java.version"));
		}

		return extent;
	}

	public static synchronized String getReportDirectory() {

	    if (reportDirectory == null) {

	        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
	                .format(new Date());

	        reportDirectory = System.getProperty("user.dir")
	                + File.separator
	                + "Reports"
	                + File.separator
	                + timestamp;

	        new File(reportDirectory).mkdirs();
	    }

	    return reportDirectory;
	}
}