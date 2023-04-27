package com.translator.service.modification.tracking;

import com.intellij.openapi.project.Project;
import com.translator.ProvisionalModificationCustomizer;
import com.translator.model.modification.*;
import com.translator.service.code.CodeHighlighterService;
import com.translator.service.code.CodeSnippetExtractorService;
import com.translator.service.code.GuardedBlockService;
import com.translator.service.code.RangeReplaceService;
import com.translator.service.file.RenameFileService;
import com.translator.service.modification.tracking.listener.EditorClickHandlerService;
import com.translator.view.viewer.ModificationQueueViewer;

import javax.inject.Inject;
import java.io.File;
import java.util.*;

public class FileModificationTrackerServiceImpl implements FileModificationTrackerService {
    private Project project;
    private Map<String, FileModificationTracker> activeModificationFiles;
    private Map<String, FileModificationSuggestionModificationTracker> activeModificationSuggestionModifications;
    private Map<String, List<ProvisionalModificationCustomizer>> provisionalModificationCustomizerMap;
    private List<MultiFileModification> activeMultiFileModifications;
    private CodeHighlighterService codeHighlighterService;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private CodeRangeTrackerService codeRangeTrackerService;
    private GuardedBlockService guardedBlockService;
    private RangeReplaceService rangeReplaceService;
    private EditorClickHandlerService editorClickHandlerService;
    private RenameFileService renameFileService;
    private ModificationQueueViewer modificationQueueViewer;

    @Inject
    public FileModificationTrackerServiceImpl(Project project,
                                              CodeHighlighterService codeHighlighterService,
                                              CodeSnippetExtractorService codeSnippetExtractorService,
                                              CodeRangeTrackerService codeRangeTrackerService,
                                              GuardedBlockService guardedBlockService,
                                              RangeReplaceService rangeReplaceService,
                                              EditorClickHandlerService editorClickHandlerService,
                                              RenameFileService renameFileService) {
        this.project = project;
        this.activeModificationFiles = new HashMap<>();
        this.activeModificationSuggestionModifications = new HashMap<>();
        this.activeMultiFileModifications = new ArrayList<>();
        this.provisionalModificationCustomizerMap = new HashMap<>();
        this.codeHighlighterService = codeHighlighterService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.codeRangeTrackerService = codeRangeTrackerService;
        this.guardedBlockService = guardedBlockService;
        this.rangeReplaceService = rangeReplaceService;
        this.editorClickHandlerService = editorClickHandlerService;
        this.renameFileService = renameFileService;
    }

