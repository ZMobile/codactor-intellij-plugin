package com.translator.service.codactor.ai.chat.inquiry;

import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.RecordType;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;

import java.util.List;

public interface InquiryService {
    Inquiry createHeadlessInquiry(String question, String model, boolean functionsEnabled);

    InquiryViewer createInquiry(InquiryViewer inquiryViewer, String subjectRecordId, RecordType recordType, String question, String filePath, String model);

    InquiryViewer createInquiry(InquiryViewer inquiryViewer, String filePath, String code, String question, List<HistoricalContextObjectHolder> priorContext, String model);

    InquiryViewer createGeneralInquiry(InquiryViewer inquiryViewer, String question, String model);

    void continueInquiry(InquiryViewer inquiryViewer, String previousInquiryChatId, String question, String model);

    InquiryChat continueHeadlessInquiry(Inquiry inquiry, String previousInquiryChatId, String question, String model, boolean functionsEnabled);
}
