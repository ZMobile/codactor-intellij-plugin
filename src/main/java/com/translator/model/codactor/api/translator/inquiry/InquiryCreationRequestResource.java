package com.translator.model.codactor.api.translator.inquiry;


import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.modification.RecordType;

import java.util.List;

public class InquiryCreationRequestResource {
    private String subjectRecordId;
    private RecordType recordType;
    private String filePath;
    private String code;
    private String question;
    private String openAiApiKey;
    private String model;
    private List<HistoricalContextObjectHolder> priorContext;

    public InquiryCreationRequestResource(String subjectRecordId, RecordType recordType, String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext) {
        this.subjectRecordId = subjectRecordId;
        this.recordType = recordType;
        this.question = question;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.priorContext = priorContext;
    }

    public InquiryCreationRequestResource(String filePath, String code, String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext) {
        this.filePath = filePath;
        this.code = code;
        this.question = question;
        this.openAiApiKey = openAiApiKey;
        this.model = model;
        this.priorContext = priorContext;
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

    public List<HistoricalContextObjectHolder> getPriorContext() {
        return priorContext;
    }

    public void setPriorContext(List<HistoricalContextObjectHolder> priorContext) {
        this.priorContext = priorContext;
    }
}
