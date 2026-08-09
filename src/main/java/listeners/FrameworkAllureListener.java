package listeners;

import java.io.File;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.FileSystemResultsWriter;
import io.qameta.allure.testng.AllureTestNg;

import utils.AllureManager;
import utils.FrameworkLogger;

public class FrameworkAllureListener extends AllureTestNg {

	/*
	 * IMPORTANT:
	 *
	 * Allure has its own directory structure.
	 *
	 * Final structure:
	 *
	 * Allure-Reports/ └── <Date-Time>/ └── Allure-results/
	 *
	 * Extent Reports are completely separate:
	 *
	 * Reports/ └── <Date-Time>/ ├── ExtentReport.html └── Screenshots/
	 */

	public FrameworkAllureListener() {

		super(createAllureLifecycle());
	}

	// ==========================================================
	// CREATE ALLURE LIFECYCLE
	// ==========================================================

	private static AllureLifecycle createAllureLifecycle() {

		/*
		 * IMPORTANT:
		 *
		 * AllureManager owns the Allure execution directory.
		 *
		 * We do NOT use ExtentManager here.
		 */
		AllureManager.initialize();

		/*
		 * Get the execution-specific Allure-results directory.
		 *
		 * Example:
		 *
		 * Allure-Reports/ └── 2026-08-09_09-30-00/ └── Allure-results/
		 */
		File allureResultsDirectory = new File(AllureManager.getResultsDirectory());

		/*
		 * Make sure the directory exists.
		 */
		if (!allureResultsDirectory.exists()) {

			if (!allureResultsDirectory.mkdirs() && !allureResultsDirectory.exists()) {

				throw new IllegalStateException(
						"Unable to create Allure-results directory : " + allureResultsDirectory.getAbsolutePath());
			}
		}

		/*
		 * Create the Allure results writer.
		 *
		 * This writer writes Allure files directly into:
		 *
		 * Allure-Reports/ └── <Date-Time>/ └── Allure-results/
		 */
		FileSystemResultsWriter resultsWriter = new FileSystemResultsWriter(allureResultsDirectory.toPath());

		/*
		 * Create the custom Allure lifecycle.
		 */
		AllureLifecycle lifecycle = new AllureLifecycle(resultsWriter);

		/*
		 * IMPORTANT:
		 *
		 * AllureManager uses the static Allure facade:
		 *
		 * Allure.step(...) Allure.addAttachment(...)
		 *
		 * Therefore the same custom lifecycle must be registered globally.
		 */
		Allure.setLifecycle(lifecycle);

		FrameworkLogger.info("Allure Results Directory : " + allureResultsDirectory.getAbsolutePath());

		return lifecycle;
	}
}