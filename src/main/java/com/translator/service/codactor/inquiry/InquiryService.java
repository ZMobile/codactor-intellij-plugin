package com.translator.service.codactor.inquiry;

import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.modification.RecordType;

import java.util.List;

public interface InquiryService {
    void createInquiry(String subjectRecordId, RecordType recordType, String question, String filePath, String model);

    void createInquiry(String filePath, String code, String question, List<HistoricalContextObjectHolder> priorContext, String model);

    void createGeneralInquiry(String question, String model);

    void continueInquiry(String previousInquiryChatId, String question, String model);
}
