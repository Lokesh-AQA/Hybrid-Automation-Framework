package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import model.TestStep;
import utils.ExcelUtils;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@SuppressWarnings("unused")
public class KeywordDrivenTest extends BaseClass {

	@Epic("Hybrid Automation Framework")
	@Feature("Input Field Automation")
	@Story("Keyword Driven Functional Testing")
	@Owner("Lokesh")
	@Severity(SeverityLevel.CRITICAL)
	@Description("Executes keyword-driven test cases using Excel data.")
	
	@Test(description = "Executes the enabled keyword-driven Excel test steps")
	public void executeKeywordDrivenScenario() {
		for (TestStep step : ExcelUtils.readTestSteps()) {

			keywordExecutor.execute(step);
			
			//Assert.fail("Testing Retry");
		}
	}
}
