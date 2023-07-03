package com.translator.service.codactor.modification;

import com.google.inject.Inject;
import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestFactory;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;

public class FileModificationSuggestionDiffViewerServiceImpl implements FileModificationSuggestionDiffViewerService {
    private Project project;

    @Inject
    public FileModificationSuggestionDiffViewerServiceImpl(Project project) {
        this.project = project;
    }

    @Override
    public void showDiffViewer(String beforeCode, String afterCode) {
        Document beforeDocument = EditorFactory.getInstance().createDocument(beforeCode);
        Document afterDocument = EditorFactory.getInstance().createDocument(afterCode);

        // Create DiffContents
        DiffContent beforeContent = DiffContentFactory.getInstance().create(project, beforeDocument);
        DiffContent afterContent = DiffContentFactory.getInstance().create(project, afterDocument);

        // Create a SimpleDiffRequest
        SimpleDiffRequest diffRequest = new SimpleDiffRequest("File Modification Suggestion", beforeContent, afterContent, "Before", "After");

        DiffManager.getInstance().showDiff(project, diffRequest);
    }
}
