package com.translator.model.codactor.ai.modification;


import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.ide.editor.RangeReplaceService;
import com.translator.service.codactor.ide.editor.diff.DiffEditorGeneratorService;
import com.translator.service.codactor.ai.modification.tracking.CodeRangeTrackerService;

import java.util.ArrayList;
import java.util.List;

public class FileModificationTracker {
    private Project project;
    private final String filePath;
    private final List<FileModification> modifications;
    private final List<FileModificationUpdate> fileModificationUpdateQueue;

    public FileModificationTracker(Project project,
                                   String filePath) {
        this.project = project;
        this.filePath = filePath;
        this.modifications = new ArrayList<>();
        this.fileModificationUpdateQueue = new ArrayList<>();
    }

    public void addModificationUpdate(FileModificationUpdate update) {
        fileModificationUpdateQueue.add(update);
    }

    public List<FileModificationUpdate> getFileModificationUpdateQueue() {
        return fileModificationUpdateQueue;
    }


    public String addModification(FileModification fileModification) {
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
