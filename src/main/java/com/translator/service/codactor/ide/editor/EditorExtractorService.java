package com.translator.service.codactor.ide.editor;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public interface EditorExtractorService {
    Editor getEditorForVirtualFile(Project project, VirtualFile virtualFile);
}
