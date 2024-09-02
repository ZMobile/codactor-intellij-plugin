package com.translator.model.codactor.ai.chat.function.directive;

public class CreateAndRunUnitTestDirectiveSession extends DirectiveSession {
    private String filePath;
    private String testFilePath;
    private String testDescription;
    private String testResult;
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

    public String getTestFilePath() {
        return testFilePath;
    }

    public void setTestFilePath(String testFilePath) {
        this.testFilePath = testFilePath;
    }

    public String getTestDescription() {
        return testDescription;
    }

    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
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
