package com.translator.service.codactor.ide.editor.psi;

import com.translator.model.codactor.ide.psi.error.ErrorResult;

import java.util.List;

public interface FindErrorService {
    List<ErrorResult> getAllErrors(String filePath, boolean includeWarnings);

    List<ErrorResult> getErrorsWithinRange(String filePath, int startOffset, int endOffset, boolean includeWarnings);

    List<ErrorResult> getErrorsWithinRange(String filePath, String codeSnippet, boolean includeWarnings);
}
