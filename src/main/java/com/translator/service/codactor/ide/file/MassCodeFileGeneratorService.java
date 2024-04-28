package com.translator.service.codactor.ide.file;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;

import java.util.List;

public interface MassCodeFileGeneratorService {
    void generateCodeFiles(Inquiry inquiry, InquiryChat inquiryChat, String language, String fileExtension, String filePath);

    void generateCodeFilesWithConsideration(Inquiry inquiry, InquiryChat inquiryChat, String language, String fileExtension, String filePath);

    void generateCodeFiles(String description, String language, String fileExtension, String filePath, List<HistoricalContextObjectHolder> priorContext);

    void generateCodeFilesWithConsideration(String description, String language, String fileExtension, String filePath, List<HistoricalContextObjectHolder> priorContext);
}
