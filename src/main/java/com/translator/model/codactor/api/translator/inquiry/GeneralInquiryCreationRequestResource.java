package com.translator.model.codactor.api.translator.inquiry;

import com.translator.model.codactor.inquiry.function.ChatGptFunction;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;

import java.util.List;

public class GeneralInquiryCreationRequestResource {

    public static class Builder {
        private String question;
        private String openAiApiKey;
        private String model;
        private List<HistoricalContextObjectHolder> priorContext;
        private List<ChatGptFunction> functions;
        private String systemMessage;

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

        public Builder withPriorContext(List<HistoricalContextObjectHolder> priorContext) {
            this.priorContext = priorContext;
            return this;
        }

        public Builder withFunctions(List<ChatGptFunction> functions) {
            this.functions = functions;
            return this;
        }

        public Builder withSystemMessage(String systemMessage) {
            this.systemMessage = systemMessage;
            return this;
        }

        public GeneralInquiryCreationRequestResource build() {
            return new GeneralInquiryCreationRequestResource(question, openAiApiKey, model, priorContext, functions, systemMessage);
        }
    }

    private String question;
    private String openAiApiKey;
    private String model;
    private List<HistoricalContextObjectHolder> priorContext;
    private List<ChatGptFunction> functions;
    private String systemMessage;

    public GeneralInquiryCreationRequestResource(String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext, List<ChatGptFunction> functions, String systemMessage) {
        this.question = question;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.priorContext = priorContext;
        this.functions = functions;
        this.systemMessage = systemMessage;
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

    public List<HistoricalContextObjectHolder> getPriorContext() {
        return priorContext;
    }

    public void setPriorContext(List<HistoricalContextObjectHolder> priorContext) {
        this.priorContext = priorContext;
    }

    public List<ChatGptFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<ChatGptFunction> functions) {
        this.functions = functions;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public void setSystemMessage(String systemMessage) {
        this.systemMessage = systemMessage;
    }
}
