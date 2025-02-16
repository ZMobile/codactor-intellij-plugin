package com.translator.model.codactor.ai.chat.function.directive.test;

public class TestClassInfoResource {

    public static class Builder {
        private String className;
        private String path;
        private String buildOutputPath;

        public Builder withClassName(String className) {
            this.className = className;
            return this;
        }

        public Builder withPath(String path) {
            this.path = path;
            return this;
        }

        public Builder withBuildOutputPath(String buildOutputPath) {
            this.buildOutputPath = buildOutputPath;
            return this;
        }

        public TestClassInfoResource build() {
            return new TestClassInfoResource(className, path, buildOutputPath);
        }
    }

    private String className;
    private String path;
    private String buildOutputPath;

    public TestClassInfoResource(String className, String path, String buildOutputPath) {
        this.className = className;
        this.path = path;
        this.buildOutputPath = buildOutputPath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBuildOutputPath() {
        return buildOutputPath;
    }

    public void setBuildOutputPath(String buildOutputPath) {
        this.buildOutputPath = buildOutputPath;
    }
}
