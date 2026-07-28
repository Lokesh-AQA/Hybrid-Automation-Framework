package tests;

import org.testng.annotations.Test;
import model.TestStep;
import utils.ExcelUtils;

public class KeywordDrivenTest2 extends BaseClass {

	@Test(description = "Executes the enabled keyword-driven Excel test steps")
	public void executeKeywordDrivenScenario() {
		for (TestStep step : ExcelUtils.readTestSteps()) {

			keywordExecutor.execute(step);
		}
	}
}
