package com.translator.model.uml.draw.node;

import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.uml.prompt.Prompt;

import java.util.ArrayList;
import java.util.List;

public class PromptNode extends Node {
    private List<Prompt> promptList;
    private List<Inquiry> activeInquiryList;
    private int outputCount;
    private boolean error;
    private String model;

    public PromptNode() {
        super();
        this.promptList = new ArrayList<>();
        this.activeInquiryList = new ArrayList<>();
        this.outputCount = 1;
        this.error = false;
        this.model = "gpt-3.5-turbo";
    }

    public List<Prompt> getPromptList() {
        return promptList;
    }

    public void setPromptList(List<Prompt> promptList) {
        this.promptList = promptList;
    }

    public List<Inquiry> getActiveInquiryList() {
        return activeInquiryList;
    }

    public void setActiveInquiryList(List<Inquiry> activeInquiryList) {
        this.activeInquiryList = activeInquiryList;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public void setOutputCount(int outputCountMultiplier) {
        this.outputCount = outputCountMultiplier;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String aiModel) {
        this.model = aiModel;
    }
}
