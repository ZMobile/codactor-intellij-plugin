package com.translator.service.codactor.test;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;

import java.util.List;

public interface StringTokenizerService {
    String generateDiffString(String originalCode, String modifiedCode);

    String generateDiffString(List<Token> originalTokens, List<Token> modifiedTokens);

    String reconstructModifiedCode(String originalCode, String modifiedCode);

    String reconstructModifiedCodeWithRestoration(List<Range> topStopBeforeRanges, List<Range> topStopModifiedRanges, List<Range> bottomStopBeforeRanges, List<Range> bottomStopModifiedRanges, String originalCode, String modifiedCode);
}
