package com.translator.service.codactor.test;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface DiffStringService {
    String getDiffString(String beforeCode, String afterCode);

    String postProcessDiffString(String diffString);

    String replaceConsideringDiffMarkers(String original, String oldSequence, String newSequence, DiffType diffType);

    String addMethodToEndOfClass(String diffString, String methodString, DiffType diffType);
}
