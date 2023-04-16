package com.translator.service.modification.tracking;

import com.intellij.openapi.project.Project;
import com.translator.model.modification.*;
import com.translator.service.code.CodeHighlighterService;
import com.translator.service.code.CodeSnippetExtractorService;
import com.translator.service.code.GuardedBlockService;
import com.translator.service.code.RangeReplaceService;
import com.translator.service.ui.ModificationQueueListButtonService;
import com.translator.view.viewer.ModificationQueueViewer;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.*;

public class FileModificationTrackerServiceImpl implements FileModificationTrackerService {
    private Project project;
    private Map<String, FileModificationTracker> activeModificationFiles;
    private Map<String, FileModificationSuggestionModificationTracker> activeModificationSuggestionModifications;
    private List<MultiFileModification> activeMultiFileModifications;
    private CodeHighlighterService codeHighlighterService;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private CodeRangeTrackerService codeRangeTrackerService;
    private GuardedBlockService guardedBlockService;
    private RangeReplaceService rangeReplaceService;
    private ModificationQueueListButtonService modificationQueueListButtonService;
    private ModificationQueueViewer modificationQueueViewer;
    private boolean implementingQueuedModifications;

    @Inject
    public FileModificationTrackerServiceImpl(Project project,
                                              CodeHighlighterService codeHighlighterService,
                                              CodeSnippetExtractorService codeSnippetExtractorService,
                                              CodeRangeTrackerService codeRangeTrackerService,
                                              GuardedBlockService guardedBlockService,
                                              RangeReplaceService rangeReplaceService) {
        this.project = project;
        this.activeModificationFiles = new HashMap<>();
        this.activeModificationSuggestionModifications = new HashMap<>();
        this.activeMultiFileModifications = new ArrayList<>();
        this.codeHighlighterService = codeHighlighterService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.codeRangeTrackerService = codeRangeTrackerService;
        this.guardedBlockService = guardedBlockService;
        this.rangeReplaceService = rangeReplaceService;
        this.implementingQueuedModifications = false;
    }

    public String addModification(String filePath, int startIndex, int endIndex, ModificationType modificationType) {
        String newFilePath;
        newFilePath = Objects.requireNonNullElse(filePath, "Untitled");
        FileModificationTracker fileModificationTracker;
        if (activeModificationFiles.containsKey(newFilePath)) {
            fileModificationTracker = activeModificationFiles.get(newFilePath);
        } else {
            fileModificationTracker = new FileModificationTracker(project, newFilePath, codeSnippetExtractorService, rangeReplaceService, codeRangeTrackerService);
            activeModificationFiles.put(newFilePath, fileModificationTracker);
        }
        String fileModificationId = fileModificationTracker.addModification(startIndex, endIndex, modificationType);
        if (fileModificationId == null) {
            //JBTextArea display = displayMap.get(newFilePath);
            /*JOptionPane.showMessageDialog(display, "Can't modify code that is already being modified", "Error",
                    JOptionPane.ERROR_MESSAGE);*/
        }
        FileModification fileModification = fileModificationTracker.getModification(fileModificationId);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        guardedBlockService.addFileModificationGuardedBlock(fileModificationId, startIndex, endIndex);
        codeHighlighterService.highlightTextArea(fileModificationTracker);
        return fileModificationId;
    }

