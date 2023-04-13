package com.translator.model.api.translator.inquiry;

import com.translator.model.history.HistoricalContextObjectHolder;

import java.util.List;

public class GeneralInquiryCreationRequestResource {
    private String question;
    private String openAiApiKey;
    private String model;
    private List<HistoricalContextObjectHolder> priorContext;

    public GeneralInquiryCreationRequestResource(String question, String openAiApiKey, String model) {
        this.question = question;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.priorContext = null;
    }

    public GeneralInquiryCreationRequestResource(String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext) {
        this.question = question;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.priorContext = priorContext;
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
}
