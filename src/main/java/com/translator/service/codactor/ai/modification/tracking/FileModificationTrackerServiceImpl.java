package com.translator.service.codactor.ai.modification.tracking;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.*;
import com.translator.service.codactor.ai.modification.tracking.suggestion.modification.FileModificationSuggestionModificationTrackerService;
import com.translator.service.codactor.ide.editor.CodeHighlighterService;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.ide.editor.GuardedBlockService;
import com.translator.service.codactor.ide.handler.EditorClickHandlerService;

import javax.inject.Inject;
import javax.swing.*;
import java.util.*;

public class FileModificationTrackerServiceImpl implements FileModificationTrackerService {
    private final Project project;
    private final Map<String, FileModificationTracker> activeModificationFiles;
    private final FileModificationService fileModificationService;
    private final FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService;
    private final EditorClickHandlerService editorClickHandlerService;
    private final List<FileModificationListener> modificationUpdateListeners;
    private final List<FileModificationListener> modificationAddedListeners;
    private final List<FileModificationListener> modificationRemovedListeners;
    private final List<FileModificationListener> modificationReadyListeners;
    private final List<FileModificationListener> modificationImplementedListeners;
    private final List<FileModificationListener> modificationErrorListeners;

    @Inject
    public FileModificationTrackerServiceImpl(Project project,
                                              FileModificationService fileModificationService,
                                              FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService,
                                              EditorClickHandlerService editorClickHandlerService) {
        this.project = project;
        this.activeModificationFiles = new HashMap<>();
        this.fileModificationService = fileModificationService;
        this.fileModificationSuggestionModificationTrackerService = fileModificationSuggestionModificationTrackerService;
        this.editorClickHandlerService = editorClickHandlerService;
        this.modificationUpdateListeners = new ArrayList<>();
        this.modificationAddedListeners = new ArrayList<>();
        this.modificationRemovedListeners = new ArrayList<>();
        this.modificationReadyListeners = new ArrayList<>();
        this.modificationImplementedListeners = new ArrayList<>();
        this.modificationErrorListeners = new ArrayList<>();
    }

