package com.translator.service.codactor.ai.modification.tracking.multi;

import com.google.inject.Injector;
import com.translator.CodactorInjector;
import com.translator.model.codactor.ai.modification.MultiFileModification;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;

import java.util.ArrayList;
import java.util.List;

public class MultiFileModificationTrackerServiceImpl implements MultiFileModificationTrackerService {

    private final List<MultiFileModification> activeMultiFileModifications;
    private final List<MultiFileModificationListener> multiFileModificationUpdateListeners;

    public MultiFileModificationTrackerServiceImpl() {
        this.activeMultiFileModifications = new ArrayList<>();
        this.multiFileModificationUpdateListeners = new ArrayList<>();
    }

    public String addMultiFileModification(String description, String language, String fileExtension, String filePath) {
        MultiFileModification multiFileModification = new MultiFileModification(description, language, fileExtension, filePath);
        activeMultiFileModifications.add(multiFileModification);
        for (MultiFileModificationListener listener : multiFileModificationUpdateListeners) {
            listener.onMultiFileModificationUpdate(multiFileModification);
        }
        if (modificationQueueViewer == null) {
            Injector injector = CodactorInjector.getInstance().getInjector(project);
            this.modificationQueueViewer = injector.getInstance(ModificationQueueViewer.class);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        return multiFileModification.getId();
    }

    public String addMultiFileModification(String description) {
        MultiFileModification multiFileModification = new MultiFileModification(description);
        activeMultiFileModifications.add(multiFileModification);
        for (MultiFileModificationListener listener : multiFileModificationUpdateListeners) {
            listener.onMultiFileModificationUpdate(multiFileModification);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        return multiFileModification.getId();
    }

    public void removeMultiFileModification(String multiFileModificationId) {
        MultiFileModification multiFileModification = activeMultiFileModifications.stream()
                .filter(m -> m.getId().equals(multiFileModificationId))
                .findFirst()
                .orElseThrow();
        activeMultiFileModifications.remove(multiFileModification);
        for (MultiFileModificationListener listener : multiFileModificationUpdateListeners) {
            listener.onMultiFileModificationUpdate(multiFileModification);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
    }

    public void setMultiFileModificationStage(String multiFileModificationId, String stage) {
        MultiFileModification multiFileModification = activeMultiFileModifications.stream()
                .filter(m -> m.getId().equals(multiFileModificationId))
                .findFirst()
                .orElseThrow();
        multiFileModification.setStage(stage);
        for (MultiFileModificationListener listener : multiFileModificationUpdateListeners) {
            listener.onMultiFileModificationUpdate(multiFileModification);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
    }

    public List<MultiFileModification> getActiveMultiFileModifications() {
        return activeMultiFileModifications;
    }

    @Override
    public void addMultiFileModificationListener(MultiFileModificationListener listener) {
        multiFileModificationUpdateListeners.add(listener);
    }
}
