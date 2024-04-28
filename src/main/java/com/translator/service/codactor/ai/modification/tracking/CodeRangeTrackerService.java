package com.translator.service.codactor.ai.modification.tracking;

import com.intellij.openapi.editor.RangeMarker;

public interface CodeRangeTrackerService {
    RangeMarker createRangeMarker(String filePath, int startIndex, int endIndex);
}
