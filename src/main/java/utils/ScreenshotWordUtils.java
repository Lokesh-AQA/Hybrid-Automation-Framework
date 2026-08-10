package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import reports.ExtentManager;

public final class ScreenshotWordUtils {

	private ScreenshotWordUtils() {
		// Utility class
	}

	// ==========================================================
	// CREATE WORD DOCUMENT
	// ==========================================================

	/**
	 * Creates one Word document for the complete test case.
	 *
	 * Expected structure:
	 *
	 * Screenshots/ ├── Pass/ │ └── TestCaseName.docx │ └── Fail/ └──
	 * TestCaseName.docx
	 *
	 * PNG screenshots are stored temporarily inside the same Pass/Fail folder while
	 * the document is being generated.
	 *
	 * After the Word document is created, FrameworkListener removes the PNG files.
	 */
	public static void createWord(String testCaseName, String resultFolder) {

		if (testCaseName == null || testCaseName.isBlank()) {

			FrameworkLogger.warn("Word generation skipped. " + "Test case name is empty.");

			return;
		}

		if (resultFolder == null || resultFolder.isBlank()) {

			FrameworkLogger.warn("Word generation skipped. " + "Result folder is empty.");

			return;
		}

		try {

			// ==================================================
			// EXECUTION DIRECTORY
			// ==================================================

			File executionFolder = new File(ExtentManager.getExecutionDirectory());

			// ==================================================
			// RESULT DIRECTORY
			// ==================================================

			File resultDirectory = new File(executionFolder, "Screenshots" + File.separator + resultFolder);

			if (!resultDirectory.exists() || !resultDirectory.isDirectory()) {

				FrameworkLogger.warn("Screenshot result directory not found : " + resultDirectory.getAbsolutePath());

				return;
			}

			// ==================================================
			// SAFE TEST CASE NAME
			// ==================================================

			String safeTestCaseName = sanitizeFileName(testCaseName);

			// ==================================================
			// GET SCREENSHOTS
			// ==================================================

			File[] screenshots = resultDirectory
					.listFiles(file -> file.isFile() && file.getName().startsWith(safeTestCaseName + "_")
							&& file.getName().toLowerCase().endsWith(".png"));

			if (screenshots == null || screenshots.length == 0) {

				FrameworkLogger.warn("No screenshots found for test : " + testCaseName);

				return;
			}

			// ==================================================
			// SORT BY EXECUTION SEQUENCE
			// ==================================================

			Arrays.sort(screenshots, Comparator.comparingInt(file -> getSequence(file, safeTestCaseName)));

			// ==================================================
			// WORD FILE
			// ==================================================

			String wordFileName = safeTestCaseName + ".docx";

			File wordFile = new File(resultDirectory, wordFileName);

			// ==================================================
			// CREATE WORD DOCUMENT
			// ==================================================

			try (XWPFDocument document = new XWPFDocument()) {

				// ==================================================
				// TITLE
				// ==================================================

				XWPFParagraph titleParagraph = document.createParagraph();

				titleParagraph.setAlignment(ParagraphAlignment.CENTER);

				XWPFRun titleRun = titleParagraph.createRun();

				titleRun.setBold(true);

				titleRun.setFontSize(16);

				titleRun.setText("Test Case : " + testCaseName);

				// ==================================================
				// SPACE
				// ==================================================

				document.createParagraph();

				// ==================================================
				// SCREENSHOTS
				// ==================================================

				for (File screenshot : screenshots) {

					// ==============================================
					// KEYWORD HEADING
					// ==============================================

					String keyword = extractKeyword(screenshot.getName(), safeTestCaseName);

					XWPFParagraph headingParagraph = document.createParagraph();

					XWPFRun headingRun = headingParagraph.createRun();

					headingRun.setBold(true);

					headingRun.setFontSize(13);

					headingRun.setText(keyword);

					// ==============================================
					// SCREENSHOT IMAGE
					// ==============================================

					XWPFParagraph imageParagraph = document.createParagraph();

					imageParagraph.setAlignment(ParagraphAlignment.CENTER);

					XWPFRun imageRun = imageParagraph.createRun();

					try (FileInputStream inputStream = new FileInputStream(screenshot)) {

						imageRun.addPicture(inputStream, PictureType.PNG, screenshot.getName(), Units.toEMU(6 * 72),
								Units.toEMU(3.5 * 72));
					}

					// ==============================================
					// SEPARATOR
					// ==============================================

					XWPFParagraph separatorParagraph = document.createParagraph();

					XWPFRun separatorRun = separatorParagraph.createRun();

					separatorRun.setText("------------------------------------------------");

					document.createParagraph();
				}

				// ==================================================
				// WRITE WORD FILE
				// ==================================================

				try (FileOutputStream outputStream = new FileOutputStream(wordFile)) {

					document.write(outputStream);
				}
			}

			FrameworkLogger.info("Screenshot Word document created : " + wordFile.getAbsolutePath());

		} catch (Exception e) {

			FrameworkLogger.error("Unable to create screenshot Word document " + "for test : " + testCaseName, e);
		}
	}

	// ==========================================================
	// GET SCREENSHOT SEQUENCE
	// ==========================================================

	/**
	 * Extracts the sequence from:
	 *
	 * TestCaseName_001_navigate_20260809_193000_123.png
	 *
	 * The test case name is removed first, so underscores inside the test case name
	 * do not cause a problem.
	 */
	private static int getSequence(File file, String safeTestCaseName) {

		try {

			String fileName = file.getName();

			String prefix = safeTestCaseName + "_";

			if (!fileName.startsWith(prefix)) {

				return Integer.MAX_VALUE;
			}

			String remaining = fileName.substring(prefix.length());

			/*
			 * remaining:
			 *
			 * 001_navigate_20260809_193000_123.png
			 */

			int underscoreIndex = remaining.indexOf('_');

			if (underscoreIndex <= 0) {

				return Integer.MAX_VALUE;
			}

			String sequence = remaining.substring(0, underscoreIndex);

			return Integer.parseInt(sequence);

		} catch (Exception e) {

			return Integer.MAX_VALUE;
		}
	}

	// ==========================================================
	// EXTRACT KEYWORD
	// ==========================================================

	/**
	 * Extracts the keyword from:
	 *
	 * TestCaseName_001_navigate_20260809_193000_123.png
	 *
	 * Result:
	 *
	 * navigate
	 */
	private static String extractKeyword(String fileName, String safeTestCaseName) {

		if (fileName == null || fileName.isBlank()) {

			return "Screenshot";
		}

		try {

			String prefix = safeTestCaseName + "_";

			if (!fileName.startsWith(prefix)) {

				return "Screenshot";
			}

			String remaining = fileName.substring(prefix.length());

			/*
			 * remaining:
			 *
			 * 001_navigate_20260809_193000_123.png
			 */

			String[] parts = remaining.split("_");

			/*
			 * parts[0] = 001 parts[1] = navigate parts[2] = 20260809 parts[3] = 193000
			 * parts[4] = 123.png
			 */

			if (parts.length < 2) {

				return "Screenshot";
			}

			return parts[1];

		} catch (Exception e) {

			return "Screenshot";
		}
	}

	// ==========================================================
	// SANITIZE FILE NAME
	// ==========================================================

	private static String sanitizeFileName(String name) {

		if (name == null || name.isBlank()) {

			return "TestCase";
		}

		return name.trim().replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s+", "_");
	}
}