package com.translator.service.codactor.modification.tracking;

import com.translator.model.codactor.modification.data.FileModificationDataHolder;
import com.translator.view.codactor.dialog.ProvisionalModificationCustomizerDialog;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.modification.*;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;

import java.util.List;
import java.util.Map;

public interface FileModificationTrackerService {
    String addModification(String filePath, String modification, int startIndex, int endIndex, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    String addModificationSuggestionModification(String filePath, String suggestionId, int startIndex, int endIndex, ModificationType modificationType);

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

    Map<String, FileModificationTracker> getActiveModificationFiles();

    Map<String, FileModificationSuggestionModificationTracker> getActiveModificationSuggestionModifications();

    FileModification getModification(String modificationId);

    FileModificationTracker getModificationTracker(String filePath);


    FileModificationSuggestion getModificationSuggestion(String suggestionId);

    FileModificationSuggestionModificationTracker getModificationSuggestionModificationTracker(String suggestionId);

    FileModificationSuggestionModification getModificationSuggestionModification(String modificationSuggestionModificationId);

    List<FileModification> getAllFileModifications();

    List<FileModificationSuggestionModification> getAllFileModificationSuggestionModifications();

    //List<FileModification> getAllFileModificationsAndModificationSuggestionModifications();

    List<FileModificationDataHolder> getQueuedFileModificationObjectHolders();

    void setModificationQueueViewer(ModificationQueueViewer modificationQueueViewer);

    void addProvisionalModificationCustomizer(ProvisionalModificationCustomizerDialog provisionalModificationCustomizerDialog);

    void errorFileModification(String modificationId);

    void retryFileModification(String modificationId);
}