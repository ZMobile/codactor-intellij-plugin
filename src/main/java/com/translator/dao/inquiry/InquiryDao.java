package com.translator.dao.inquiry;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.function.GptFunction;
import com.translator.model.codactor.ai.modification.RecordType;

import java.util.List;

public interface InquiryDao {
    List<Inquiry> getRecentInquiries();

    Inquiry getInquiry(String inquiryId);

    Inquiry createInquiry(String subjectRecordId, RecordType recordType, String question, String openAiApiKey, String model, boolean azure, String azureResource, String azureDeployment, List<HistoricalContextObjectHolder> priorContext, String systemMessage);

    Inquiry createInquiry(String subjectRecordId, RecordType recordType, String question, String openAiApiKey, String model, boolean azure, String azureResource, String azureDeployment, List<HistoricalContextObjectHolder> priorContext, List<GptFunction> functions, String systemMessage);

    Inquiry createInquiry(String filePath, String code, String question, String openAiApiKey, String model, boolean azure, String azureResource, String azureDeployment, List<HistoricalContextObjectHolder> priorContext, String systemMessage);

    Inquiry createInquiry(String filePath, String code, String question, String openAiApiKey, String model, boolean azure, String azureResource, String azureDeployment, List<HistoricalContextObjectHolder> priorContext, List<GptFunction> functions, String systemMessage);

    Inquiry createGeneralInquiry(String question, String openAiApiKey, String model, boolean azure, String azureResource, String azureDeployment, String systemMessage);

    Inquiry createGeneralInquiry(String question, String openAiApiKey, String model, boolean azure, String azureResource, String azureDeployment, List<HistoricalContextObjectHolder> priorContext, String systemMessage);

    Inquiry createGeneralInquiry(String question, String openAiApiKey, String model, boolean azure, String azureResource, String azureDeployment, List<HistoricalContextObjectHolder> priorContext, List<GptFunction> functions, String systemMessage);

    Inquiry continueInquiry(String previousInquiryChatId, String question, String openAiApiKey, String model, boolean azure,  String azureResource, String azureDeployment);

    Inquiry continueInquiry(String previousInquiryChatId, String question, String openAiApiKey, String model, boolean azure,  String azureResource, String azureDeployment,List<GptFunction> functions);

    Inquiry respondToFunctionCall(String previousInquiryChatId, String functionName, String content, String openAiApiKey, String model, boolean azure,  String azureResource, String azureDeployment,List<GptFunction> functions);
}
