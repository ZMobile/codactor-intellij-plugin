package com.translator.service.codactor.test;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;

import java.util.List;

public interface StringTokenizerService {
    String generateDiffString(String originalCode, String modifiedCode);

    String generateDiffString(List<Token> originalTokens, List<Token> modifiedTokens);

    String reconstructModifiedCode(String originalCode, String modifiedCode);

    String reconstructModifiedCodeWithRestoration(String originalCode, String modifiedCode);
}
