package com.translator.model.uml.draw.adapter;

import com.intellij.openapi.command.undo.BasicUndoableAction;
import com.intellij.openapi.command.undo.DocumentReference;
import com.intellij.openapi.command.undo.UnexpectedUndoException;
import com.intellij.openapi.vfs.VirtualFile;
import javax.swing.undo.UndoableEdit;

public class UndoableEditAdapter extends BasicUndoableAction {
    private final UndoableEdit undoableEdit;

    public UndoableEditAdapter(VirtualFile file, UndoableEdit undoableEdit) {
        super(file);
        this.undoableEdit = undoableEdit;
    }

    @Override
    public void undo() {
        undoableEdit.undo();
    }

    @Override
    public void redo() {
        undoableEdit.redo();
    }
}
