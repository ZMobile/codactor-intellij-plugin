package com.translator.view.uml.editor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.HashMap;
import java.util.Map;

public class CodactorUmlBuilderSVGEditorProvider implements FileEditorProvider {
    @Override
    public boolean accept(Project project, VirtualFile file) {
        return "svg".equals(file.getExtension());  // Check if the file is an SVG file
    }

    @Override
    public FileEditor createEditor(Project project, VirtualFile file) {
        return new CodactorUmlBuilderSVGEditor(project, file);
    }

    @Override
    public @org.jetbrains.annotations.NotNull
    @org.jetbrains.annotations.NonNls String getEditorTypeId() {
        return "com.translator.view.uml.editor.CodactorUmlBuilderSVGEditor";
    }

    @Override
    public @org.jetbrains.annotations.NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}