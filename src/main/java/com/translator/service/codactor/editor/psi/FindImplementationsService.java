package com.translator.service.codactor.editor.psi;

import com.translator.model.codactor.editor.psi.implementation.ImplementationResultsResource;

import java.util.List;

public interface FindImplementationsService {
    ImplementationResultsResource findImplementationsWithinRange(String filePath, int startOffset, int endOffset);

    ImplementationResultsResource findImplementationsWithinRange(String filePath, String codeSnippet);
}
