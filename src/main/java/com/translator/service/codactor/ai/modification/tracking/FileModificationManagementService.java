package com.translator.service.codactor.ai.modification.tracking;

import com.intellij.openapi.editor.Editor;
import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.*;
import com.translator.model.codactor.ai.modification.data.FileModificationDataHolder;
import com.translator.view.codactor.dialog.modification.ProvisionalModificationCustomizerDialog;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;

import java.util.List;
import java.util.Map;

public interface FileModificationManagementService {
    String addModification(String filePath, String modification, int startIndex, int endIndex, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    String addModificationSuggestionModification(Editor editor, String filePath, String suggestionId, int startIndex, int endIndex, ModificationType modificationType);

    String addMultiFileModification(String description, String language, String fileExtension, String filePath);

    String addMultiFileModification(String description);

    void removeModification(String modificationId);

    void removeModificationSuggestionModification(String modificationSuggestionModificationId);

    void removeMultiFileModification(String multiFileModificationId);

    void setMultiFileModificationStage(String multiFileModificationId, String stage);

    void implementModificationUpdate(String modificationId, String modification, boolean silent);

    void implementModificationSuggestionModificationUpdate(FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord);

    void readyFileModificationUpdate(String modificationId, String subjectLine, List<FileModificationSuggestionRecord> modificationOptions);

    void undoReadyFileModification(String modificationId);

    FileModificationSuggestion getModificationSuggestion(String suggestionId);


    FileModificationSuggestionModification getModificationSuggestionModification(String modificationSuggestionModificationId);


    List<FileModificationSuggestionModification> getAllFileModificationSuggestionModifications();

    //List<FileModification> getAllFileModificationsAndModificationSuggestionModifications();

    void setModificationQueueViewer(ModificationQueueViewer modificationQueueViewer);


    void errorFileModification(String modificationId);

    void retryFileModification(String modificationId);
}