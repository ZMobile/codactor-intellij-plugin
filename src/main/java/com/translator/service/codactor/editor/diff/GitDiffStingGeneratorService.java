package com.translator.service.codactor.editor.diff;

public interface GitDiffStingGeneratorService {
    String createDiffString(String oldText, String newText);
}
