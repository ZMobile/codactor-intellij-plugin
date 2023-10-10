package com.translator.service.codactor.editor.psi;

import com.translator.model.codactor.editor.psi.ErrorResult;

import java.util.List;

public interface FindErrorService {
    List<ErrorResult> getErrorsWithinRange(String filePath, int startOffset, int endOffset, boolean includeWarnings);

    List<ErrorResult> getErrorsWithinRange(String filePath, String codeSnippet, boolean includeWarnings);
}
