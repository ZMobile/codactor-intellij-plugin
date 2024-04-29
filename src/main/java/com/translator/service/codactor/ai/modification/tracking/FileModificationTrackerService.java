package com.translator.service.codactor.ai.modification.tracking;

import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionRecord;
import com.translator.model.codactor.ai.modification.FileModificationTracker;
import com.translator.model.codactor.ai.modification.ModificationType;

import java.util.List;
import java.util.Map;

public interface FileModificationTrackerService {
        interface FileModificationListener {
                void onModificationUpdate(FileModification fileModification);
        }

        String addModification(String filePath, String modification, int startIndex, int endIndex, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

        void removeModification(String modificationId);

        void implementModification(String modificationId, String modification, boolean silent);

        void readyFileModificationUpdate(String modificationId, String subjectLine, List<FileModificationSuggestionRecord> modificationOptions);

        void undoReadyFileModification(String modificationId);

        Map<String, FileModificationTracker> getActiveModificationFiles();

        FileModificationTracker getModificationTracker(String filePath);

        FileModificationTracker getTrackerWithModificationId(String modificationId);

        FileModification getModification(String modificationId);

        List<FileModification> getAllFileModifications();

        void errorFileModification(String modificationId);

        void retryFileModification(String modificationId);

        void addModificationUpdateListener(FileModificationListener listener);

        void addModificationImplementedListener(FileModificationListener listener);

        void addModificationErrorListener(FileModificationListener listener);

        void addModificationAddedListener(FileModificationListener listener);
        void addModificationRemovedListener(FileModificationListener listener);

        void addModificationReadyListener(FileModificationListener listener);
}
