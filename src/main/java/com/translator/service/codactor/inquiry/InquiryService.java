package com.translator.service.codactor.inquiry;

import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.modification.RecordType;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;

import java.util.List;

public interface InquiryService {
    void createInquiry(InquiryViewer inquiryViewer, String subjectRecordId, RecordType recordType, String question, String filePath, String model);

    void createInquiry(InquiryViewer inquiryViewer, String filePath, String code, String question, List<HistoricalContextObjectHolder> priorContext, String model);

    void createGeneralInquiry(InquiryViewer inquiryViewer, String question, String model);

    void continueInquiry(InquiryViewer inquiryViewer, String previousInquiryChatId, String question, String model);
}
