package com.translator.service.codactor.ai.modification.tracking.multi;

import com.google.inject.Injector;
import com.translator.CodactorInjector;
import com.translator.model.codactor.ai.modification.MultiFileModification;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;

import java.util.List;

public interface MultiFileModificationTrackerService {
    interface MultiFileModificationListener {
        void onMultiFileModificationUpdate(MultiFileModification multiFileModification);
    }

    String addMultiFileModification(String description, String language, String fileExtension, String filePath);

    String addMultiFileModification(String description);

    void removeMultiFileModification(String multiFileModificationId);

    void setMultiFileModificationStage(String multiFileModificationId, String stage);

    List<MultiFileModification> getActiveMultiFileModifications();

    void addMultiFileModificationListener(MultiFileModificationListener listener);
}
