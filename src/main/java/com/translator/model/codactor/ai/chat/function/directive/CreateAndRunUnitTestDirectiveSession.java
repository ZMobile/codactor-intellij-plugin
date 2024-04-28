package com.translator.model.codactor.ai.chat.function.directive;

public class CreateAndRunUnitTestDirectiveSession {
    private String filePath;
    private String testFilePath;
    private String testDescription;
    private boolean unitTestCreated;
    private boolean unitTestRun;

    public CreateAndRunUnitTestDirectiveSession() {
        this.unitTestCreated = false;
        this.unitTestRun = false;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTestDescription() {
        return testDescription;
    }

    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    public boolean isUnitTestCreated() {
        return unitTestCreated;
    }

    public void setUnitTestCreated(boolean unitTestCreated) {
        this.unitTestCreated = unitTestCreated;
    }

    public boolean isUnitTestRun() {
        return unitTestRun;
    }

    public void setUnitTestRun(boolean unitTestRun) {
        this.unitTestRun = unitTestRun;
    }
}
