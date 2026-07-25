package model;

public class TestStep {

    private String keyword;
    private String testData;
    private String objectName;
    private String runMode;

    public TestStep(String keyword, String testData,
                    String objectName, String runMode) {

        this.keyword = keyword;
        this.testData = testData;
        this.objectName = objectName;
        this.runMode = runMode;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getTestData() {
        return testData;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getRunMode() {
        return runMode;
    }
}