    public String addModification(String filePath, int startIndex, int endIndex, ModificationType modificationType) {
        System.out.println("Testo mini 1: " + filePath);
        String newFilePath = Objects.requireNonNullElse(filePath, "Untitled");
        FileModificationTracker fileModificationTracker;
        if (activeModificationFiles.containsKey(newFilePath)) {
            fileModificationTracker = activeModificationFiles.get(newFilePath);
        } else {
            fileModificationTracker = new FileModificationTracker(project, newFilePath, codeSnippetExtractorService, rangeReplaceService, codeRangeTrackerService);
            System.out.println("Testo mini 2");
            activeModificationFiles.put(newFilePath, fileModificationTracker);
            System.out.println("Testo mini 3");
            System.out.println("Testo mini 4");
        }
        System.out.println("Testo mini 5");
        String fileModificationId = fileModificationTracker.addModification(startIndex, endIndex, modificationType);
        if (fileModificationId == null) {
            //JBTextArea display = displayMap.get(newFilePath);
            /*JOptionPane.showMessageDialog(display, "Can't modify code that is already being modified", "Error",
                    JOptionPane.ERROR_MESSAGE);*/
        }
        if (modificationType != ModificationType.CREATE) {
            editorClickHandlerService.addEditorClickHandler(newFilePath);
            guardedBlockService.addFileModificationGuardedBlock(fileModificationId, startIndex, endIndex);
            codeHighlighterService.highlightTextArea(fileModificationTracker);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        return fileModificationId;
    }

    public String addModificationSuggestionModification(String filePath, String suggestionId, int startIndex, int endIndex, ModificationType modificationType) {
        String newFilePath = Objects.requireNonNullElse(filePath, "Untitled");
        FileModificationSuggestion fileModificationSuggestion = getModificationSuggestion(suggestionId);
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker;
        if (activeModificationSuggestionModifications.containsKey(suggestionId)) {
            fileModificationSuggestionModificationTracker = activeModificationSuggestionModifications.get(suggestionId);
        } else {
            fileModificationSuggestionModificationTracker = new FileModificationSuggestionModificationTracker(fileModificationSuggestion, rangeReplaceService);
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

    public String addMultiFileModification(String description) {
        MultiFileModification multiFileModification = new MultiFileModification(description);
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
            editorClickHandlerService.removeEditorClickHandler(fileModificationTracker.getFilePath());
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        guardedBlockService.removeFileModificationGuardedBlock(modificationId);
        //jTreeHighlighterService.repaint();
        codeHighlighterService.highlightTextArea(fileModificationTracker);
        disposeProvisionalModificationCustomizers(fileModification);
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
    public void implementModificationUpdate(String modificationId, String modification, boolean silent) {
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
        guardedBlockService.removeFileModificationGuardedBlock(modificationId);
        System.out.println("Creator testo 6");
        fileModificationTracker.implementModification(modificationId, modification, silent);
        if (fileModificationTracker.getModifications().isEmpty()) {
            activeModificationFiles.values().remove(fileModificationTracker);
            editorClickHandlerService.removeEditorClickHandler(fileModificationTracker.getFilePath());
        }
        codeHighlighterService.highlightTextArea(fileModificationTracker);
        disposeProvisionalModificationCustomizers(fileModification);
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
        if (fileModification.getModificationType() == ModificationType.TRANSLATE) {
            File file = new File(fileModification.getFilePath());
            String fileNameWithoutExtension = file.getName().substring(0, file.getName().lastIndexOf('.'));
            String newFileType;
            if (fileModification.getNewFileType().startsWith(".")){
                newFileType = fileModification.getNewFileType();
            } else {
                newFileType = "." + fileModification.getNewFileType();
            }
            String newFileName = fileNameWithoutExtension + newFileType;
            renameFileService.renameFile(fileModification.getFilePath(), newFileName);
        }
    }

    @Override
    public void implementModificationSuggestionModificationUpdate(FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord) {
        FileModificationSuggestionModificationTracker fileModificationSuggestionModificationTracker = activeModificationSuggestionModifications.values().stream()
                .filter(m -> m.hasModificationSuggestionModification(fileModificationSuggestionModificationRecord.getModificationSuggestionModificationId()))
                .findFirst()
                .orElseThrow();
        guardedBlockService.removeFileModificationSuggestionModificationGuardedBlock(fileModificationSuggestionModificationRecord.getModificationSuggestionModificationId());
        codeHighlighterService.highlightTextArea(fileModificationSuggestionModificationTracker);
        fileModificationSuggestionModificationTracker.implementModification(fileModificationSuggestionModificationRecord.getModificationSuggestionModificationId(), fileModificationSuggestionModificationRecord.getEditedCode().trim());
        if (fileModificationSuggestionModificationTracker.getModifications().isEmpty()) {
            activeModificationSuggestionModifications.values().remove(fileModificationSuggestionModificationTracker);
        }
        modificationQueueViewer.updateModificationList(getQueuedFileModificationObjectHolders());
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


    public void setModificationQueueViewer(ModificationQueueViewer modificationQueueViewer) {
        this.modificationQueueViewer = modificationQueueViewer;
    }

    public void addProvisionalModificationCustomizer(ProvisionalModificationCustomizer provisionalModificationCustomizer) {
        List<ProvisionalModificationCustomizer> provisionalModificationCustomizerList = provisionalModificationCustomizerMap.get(provisionalModificationCustomizer.getFileModificationSuggestion().getId());
        if (provisionalModificationCustomizerList == null) {
            provisionalModificationCustomizerList = new ArrayList<>();
        }
        provisionalModificationCustomizerList.add(provisionalModificationCustomizer);
        provisionalModificationCustomizerMap.put(provisionalModificationCustomizer.getFileModificationSuggestion().getId(), provisionalModificationCustomizerList);
    }

    private void disposeProvisionalModificationCustomizers(FileModification fileModification) {
        for (FileModificationSuggestion fileModificationSuggestion : fileModification.getModificationOptions()) {
            List<ProvisionalModificationCustomizer> provisionalModificationCustomizerList = provisionalModificationCustomizerMap.get(fileModificationSuggestion.getId());
            if (provisionalModificationCustomizerList != null) {
                for (ProvisionalModificationCustomizer provisionalModificationCustomizer : provisionalModificationCustomizerList) {
                    provisionalModificationCustomizer.dispose();
                }
            }
            provisionalModificationCustomizerMap.remove(fileModificationSuggestion.getId());
        }
    }

    @Override
    public void errorFileModification(String modificationId) {
        FileModification fileModification = getModification(modificationId);
        if (fileModification == null) {
            return;
        }
        fileModification.setError(true);
    }

    @Override
    public void retryFileModification(String modificationId) {
        FileModification fileModification = getModification(modificationId);
        if (fileModification == null) {
            return;
        }
        fileModification.setError(false);
    }
}
