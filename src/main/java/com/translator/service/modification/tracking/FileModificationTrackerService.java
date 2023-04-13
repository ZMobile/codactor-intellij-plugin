package com.translator.service.modification.tracking;

import com.translator.model.modification.*;
import com.translator.service.modification.tracking.listener.DocumentListenerService;
import com.translator.service.modification.tracking.listener.UneditableSegmentListenerService;
import com.translator.service.ui.ModificationQueueListButtonService;
import com.translator.view.viewer.ModificationQueueViewer;

import java.awt.*;
import java.util.List;
import java.util.Map;

public interface FileModificationTrackerService {
    String addModification(String filePath, int startIndex, int endIndex, ModificationType modificationType);

    String addModificationSuggestionModification(String filePath, String suggestionId, int startIndex, int endIndex, ModificationType modificationType);

    String addMultiFileModification(String description, String language, String fileExtension, String filePath);

    void removeModification(String modificationId);

    void removeModificationSuggestionModification(String modificationSuggestionModificationId);

    void removeMultiFileModification(String multiFileModificationId);

    void setMultiFileModificationStage(String multiFileModificationId, String stage);

    void queueModificationUpdate(String modificationId, String modification);

    void queueModificationSuggestionModificationUpdate(FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord);

    void implementQueuedModificationUpdates();

    void implementModificationUpdate(String modificationId, String modification);

    void updateModifications(String filePath, int formerStartIndex, int formerEndIndex, String textInserted);

    void updateModificationSuggestionModifications(String suggestionId, int formerStartIndex, int formerEndIndex, String textInserted);

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

    Color getModificationQueueListButtonColor();

    DocumentListenerService getDocumentListenerService();

    UneditableSegmentListenerService getUneditableSegmentListenerService();

    void setModificationQueueListButtonService(ModificationQueueListButtonService modificationQueueListButtonService);

    void setModificationQueueViewer(ModificationQueueViewer modificationQueueViewer);
}