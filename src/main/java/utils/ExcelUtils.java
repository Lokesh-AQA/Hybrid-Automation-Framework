package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.TestStep;

public class ExcelUtils {

	private static final String TEST_DATA_RESOURCE = "testdata/TestData.xlsx";
	private static final String SHEET_NAME = "Sheet1";

	public static List<TestStep> readTestSteps() {
		try (InputStream input = ExcelUtils.class.getClassLoader().getResourceAsStream(TEST_DATA_RESOURCE)) {
			if (input == null) {
				throw new IllegalStateException("Test data file not found: " + TEST_DATA_RESOURCE);
			}

			try (XSSFWorkbook workbook = new XSSFWorkbook(input)) {
				XSSFSheet sheet = workbook.getSheet(SHEET_NAME);
				if (sheet == null) {
					throw new IllegalStateException("Worksheet not found: " + SHEET_NAME);
				}

				DataFormatter formatter = new DataFormatter();
				List<TestStep> testSteps = new ArrayList<>();

				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					Row row = sheet.getRow(i);

					// Skip Empty Row
					if (row == null) {
						continue;
					}

					String keyword = formatter.formatCellValue(row.getCell(1)).trim();
					String testData = formatter.formatCellValue(row.getCell(2)).trim();
					String objectName = formatter.formatCellValue(row.getCell(3)).trim();
					String runMode = formatter.formatCellValue(row.getCell(4)).trim();

					// Skip Empty Keyword
					if (keyword.isEmpty()) {
						continue;
					}

					if (runMode.equalsIgnoreCase("yes")) {

						TestStep currentStep = new TestStep(keyword, testData, objectName, runMode);
						testSteps.add(currentStep);
						// testSteps.add(new TestStep(keyword, testData, objectName, runMode));

					} else {
						FrameworkStatistics.incrementSkipped();
					}
				}

				return testSteps;
			}
		} catch (IOException e) {
			throw new IllegalStateException("Unable to read test data file: " + TEST_DATA_RESOURCE, e);
		}
	}
}
