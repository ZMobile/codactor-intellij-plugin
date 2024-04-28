package com.translator.service.codactor.ide.editor.diff;

public interface GitDiffStingGeneratorService {
    String createDiffString(String oldText, String newText);
}
