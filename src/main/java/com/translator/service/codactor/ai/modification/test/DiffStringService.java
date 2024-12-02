package com.translator.service.codactor.ai.modification.test;

import com.translator.model.codactor.ai.modification.test.DiffType;

public interface DiffStringService {
    String getDiffString(String beforeCode, String afterCode);

    String postProcessDiffString(String diffString);

    String replaceConsideringDiffMarkers(String original, String oldSequence, String newSequence, DiffType diffType);

    String addMethodToEndOfClass(String diffString, String methodString, DiffType diffType);
}