    public String addModificationSuggestionModification(String filePath, String suggestionId, int startIndex, int endIndex, ModificationType modificationType) {
        String newFilePath = Objects.requireNonNullElse(filePath, "Untitled");
        FileModificationSuggestion fileModificationSuggestion = getModificationSuggestion(suggestionId);
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker;
        if (activeModificationSuggestionModifications.containsKey(suggestionId)) {
            fileModificationSuggestionModificationTracker = activeModificationSuggestionModifications.get(suggestionId);
        } else {
            fileModificationSuggestionModificationTracker = new FileModificationSuggestionModificationTracker(fileModificationSuggestion);
            activeModificationSuggestionModifications.put(suggestionId, fileModificationSuggestionModificationTracker);
        }
        String fileModificationSuggestionModificationId = fileModificationSuggestionModificationTracker.addModificationSuggestionModification(newFilePath, startIndex, endIndex, modificationType);
        if (fileModificationSuggestionModificationId == null) {
            /*JBTextArea display = fileModificationSuggestion.getDisplay();
            JOptionPane.showMessageDialog(display, "Can't modify code that is already being modified", "Error",
                    JOptionPane.ERROR_MESSAGE);*/
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        guardedBlockService.addFileModificationSuggestionModificationGuardedBlock(fileModificationSuggestionModificationId, startIndex, endIndex);
        //jTreeHighlighterService.repaint();
        codeHighlighterService.highlightTextArea(fileModificationSuggestionModificationTracker);
        return fileModificationSuggestionModificationId;
    }

    public String addMultiFileModification(String description, String language, String fileExtension, String filePath) {
        MultiFileModification multiFileModification = new MultiFileModification(description, language, fileExtension, filePath);
        activeMultiFileModifications.add(multiFileModification);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        return multiFileModification.getId();
    }

    public void removeModification(String modificationId) {
        FileModificationTracker fileModificationTracker = activeModificationFiles.values().stream()
                .filter(m -> m.hasModification(modificationId))
                .findFirst()
                .orElseThrow();
        FileModification fileModification = fileModificationTracker.getModification(modificationId);
        for (FileModificationSuggestion fileModificationSuggestion : fileModification.getModificationOptions()) {
            FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = getModificationSuggestionModificationTracker(fileModificationSuggestion.getId());
            if (fileModificationSuggestionModificationTracker == null) {
                continue;
            }
            for (FileModificationSuggestionModification fileModificationSuggestionModification : fileModificationSuggestionModificationTracker.getModifications()) {
                removeModificationSuggestionModification(fileModificationSuggestionModification.getId());
            }
        }
        fileModificationTracker.removeModification(modificationId);
        if (fileModificationTracker.getModifications().isEmpty()) {
            activeModificationFiles.values().remove(fileModificationTracker);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        guardedBlockService.removeFileModificationGuardedBlock(modificationId);
        //jTreeHighlighterService.repaint();
        codeHighlighterService.highlightTextArea(fileModificationTracker);
    }

    public void removeModificationSuggestionModification(String modificationSuggestionModificationId) {
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = activeModificationSuggestionModifications.values().stream()
                .filter(m -> m.hasModificationSuggestionModification(modificationSuggestionModificationId))
                .findFirst()
                .orElseThrow();
        fileModificationSuggestionModificationTracker.removeModificationSuggestionModification(modificationSuggestionModificationId);
        if (fileModificationSuggestionModificationTracker.getModifications().isEmpty()) {
            activeModificationSuggestionModifications.values().remove(fileModificationSuggestionModificationTracker);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        guardedBlockService.removeFileModificationSuggestionModificationGuardedBlock(modificationSuggestionModificationId);
        //jTreeHighlighterService.repaint();
        codeHighlighterService.highlightTextArea(fileModificationSuggestionModificationTracker);
    }

    public void removeMultiFileModification(String multiFileModificationId) {
        MultiFileModification multiFileModification = activeMultiFileModifications.stream()
                .filter(m -> m.getId().equals(multiFileModificationId))
                .findFirst()
                .orElseThrow();
        activeMultiFileModifications.remove(multiFileModification);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
    }

    public void setMultiFileModificationStage(String multiFileModificationId, String stage) {
        MultiFileModification multiFileModification = activeMultiFileModifications.stream()
                .filter(m -> m.getId().equals(multiFileModificationId))
                .findFirst()
                .orElseThrow();
        multiFileModification.setStage(stage);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
    }

    @Override
    public void queueModificationUpdate(String modificationId, String modification) {
        FileModificationTracker fileModificationTracker = activeModificationFiles.values().stream()
                .filter(m -> m.hasModification(modificationId))
                .findFirst()
                .orElse(null);
        if (fileModificationTracker == null) {
            return;
        }
        fileModificationTracker.addModificationUpdate(new FileModificationUpdate(modificationId, modification));
        if (fileModificationTracker.getModifications().isEmpty()) {
            activeModificationFiles.values().remove(fileModificationTracker);
        }
        guardedBlockService.removeFileModificationGuardedBlock(modificationId);
    }

    @Override
    public void queueModificationSuggestionModificationUpdate(FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord) {
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = activeModificationSuggestionModifications.values().stream()
                .filter(m -> m.hasModificationSuggestionModification(fileModificationSuggestionModificationRecord.getModificationSuggestionModificationId()))
                .findFirst()
                .orElseThrow();
        fileModificationSuggestionModificationTracker.addModificationUpdate(new FileModificationUpdate(fileModificationSuggestionModificationRecord.getModificationSuggestionModificationId(), fileModificationSuggestionModificationRecord.getEditedCode().trim()));
        if (fileModificationSuggestionModificationTracker.getModifications().isEmpty()) {
            activeModificationSuggestionModifications.values().remove(fileModificationSuggestionModificationTracker);
        }
        guardedBlockService.removeFileModificationSuggestionModificationGuardedBlock(fileModificationSuggestionModificationRecord.getModificationSuggestionModificationId());
    }

    @Override
    public void implementQueuedModificationUpdates() {
        if (implementingQueuedModifications) {
            return;
        }
        implementingQueuedModifications = true;
        for (FileModificationTracker fileModificationTracker : activeModificationFiles.values()) {
            if (!fileModificationTracker.getFileModificationUpdateQueue().isEmpty()) {
                fileModificationTracker.processModificationUpdates();
            }
        }
        for (FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker : activeModificationSuggestionModifications.values()) {
            if (!fileModificationSuggestionModificationTracker.getFileModificationUpdateQueue().isEmpty()) {
                fileModificationSuggestionModificationTracker.processModificationUpdates();
            }
        }
        if (modificationQueueViewer != null) {
            modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        }
        if (modificationQueueListButtonService != null) {
        }
        implementingQueuedModifications = false;
    }

    @Override
    public void implementModificationUpdate(String modificationId, String modification) {
        FileModificationTracker fileModificationTracker = activeModificationFiles.values().stream()
                .filter(m -> m.hasModification(modificationId))
                .findFirst()
                .orElse(null);
        if (fileModificationTracker == null) {
            return;
        }
        FileModification fileModification = fileModificationTracker.getModification(modificationId);
        for (FileModificationSuggestion fileModificationSuggestion : fileModification.getModificationOptions()) {
            FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = getModificationSuggestionModificationTracker(fileModificationSuggestion.getId());
            if (fileModificationSuggestionModificationTracker == null) {
                continue;
            }
            for (FileModificationSuggestionModification fileModificationSuggestionModification : fileModificationSuggestionModificationTracker.getModifications()) {
                removeModificationSuggestionModification(fileModificationSuggestionModification.getId());
            }
        }
        fileModificationTracker.implementModification(modificationId, modification);
        if (fileModificationTracker.getModifications().isEmpty()) {
            activeModificationFiles.values().remove(fileModificationTracker);
        }
        guardedBlockService.removeFileModificationGuardedBlock(modificationId);
    }

    @Override
    public void updateModifications(String filePath, int formerStartIndex, int formerEndIndex, String textInserted) {
        String newFilePath;
        newFilePath = Objects.requireNonNullElse(filePath, "Untitled");
        FileModificationTracker fileModificationTracker = activeModificationFiles.get(newFilePath);
        if (fileModificationTracker == null) {
            return;
        }
        //jTreeHighlighterService.repaint();
        codeHighlighterService.highlightTextArea(fileModificationTracker);
    }

    @Override
    public void updateModificationSuggestionModifications(String suggestionId, int formerStartIndex, int formerEndIndex, String textInserted) {
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = activeModificationSuggestionModifications.get(suggestionId);
        if (fileModificationSuggestionModificationTracker == null) {
            return;
        }
        //jTreeHighlighterService.repaint();
        codeHighlighterService.highlightTextArea(fileModificationSuggestionModificationTracker);
    }

    public void reHighlightTextArea(String filePath) {
        String newFilePath;
        newFilePath = Objects.requireNonNullElse(filePath, "Untitled");
        codeHighlighterService.highlightTextArea(activeModificationFiles.get(newFilePath));
    }

    public Map<String, FileModificationTracker> getActiveModificationFiles() {
        return activeModificationFiles;
    }

    public Map<String, FileModificationSuggestionModificationTracker> getActiveModificationSuggestionModifications() {
        return activeModificationSuggestionModifications;
    }

    public FileModification getModification(String modificationId) {
        FileModificationTracker fileModificationTracker = activeModificationFiles.values().stream()
                .filter(m -> m.hasModification(modificationId))
                .findFirst()
                .orElse(null);
        if (fileModificationTracker == null) {
            return null;
        }
        return fileModificationTracker.getModification(modificationId);
    }

    public FileModificationSuggestion getModificationSuggestion(String suggestionId) {
        List<FileModification> fileModifications = getAllFileModifications();
        for (FileModification fileModification : fileModifications) {
            FileModificationSuggestion fileModificationSuggestion = fileModification.getModificationOptions().stream()
                    .filter(m -> m.getId().equals(suggestionId))
                    .findFirst()
                    .orElse(null);
            if (fileModificationSuggestion != null) {
                return fileModificationSuggestion;
            }
        }
        return null;
    }

    public FileModificationTracker getModificationTracker(String filePath) {
        return activeModificationFiles.get(filePath);
    }

    public FileModificationSuggestionModificationTracker getModificationSuggestionModificationTracker(String suggestionId) {
        return activeModificationSuggestionModifications.get(suggestionId);
    }

    public FileModificationSuggestionModification getModificationSuggestionModification(String modificationSuggestionModificationId) {
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = getActiveModificationSuggestionModifications().values().stream()
                .filter(m -> m.hasModificationSuggestionModification(modificationSuggestionModificationId))
                .findFirst()
                .orElse(null);
        if (fileModificationSuggestionModificationTracker == null) {
            return null;
        }
        return fileModificationSuggestionModificationTracker.getModificationSuggestionModification(modificationSuggestionModificationId);
    }

    public void readyFileModificationUpdate(String modificationId, List<FileModificationSuggestionRecord> modificationOptions) {
        FileModificationTracker fileModificationTracker = activeModificationFiles.values().stream()
                .filter(m -> m.hasModification(modificationId))
                .findFirst()
                .orElseThrow();
        fileModificationTracker.readyFileModificationUpdate(modificationId, modificationOptions);
        codeHighlighterService.highlightTextArea(fileModificationTracker);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
    }

    public List<FileModification> getAllFileModifications() {
        List<FileModification> fileModifications = new ArrayList<>();
        for (FileModificationTracker fileModificationTracker : activeModificationFiles.values()) {
            fileModifications.addAll(fileModificationTracker.getModifications());
        }
        return fileModifications;
    }

    public List<FileModificationSuggestionModification> getAllFileModificationSuggestionModifications() {
        List<FileModificationSuggestionModification> fileModificationSuggestionModifications = new ArrayList<>();
        for (FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker : activeModificationSuggestionModifications.values()) {
            fileModificationSuggestionModifications.addAll(fileModificationSuggestionModificationTracker.getModifications());
        }
        return fileModificationSuggestionModifications;
    }

    public List<QueuedFileModificationObjectHolder> getQueuedFileModificationObjectHolders() {
        List<QueuedFileModificationObjectHolder> queuedFileModificationObjectHolders = new ArrayList<>();
        List<FileModification> fileModifications = new ArrayList<>(getAllFileModifications());
        for (FileModification fileModification : fileModifications) {
            QueuedFileModificationObjectHolder queuedFileModificationObjectHolder = new QueuedFileModificationObjectHolder(fileModification);
            queuedFileModificationObjectHolders.add(queuedFileModificationObjectHolder);
        }
        List<FileModificationSuggestionModification> fileModificationSuggestionModifications = new ArrayList<>(getAllFileModificationSuggestionModifications());
        for (FileModificationSuggestionModification fileModificationSuggestionModification : fileModificationSuggestionModifications) {
            QueuedFileModificationObjectHolder queuedFileModificationObjectHolder = new QueuedFileModificationObjectHolder(fileModificationSuggestionModification);
            queuedFileModificationObjectHolders.add(queuedFileModificationObjectHolder);
        }
        for (MultiFileModification multiFileModification : activeMultiFileModifications) {
            QueuedFileModificationObjectHolder queuedFileModificationObjectHolder = new QueuedFileModificationObjectHolder(multiFileModification);
            queuedFileModificationObjectHolders.add(queuedFileModificationObjectHolder);
        }
        return queuedFileModificationObjectHolders;
    }


    @Override
    public Color getModificationQueueListButtonColor() {
        return modificationQueueListButtonService.getModificationQueueListButtonColor();
    }

    public void setModificationQueueListButtonService(ModificationQueueListButtonService modificationQueueListButtonService) {
        this.modificationQueueListButtonService = modificationQueueListButtonService;
    }

    public void setModificationQueueViewer(ModificationQueueViewer modificationQueueViewer) {
        this.modificationQueueViewer = modificationQueueViewer;
    }
}
