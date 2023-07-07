package com.translator.dao.inquiry;

import com.translator.model.codactor.api.translator.inquiry.function.ChatGptFunction;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.modification.RecordType;

import java.util.List;

public interface InquiryDao {
    List<Inquiry> getRecentInquiries();

    Inquiry getInquiry(String inquiryId);

    Inquiry createInquiry(String subjectRecordId, RecordType recordType, String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext);

    Inquiry createInquiry(String subjectRecordId, RecordType recordType, String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext, List<ChatGptFunction> functions);

    Inquiry createInquiry(String filePath, String code, String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext);

    Inquiry createInquiry(String filePath, String code, String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext, List<ChatGptFunction> functions);

    Inquiry createGeneralInquiry(String question, String openAiApiKey, String model);

    Inquiry createGeneralInquiry(String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext);

    Inquiry createGeneralInquiry(String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext, List<ChatGptFunction> functions);

    Inquiry continueInquiry(String previousInquiryChatId, String question, String openAiApiKey, String model);

    Inquiry continueInquiry(String previousInquiryChatId, String question, String openAiApiKey, String model, List<ChatGptFunction> functions);

    Inquiry respondToFunctionCall(String previousInquiryChatId, String functionName, String content, String openAiApiKey, String model, List<ChatGptFunction> functions);
}
