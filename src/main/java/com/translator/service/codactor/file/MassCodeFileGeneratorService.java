package com.translator.service.codactor.file;

import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.InquiryChat;

import java.util.List;

public interface MassCodeFileGeneratorService {
    void generateCodeFiles(Inquiry inquiry, InquiryChat inquiryChat, String language, String fileExtension, String filePath);

    void generateCodeFilesWithConsideration(Inquiry inquiry, InquiryChat inquiryChat, String language, String fileExtension, String filePath);

    void generateCodeFiles(String description, String language, String fileExtension, String filePath, List<HistoricalContextObjectHolder> priorContext);

    void generateCodeFilesWithConsideration(String description, String language, String fileExtension, String filePath, List<HistoricalContextObjectHolder> priorContext);
}
