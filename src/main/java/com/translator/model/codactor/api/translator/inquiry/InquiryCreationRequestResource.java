package com.translator.model.codactor.api.translator.inquiry;


import com.translator.model.codactor.inquiry.function.ChatGptFunction;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.modification.RecordType;

import java.util.List;

public class InquiryCreationRequestResource {

    public static class Builder {
        private String subjectRecordId;
        private RecordType recordType;
        private String filePath;
        private String code;
        private String question;
        private String openAiApiKey;
        private String model;
        private List<ChatGptFunction> functions;
        private List<HistoricalContextObjectHolder> priorContext;
        private String systemMessage;

        public Builder withSubjectRecordId(String subjectRecordId) {
            this.subjectRecordId = subjectRecordId;
            return this;
        }

        public Builder withRecordType(RecordType recordType) {
            this.recordType = recordType;
            return this;
        }

        public Builder withFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withQuestion(String question) {
            this.question = question;
            return this;
        }

        public Builder withOpenAiApiKey(String openAiApiKey) {
            this.openAiApiKey = openAiApiKey;
            return this;
        }

        public Builder withModel(String model) {
            this.model = model;
            return this;
        }

        public Builder withFunctions(List<ChatGptFunction> functions) {
            this.functions = functions;
            return this;
        }

        public Builder withPriorContext(List<HistoricalContextObjectHolder> priorContext) {
            this.priorContext = priorContext;
            return this;
        }

        public Builder withSystemMessage(String systemMessage) {
            this.systemMessage = systemMessage;
            return this;
        }

        public InquiryCreationRequestResource build() {
            return new InquiryCreationRequestResource(subjectRecordId, recordType, filePath, code, question, openAiApiKey, model, functions, priorContext, systemMessage);
        }
    }

    private String subjectRecordId;
    private RecordType recordType;
    private String filePath;
    private String code;
    private String question;
    private String openAiApiKey;
    private String model;
    private List<ChatGptFunction> functions;
    private List<HistoricalContextObjectHolder> priorContext;
    private String systemMessage;

    public InquiryCreationRequestResource(String subjectRecordId,
                                          RecordType recordType,
                                          String filePath,
                                          String code,
                                          String question,
                                          String openAiApiKey,
                                          String model,
                                          List<ChatGptFunction> functions,
                                          List<HistoricalContextObjectHolder> priorContext,
                                          String systemMessage) {
        this.subjectRecordId = subjectRecordId;
        this.recordType = recordType;
        this.filePath = filePath;
        this.code = code;
        this.question = question;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.functions = functions;
        this.priorContext = priorContext;
        this.systemMessage = systemMessage;
    }

    public String getSubjectRecordId() {
        return subjectRecordId;
    }

    public void setSubjectRecordId(String subjectRecordId) {
        this.subjectRecordId = subjectRecordId;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOpenAiApiKey() {
        return openAiApiKey;
    }

    public void setOpenAiApiKey(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<ChatGptFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<ChatGptFunction> functions) {
        this.functions = functions;
    }

    public List<HistoricalContextObjectHolder> getPriorContext() {
        return priorContext;
    }

    public void setPriorContext(List<HistoricalContextObjectHolder> priorContext) {
        this.priorContext = priorContext;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public void setSystemMessage(String systemMessage) {
        this.systemMessage = systemMessage;
    }
}
