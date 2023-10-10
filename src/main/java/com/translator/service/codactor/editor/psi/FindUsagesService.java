package com.translator.service.codactor.editor.psi;

import com.translator.model.codactor.editor.psi.UsageResult;

import java.util.List;

public interface FindUsagesService {
    List<UsageResult> findUsagesWithinRange(String filePath, int startOffset, int endOffset);

    List<UsageResult> findUsagesWithinRange(String filePath, String codeSnippet);
}
