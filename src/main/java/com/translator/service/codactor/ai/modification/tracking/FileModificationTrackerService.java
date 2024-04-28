package com.translator.service.codactor.ai.modification.tracking;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionRecord;
import com.translator.model.codactor.ai.modification.FileModificationTracker;
import com.translator.model.codactor.ai.modification.ModificationType;

import java.util.List;
import java.util.Map;

public interface FileModificationTrackerService {
        String addModification(String filePath, String modification, int startIndex, int endIndex, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

        void removeModification(String modificationId);

        void implementModification(String modificationId, String modification, boolean silent);

        void readyFileModificationUpdate(String modificationId, String subjectLine, List<FileModificationSuggestionRecord> modificationOptions);

        void undoReadyFileModification(String modificationId);

        Map<String, FileModificationTracker> getActiveModificationFiles();

        FileModification getModification(String modificationId);

        List<FileModification> getAllFileModifications();

        void errorFileModification(String modificationId);

        void addModificationUpdateListener(FileModificationTrackerServiceImpl.FileModificationListener listener);

        void addModificationImplementedListener(FileModificationTrackerServiceImpl.FileModificationListener listener);

        void addModificationErrorListener(FileModificationTrackerServiceImpl.FileModificationListener listener);

        void addModificationAddedListener(FileModificationTrackerServiceImpl.FileModificationListener listener);
        void addModificationRemovedListener(FileModificationTrackerServiceImpl.FileModificationListener listener);

        void addModificationReadyListener(FileModificationTrackerServiceImpl.FileModificationListener listener);
}
