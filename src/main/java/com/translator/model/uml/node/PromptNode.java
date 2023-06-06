package com.translator.model.uml.node;

import com.translator.model.inquiry.Inquiry;
import com.translator.model.uml.prompt.Prompt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PromptNode extends Node {
    private List<Prompt> promptList;
    private List<Inquiry> activeInquiryList;
    private int outputCount;
    private boolean running;
    private boolean error;
    private String aiModel;

    public PromptNode() {
        super();
        this.promptList = new ArrayList<>();
        this.activeInquiryList = new ArrayList<>();
        this.outputCount = 1;
        this.running = false;
        this.error = false;
        this.aiModel = "gpt-3.5-turbo";
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

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
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

    public String getAiModel() {
        return aiModel;
    }

    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }
}
