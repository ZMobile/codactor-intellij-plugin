package com.translator.model.uml.node;

import com.translator.model.uml.prompt.Prompt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PromptNode extends Node {
    private List<Prompt> promptList;
    private int outputCountMultiplier;

    public PromptNode() {
        super();
        this.promptList = new ArrayList<>();
        this.outputCountMultiplier = 1;
    }

    public List<Prompt> getPromptList() {
        return promptList;
    }

    public void setPromptList(List<Prompt> promptList) {
        this.promptList = promptList;
    }

    public int getOutputCountMultiplier() {
        return outputCountMultiplier;
    }

    public void setOutputCountMultiplier(int outputCountMultiplier) {
        this.outputCountMultiplier = outputCountMultiplier;
    }
}
