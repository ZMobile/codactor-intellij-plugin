package com.translator.service.modification.tracking;

import com.intellij.openapi.editor.RangeMarker;

public interface CodeRangeTrackerService {
    RangeMarker createRangeMarker(String filePath, int startIndex, int endIndex);
}
