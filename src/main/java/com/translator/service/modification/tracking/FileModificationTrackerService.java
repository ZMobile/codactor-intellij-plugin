package com.translator.service.modification.tracking;

import com.translator.view.dialog.ProvisionalModificationCustomizer;
import com.translator.model.modification.*;
import com.translator.view.viewer.ModificationQueueViewer;

import java.util.List;
import java.util.Map;

public interface FileModificationTrackerService {
    String addModification(String filePath, int startIndex, int endIndex, ModificationType modificationType);

    String addModificationSuggestionModification(String filePath, String suggestionId, int startIndex, int endIndex, ModificationType modificationType);

    String addMultiFileModification(String description, String language, String fileExtension, String filePath);

    String addMultiFileModification(String description);

    void removeModification(String modificationId);

    void removeModificationSuggestionModification(String modificationSuggestionModificationId);

    void removeMultiFileModification(String multiFileModificationId);

    void setMultiFileModificationStage(String multiFileModificationId, String stage);

    void implementModificationUpdate(String modificationId, String modification, boolean silent);

    void implementModificationSuggestionModificationUpdate(FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord);

    void readyFileModificationUpdate(String modificationId, List<FileModificationSuggestionRecord> modificationOptions);

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

    List<QueuedFileModificationObjectHolder> getQueuedFileModificationObjectHolders();

    void setModificationQueueViewer(ModificationQueueViewer modificationQueueViewer);

    void addProvisionalModificationCustomizer(ProvisionalModificationCustomizer provisionalModificationCustomizer);

    void errorFileModification(String modificationId);

    void retryFileModification(String modificationId);
}