package com.translator.service.codactor.modification.tracking;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicReference;

public class CodeRangeTrackerServiceImpl implements CodeRangeTrackerService {
    private final Project project;

    @Inject
    public CodeRangeTrackerServiceImpl(Project project) {
        this.project = project;
    }

    public RangeMarker createRangeMarker(String filePath, int startIndex, int endIndex) {
        // Convert the file path to a VirtualFile instance
        AtomicReference<RangeMarker> rangeMarker = new AtomicReference<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath);

            if (virtualFile != null) {
                // Get the Document instance corresponding to the VirtualFile
                Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

                if (document != null) {
                    // Create a RangeMarker for the specified range
                    int newEndIndex = Math.min(endIndex, document..getText().length());
                    rangeMarker.set(document.createRangeMarker(startIndex, newEndIndex));
                }
            }
        });
        return rangeMarker.get();
    }
}
