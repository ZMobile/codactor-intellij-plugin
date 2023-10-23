package com.translator.service.codactor.editor.psi;

import com.translator.model.codactor.editor.psi.usage.UsageResult;
import com.translator.model.codactor.editor.psi.usage.UsageResultsResource;

import java.util.List;

public interface FindUsagesService {
    UsageResultsResource findUsagesWithinRange(String filePath, int startOffset, int endOffset);

    UsageResultsResource findUsagesWithinRange(String filePath, String codeSnippet);
}
