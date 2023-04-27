package com.translator.service.file;

import com.translator.model.history.data.HistoricalContextObjectDataHolder;
import com.translator.model.inquiry.Inquiry;
import com.translator.model.inquiry.InquiryChat;

import java.util.List;

public interface CodeFileGeneratorService {
    void generateCodeFiles(Inquiry inquiry, InquiryChat inquiryChat, String language, String fileExtension, String filePath);

    void generateCodeFilesWithConsideration(Inquiry inquiry, InquiryChat inquiryChat, String language, String fileExtension, String filePath);

    void generateCodeFiles(String description, String language, String fileExtension, String filePath, List<HistoricalContextObjectDataHolder> priorContextData);

    void generateCodeFilesWithConsideration(String description, String language, String fileExtension, String filePath, List<HistoricalContextObjectDataHolder> priorContextData);
}
