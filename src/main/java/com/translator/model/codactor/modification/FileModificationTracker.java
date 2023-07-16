package com.translator.model.codactor.modification;


import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.editor.diff.DiffEditorGeneratorService;
import com.translator.service.codactor.editor.RangeReplaceService;
import com.translator.service.codactor.modification.tracking.CodeRangeTrackerService;

import java.util.ArrayList;
import java.util.List;

public class FileModificationTracker {
    private Project project;
    private final String filePath;
    private final List<FileModification> modifications;
    private final List<FileModificationUpdate> fileModificationUpdateQueue;
    private final CodeSnippetExtractorService codeSnippetExtractorService;
    private final RangeReplaceService rangeReplaceService;
    private final CodeRangeTrackerService codeRangeTrackerService;
    private final DiffEditorGeneratorService diffEditorGeneratorService;

    public FileModificationTracker(Project project,
                                   String filePath,
                                   CodeSnippetExtractorService codeSnippetExtractorService,
                                   RangeReplaceService rangeReplaceService,
                                   CodeRangeTrackerService codeRangeTrackerService,
                                   DiffEditorGeneratorService diffEditorGeneratorService) {
        this.project = project;
        this.filePath = filePath;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.rangeReplaceService = rangeReplaceService;
        this.codeRangeTrackerService = codeRangeTrackerService;
        this.diffEditorGeneratorService = diffEditorGeneratorService;
        this.modifications = new ArrayList<>();
        this.fileModificationUpdateQueue = new ArrayList<>();
    }

    public void addModificationUpdate(FileModificationUpdate update) {
        fileModificationUpdateQueue.add(update);
    }

    public List<FileModificationUpdate> getFileModificationUpdateQueue() {
        return fileModificationUpdateQueue;
    }


    public String addModification(String modification, int startIndex, int endIndex, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext) {
        String beforeText = codeSnippetExtractorService.getSnippet(filePath, startIndex, endIndex);
        RangeMarker rangeMarker = codeRangeTrackerService.createRangeMarker(filePath, startIndex, endIndex);
        for (FileModification m : modifications) {
            RangeMarker existingRangeMarker = m.getRangeMarker();
            if (existingRangeMarker != null && existingRangeMarker.isValid()) {
                if ((startIndex <= existingRangeMarker.getStartOffset() && endIndex >= existingRangeMarker.getStartOffset()) || (startIndex <= existingRangeMarker.getEndOffset() && endIndex >= existingRangeMarker.getEndOffset())) {
                    return null;
                }
            }
        }
        FileModification fileModification = new FileModification(filePath, modification, rangeMarker, beforeText, modificationType, priorContext);
        modifications.add(fileModification);
        return fileModification.getId();
    }


   public void removeModification(String modificationId) {
       FileModification m = modifications.stream()
                .filter(modification -> modification.getId().equals(modificationId))
                .findFirst()
                .orElse(null);
        if (m == null) {
            return;
        }
        modifications.remove(m);
    }

    public boolean hasModification(String modificationId) {
        return modifications.stream()
                .anyMatch(m -> m.getId().equals(modificationId));
    }

    public List<FileModification> getModifications() {
        return modifications;
    }


    public void implementModification(String modificationId, String modification, boolean silent) {
        synchronized (modifications) {
            for (FileModification m : modifications) {
                if (m.getId().equals(modificationId)) {
                    RangeMarker rangeMarker = m.getRangeMarker();
                    if (rangeMarker != null && rangeMarker.isValid()) {
                        int formerStartIndex = rangeMarker.getStartOffset();
                        int formerEndIndex = rangeMarker.getEndOffset();
                        rangeReplaceService.replaceRange(filePath, formerStartIndex, formerEndIndex, modification, silent);
                        rangeMarker.dispose(); // Dispose the RangeMarker after it's no longer needed
                    } else if (m.getModificationType() == ModificationType.CREATE) {
                        rangeReplaceService.replaceRange(filePath, 0, 0, modification, silent);
                    }
                    modifications.remove(m);
                    break;
                }
            }
        }
    }

    public void readyFileModificationUpdate(String modificationId, String subjectLine, List<FileModificationSuggestionRecord> modificationOptions) {
        FileModification fileModification = getModification(modificationId);
        if (fileModification != null) {
            fileModification.setSubjectLine(subjectLine);
            fileModification.setModificationRecordId(modificationOptions.get(0).getModificationId());
            List<FileModificationSuggestion> suggestions = new ArrayList<>();
            for (FileModificationSuggestionRecord modificationOption : modificationOptions) {
                if (fileModification.getModificationType() == ModificationType.TRANSLATE) {
                    suggestions.add(new FileModificationSuggestion(diffEditorGeneratorService, project, modificationOption.getId(), filePath, modificationId, fileModification.getBeforeText(), modificationOption.getSuggestedCode(), fileModification.getNewFileType().trim().toLowerCase()));
                } else {
                    FileModificationSuggestion fileModificationSuggestion = new FileModificationSuggestion(diffEditorGeneratorService, project, modificationOption.getId(), filePath, modificationId, fileModification.getBeforeText(), modificationOption.getSuggestedCode());
                    suggestions.add(fileModificationSuggestion);
                }
            }
            fileModification.setModificationOptions(suggestions);
            fileModification.setDone(true);
        }
    }

    public void undoReadyFileModification(String modificationId) {
        FileModification fileModification = getModification(modificationId);
        if (fileModification != null) {
            fileModification.setDone(false);
            fileModification.getModificationOptions().clear();
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public FileModification getModification(String modificationId) {
        for (FileModification m : modifications) {
            if (m.getId().equals(modificationId)) {
                return m;
            }
        }
        return null;
    }
    public String getFileExtension(String filePath) {
        int i = filePath.lastIndexOf('.');
        if (i > 0) {
            return filePath.substring(i + 1);
        }
        return "";
    }

    public void errorModification(String modificationId) {
        FileModification fileModification = getModification(modificationId);
        if (fileModification != null) {
            fileModification.setError(true);
        }
    }
}
