package listeners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.testng.ISuite;
import org.testng.ISuiteListener;

import utils.AllureManager;
import utils.FrameworkLogger;

public class AllureSuiteListener implements ISuiteListener {

	// ==========================================================
	// SUITE START
	// ==========================================================

	@Override
	public void onStart(ISuite suite) {

		FrameworkLogger.info("========================================");

		FrameworkLogger.info("Allure Execution Initialization Started");

		FrameworkLogger.info("Suite : " + suite.getName());

		FrameworkLogger.info("========================================");

		/*
		 * Create ONE Allure execution directory.
		 *
		 * Example:
		 *
		 * Allure-Reports/ └── 2026-08-09_10-30-00/ └── Allure-results/
		 */
		AllureManager.initialize();
	}

	// ==========================================================
	// SUITE FINISH
	// ==========================================================

	@Override
	public void onFinish(ISuite suite) {

		FrameworkLogger.info("========================================");

		FrameworkLogger.info("Moving Allure Results");

		FrameworkLogger.info("Suite : " + suite.getName());

		FrameworkLogger.info("========================================");

		moveAllureResults();
	}

	// ==========================================================
	// MOVE ALLURE RESULTS
	// ==========================================================

	private void moveAllureResults() {

		/*
		 * Allure TestNG writes temporary results here because
		 * src/test/resources/allure.properties contains:
		 *
		 * allure.results.directory=target/allure-results
		 */
		File sourceDirectory = new File(System.getProperty("user.dir"), "target" + File.separator + "allure-results");

		/*
		 * Nothing to move.
		 */
		if (!sourceDirectory.exists()) {

			FrameworkLogger.warn("Temporary Allure results directory not found : " + sourceDirectory.getAbsolutePath());

			return;
		}

		/*
		 * Final location:
		 *
		 * Allure-Reports/ └── <Date-Time>/ └── Allure-results/
		 */
		File targetDirectory = new File(AllureManager.getResultsDirectory());

		createDirectory(targetDirectory);

		FrameworkLogger.info("Temporary Allure results : " + sourceDirectory.getAbsolutePath());

		FrameworkLogger.info("Final Allure results      : " + targetDirectory.getAbsolutePath());

		File[] files = sourceDirectory.listFiles();

		if (files == null) {

			FrameworkLogger.warn("Unable to read temporary Allure results directory.");

			return;
		}

		/*
		 * Move every generated Allure file.
		 */
		for (File sourceFile : files) {

			File targetFile = new File(targetDirectory, sourceFile.getName());

			try {

				moveFile(sourceFile.toPath(), targetFile.toPath());

			} catch (IOException e) {

				throw new IllegalStateException("Unable to move Allure result file : " + sourceFile.getAbsolutePath(),
						e);
			}
		}

		/*
		 * Remove temporary target/allure-results.
		 */
		deleteDirectory(sourceDirectory);

		FrameworkLogger.info("Allure results successfully moved.");

		FrameworkLogger.info("Final Allure results directory : " + targetDirectory.getAbsolutePath());
	}

	// ==========================================================
	// CREATE DIRECTORY
	// ==========================================================

	private void createDirectory(File directory) {

		if (directory.exists()) {

			return;
		}

		if (!directory.mkdirs() && !directory.exists()) {

			throw new IllegalStateException("Unable to create directory : " + directory.getAbsolutePath());
		}
	}

	// ==========================================================
	// MOVE FILE
	// ==========================================================

	private void moveFile(Path source, Path target) throws IOException {

		Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
	}

	// ==========================================================
	// DELETE DIRECTORY
	// ==========================================================

	private void deleteDirectory(File directory) {

		if (!directory.exists()) {

			return;
		}

		File[] files = directory.listFiles();

		if (files != null) {

			for (File file : files) {

				if (file.isDirectory()) {

					deleteDirectory(file);

				} else {

					if (!file.delete()) {

						throw new IllegalStateException("Unable to delete file : " + file.getAbsolutePath());
					}
				}
			}
		}

		if (!directory.delete()) {

			throw new IllegalStateException("Unable to delete directory : " + directory.getAbsolutePath());
		}
	}
}