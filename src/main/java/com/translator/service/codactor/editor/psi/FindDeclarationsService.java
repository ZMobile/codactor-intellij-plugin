package com.translator.service.codactor.editor.psi;

import com.translator.model.codactor.editor.psi.DeclarationResult;

import java.util.List;

public interface FindDeclarationsService {
    List<DeclarationResult> findDeclarationsWithinRange(String filePath, int startOffset, int endOffset);

    List<DeclarationResult> findDeclarationsWithinRange(String filePath, String codeSnippet);
}
