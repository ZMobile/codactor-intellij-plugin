package com.translator.model.codactor.ai.chat.function.directive.test;

import org.junit.runner.Result;

public class ResultsResource {

    public static class Builder {
        private String filePath;
        private String buildOutputPath;
        private Result result;
        private String error;

        public Builder withFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder withBuildOutputPath(String buildOutputPath) {
            this.buildOutputPath = buildOutputPath;
            return this;
        }

        public Builder withResult(Result result) {
            this.result = result;
            return this;
        }

        public Builder withError(String error) {
            this.error = error;
            return this;
        }

        public ResultsResource build() {
            return new ResultsResource(filePath, buildOutputPath, result, error);
        }
    }

    private String filePath;
    private String buildOutputPath;
    private Result result;
    private String error;

    public ResultsResource(String filePath, String buildOutputPath, Result result) {
        this.filePath = filePath;
        this.buildOutputPath = buildOutputPath;
        this.result = result;
    }

    public ResultsResource(String filePath, String buildOutputPath, Result result, String error) {
        this.filePath = filePath;
        this.buildOutputPath = buildOutputPath;
        this.result = result;
        this.error = error;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getBuildOutputPath() {
        return buildOutputPath;
    }

    public void setBuildOutputPath(String buildOutputPath) {
        this.buildOutputPath = buildOutputPath;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
