package com.translator.service.code;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiManager;
import com.translator.model.modification.FileModification;

public interface GuardedBlockService {
    void addFileModificationGuardedBlock(String fileModificationId, int startOffset, int endOffset);

    void removeFileModificationGuardedBlock(String fileModificationId);

    void addFileModificationSuggestionModificationGuardedBlock(String fileModificationSuggestionModificationId, int startOffset, int endOffset);

    void removeFileModificationSuggestionModificationGuardedBlock(String fileModificationSuggestionModificationId);
}