    public String addModification(String filePath, String modification, int startIndex, int endIndex, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        String newFilePath = Objects.requireNonNullElse(filePath, "Untitled");
        FileModificationTracker fileModificationTracker;
        if (activeModificationFiles.containsKey(newFilePath)) {
            fileModificationTracker = activeModificationFiles.get(newFilePath);
        } else {
            fileModificationTracker = new FileModificationTracker(project, newFilePath);
            activeModificationFiles.put(newFilePath, fileModificationTracker);
            editorClickHandlerService.addEditorClickHandler(fileModificationTracker);
        }
        for (FileModification m : fileModificationTracker.getModifications()) {
            RangeMarker existingRangeMarker = m.getRangeMarker();
            if (existingRangeMarker != null && existingRangeMarker.isValid()) {
                if ((startIndex <= existingRangeMarker.getStartOffset() && endIndex >= existingRangeMarker.getStartOffset()) || (startIndex <= existingRangeMarker.getEndOffset() && endIndex >= existingRangeMarker.getEndOffset())) {
                    JOptionPane.showMessageDialog(null, "Can't modify code that is already being modified", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return "Error: Can't modify code that is already being modified. Your modification boundaries clash with modification id: " + m.getId() + " with code snippet: \n" + m.getBeforeText() + "\n";
                }
            }
        }
        FileModification fileModification = fileModificationService.addModification(fileModificationTracker.getFilePath(), modification, startIndex, endIndex, modificationType, priorContext);
        fileModificationTracker.getModifications().add(fileModification);
        for (FileModificationListener listener : modificationUpdateListeners) {
            listener.onModificationUpdate(fileModification);
        }
        for (FileModificationListener listener : modificationAddedListeners) {
            listener.onModificationUpdate(fileModification);
        }
        return fileModification.getId();
    }

    @Override
    public void implementModification(String modificationId, String modification, boolean silent) {
        FileModificationTracker fileModificationTracker = getTrackerWithModificationId(modificationId);
        if (fileModificationTracker == null) {
            return;
        }
        FileModification fileModification = getModification(fileModificationTracker, modificationId);
        fileModificationService.implementModification(fileModification, modification, silent);
        for (FileModificationListener listener : modificationUpdateListeners) {
            listener.onModificationUpdate(fileModification);
        }
        for (FileModificationListener listener : modificationImplementedListeners) {
            listener.onModificationUpdate(fileModification);
        }
        removeModification(modificationId);
    }

    public void removeModification(String modificationId) {
        FileModificationTracker fileModificationTracker = getTrackerWithModificationId(modificationId);
        FileModification fileModification = getModification(fileModificationTracker, modificationId);
        if (fileModification == null) {
            return;
        }
        for (FileModificationSuggestion fileModificationSuggestion : fileModification.getModificationOptions()) {
            ApplicationManager.getApplication().invokeLater(fileModificationSuggestion::dispose);
            FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = fileModificationSuggestionModificationTrackerService.getModificationSuggestionModificationTracker(fileModificationSuggestion.getId());
            if (fileModificationSuggestionModificationTracker == null) {
                continue;
            }
            for (FileModificationSuggestionModification fileModificationSuggestionModification : fileModificationSuggestionModificationTracker.getModifications()) {
                fileModificationSuggestionModificationTrackerService.removeModificationSuggestionModification(fileModificationSuggestionModification.getId());
            }
        }
        fileModificationTracker.getModifications().remove(fileModification);
        fileModificationService.removeModification(fileModification);
        if (fileModificationTracker.getModifications().isEmpty()) {
            activeModificationFiles.values().remove(fileModificationTracker);
            editorClickHandlerService.removeEditorClickHandler(fileModificationTracker.getFilePath());
        }
        for (FileModificationListener listener : modificationUpdateListeners) {
            listener.onModificationUpdate(fileModification);
        }
        for (FileModificationListener listener : modificationRemovedListeners) {
            listener.onModificationUpdate(fileModification);
        }
    }

    @Override
    public void readyFileModificationUpdate(String modificationId, String subjectLine, List<FileModificationSuggestionRecord> modificationOptions) {
        FileModification fileModification = getModification(modificationId);
        fileModificationService.readyFileModificationUpdate(fileModification, subjectLine, modificationOptions);
        for (FileModificationListener listener : modificationUpdateListeners) {
            listener.onModificationUpdate(fileModification);
        }
        for (FileModificationListener listener : modificationReadyListeners) {
            listener.onModificationUpdate(fileModification);
        }
    }

    @Override
    public void undoReadyFileModification(String modificationId) {
        FileModification fileModification = getModification(modificationId);
        fileModificationService.undoReadyFileModification(fileModification);
        for (FileModificationListener listener : modificationUpdateListeners) {
            listener.onModificationUpdate(fileModification);
        }
    }

    public boolean hasModification(FileModificationTracker fileModificationTracker, String modificationId) {
        return fileModificationTracker.getModifications().stream()
                .anyMatch(m -> m.getId().equals(modificationId));
    }

    public FileModification getModification(FileModificationTracker fileModificationTracker, String modificationId) {
        for (FileModification m : fileModificationTracker.getModifications()) {
            if (m.getId().equals(modificationId)) {
                return m;
            }
        }
        return null;
    }

    @Override
    public FileModificationTracker getModificationTracker(String filePath) {
        return activeModificationFiles.get(filePath);
    }

    @Override
    public FileModificationTracker getTrackerWithModificationId(String modificationId) {
        return activeModificationFiles.values().stream()
                .filter(m -> hasModification(m, modificationId))
                .findFirst()
                .orElse(null);
    }

    public Map<String, FileModificationTracker> getActiveModificationFiles() {
        return activeModificationFiles;
    }

    private FileModification getModification(String filePath, String modificationId) {
        FileModificationTracker fileModificationTracker = activeModificationFiles.get(filePath);
        if (fileModificationTracker == null) {
            return null;
        }
        return getModification(fileModificationTracker, modificationId);
    }

    @Override
    public FileModification getModification(String modificationId) {
        FileModificationTracker fileModificationTracker = getTrackerWithModificationId(modificationId);
        if (fileModificationTracker == null) {
            return null;
        }
        return getModification(fileModificationTracker, modificationId);
    }

    @Override
    public List<FileModification> getAllFileModifications() {
        List<FileModification> fileModifications = new ArrayList<>();
        for (FileModificationTracker fileModificationTracker : activeModificationFiles.values()) {
            fileModifications.addAll(fileModificationTracker.getModifications());
        }
        return fileModifications;
    }

    @Override
    public void errorFileModification(String modificationId) {
        FileModification fileModification = getModification(modificationId);
        fileModificationService.errorFileModification(fileModification);
        for (FileModificationListener listener : modificationUpdateListeners) {
            listener.onModificationUpdate(fileModification);
        }
        for (FileModificationListener listener : modificationErrorListeners) {
            listener.onModificationUpdate(fileModification);
        }
    }

    @Override
    public void retryFileModification(String modificationId) {
        FileModification fileModification = getModification(modificationId);
        if (fileModification == null) {
            return;
        }
        fileModificationService.retryFileModification(fileModification);
        for (FileModificationListener listener : modificationUpdateListeners) {
            listener.onModificationUpdate(fileModification);
        }
    }

        @Override
    public void addModificationUpdateListener(FileModificationListener listener) {
        modificationUpdateListeners.add(listener);
    }

    @Override
    public void addModificationImplementedListener(FileModificationListener listener) {
        modificationImplementedListeners.add(listener);
    }

    @Override
    public void addModificationReadyListener(FileModificationListener listener) {
        modificationReadyListeners.add(listener);
    }

    @Override
    public void addModificationErrorListener(FileModificationListener listener) {
        modificationErrorListeners.add(listener);
    }

    @Override
    public void addModificationAddedListener(FileModificationListener listener) {
        modificationAddedListeners.add(listener);
    }

    @Override
    public void addModificationRemovedListener(FileModificationListener listener) {
        modificationRemovedListeners.add(listener);
    }
}
