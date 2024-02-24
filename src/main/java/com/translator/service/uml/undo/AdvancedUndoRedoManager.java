package com.translator.service.uml.undo;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.model.uml.draw.adapter.UndoableEditAdapter;
import org.jhotdraw.undo.UndoRedoManager;

import javax.swing.undo.UndoableEdit;

public class AdvancedUndoRedoManager extends UndoRedoManager {
    private Project project;
    private String filePath;

    public AdvancedUndoRedoManager(Project project) {
        this.project = project;
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        CommandProcessor.getInstance().executeCommand(project, () -> {
            UndoManager undoManager = UndoManager.getInstance(project);
            VirtualFile file = LocalFileSystem.getInstance().findFileByPath(filePath);
            undoManager.undoableActionPerformed(new UndoableEditAdapter(file, anEdit));
        }, "Add Edit", "Add Edit");
        return super.addEdit(anEdit);
    }

    public Project getProject() {
        System.out.println("Wohoo 2!");
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}