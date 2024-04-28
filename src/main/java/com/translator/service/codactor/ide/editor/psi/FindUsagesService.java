package com.translator.service.codactor.ide.editor.psi;

import com.translator.model.codactor.ide.psi.usage.UsageResultsResource;

public interface FindUsagesService {
    UsageResultsResource findUsagesWithinRange(String filePath, int startOffset, int endOffset);

    UsageResultsResource findUsagesWithinRange(String filePath, String codeSnippet);
}
