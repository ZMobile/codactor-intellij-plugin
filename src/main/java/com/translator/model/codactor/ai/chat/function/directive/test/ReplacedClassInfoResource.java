package com.translator.model.codactor.ai.chat.function.directive.test;

import org.junit.runner.Result;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReplacedClassInfoResource {
    public static class Builder {
        private String filePath;
        private String oldCode;
        private String newCode;
        private Map<String, Result> formerResults;

        public Builder withFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder withOldCode(String oldCode) {
            this.oldCode = oldCode;
            return this;
        }

        public Builder withNewCode(String newCode) {
            this.newCode = newCode;
            return this;
        }


        public Builder withFormerResults(Map<String, Result> formerResults) {
            /*if (formerResults.isEmpty()) {
                this.formerResults = formerResults;
                return this;
            }

            // Use LinkedHashMap to maintain order
            LinkedHashMap<String, Result> reordered = new LinkedHashMap<>();

            // Store the first entry separately
            Iterator<Map.Entry<String, Result>> iterator = formerResults.entrySet().iterator();
            Map.Entry<String, Result> firstEntry = iterator.next();

            // Add the remaining entries
            while (iterator.hasNext()) {
                Map.Entry<String, Result> entry = iterator.next();
                reordered.put(entry.getKey(), entry.getValue());
            }

            // Add the first entry to the end
            reordered.put(firstEntry.getKey(), firstEntry.getValue());

            // Assign to the field*/
            this.formerResults = formerResults;

            return this;
        }

        public ReplacedClassInfoResource build() {
            return new ReplacedClassInfoResource(filePath, oldCode, newCode, formerResults);
        }
    }

    private String filePath;
    private String oldCode;
    private String newCode;
    private Map<String, Result> formerResults;

    public ReplacedClassInfoResource(String filePath, String oldCode, String newCode, Map<String, Result> formerResults) {
        this.filePath = filePath;
        this.oldCode = oldCode;
        this.newCode = newCode;
        this.formerResults = formerResults;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOldCode() {
        return oldCode;
    }

    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    public String getNewCode() {
        return newCode;
    }

    public void setNewCode(String newCode) {
        this.newCode = newCode;
    }

    public Map<String, Result> getFormerResults() {
        return formerResults;
    }

    public void setFormerResults(Map<String, Result> formerResults) {
        this.formerResults = formerResults;
    }
}
