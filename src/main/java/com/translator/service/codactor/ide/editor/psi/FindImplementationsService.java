package com.translator.service.codactor.ide.editor.psi;

import com.translator.model.codactor.ide.psi.implementation.ImplementationResultsResource;

public interface FindImplementationsService {
    ImplementationResultsResource findImplementationsWithinRange(String filePath, int startOffset, int endOffset);

    ImplementationResultsResource findImplementationsWithinRange(String filePath, String codeSnippet);
}
