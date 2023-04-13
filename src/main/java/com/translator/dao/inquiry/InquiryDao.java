package com.translator.dao.inquiry;

import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.inquiry.Inquiry;
import com.translator.model.modification.RecordType;

import java.util.List;

public interface InquiryDao {
    List<Inquiry> getRecentInquiries();

    Inquiry getInquiry(String inquiryId);

    Inquiry createInquiry(String subjectRecordId, RecordType recordType, String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext);

    Inquiry createInquiry(String filePath, String code, String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext);

    Inquiry createGeneralInquiry(String question, String openAiApiKey, String model);

    Inquiry createGeneralInquiry(String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext);

    Inquiry continueInquiry(String previousInquiryChatId, String question, String openAiApiKey, String model);
}
