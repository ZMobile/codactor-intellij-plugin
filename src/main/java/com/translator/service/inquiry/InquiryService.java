package com.translator.service.inquiry;

import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.modification.RecordType;

import java.util.List;

public interface InquiryService {
    void createInquiry(String subjectRecordId, RecordType recordType, String question, String filePath);

    void createInquiry(String filePath, String code, String question, List<HistoricalContextObjectHolder> priorContext);

    void createGeneralInquiry(String question);

    void continueInquiry(String previousInquiryChatId, String question);
}
