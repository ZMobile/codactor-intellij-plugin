package com.translator.model.ai;

public class LikelihoodResponse {
    private double likelihoodPercentage;
    private String reasoning;

    public double getLikelihoodPercentage() {
        return likelihoodPercentage;
    }

    public String getReasoning() {
        return reasoning;
    }
}
