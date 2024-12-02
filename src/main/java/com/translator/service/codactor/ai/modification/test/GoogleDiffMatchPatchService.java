package com.translator.service.codactor.ai.modification.test;

public interface GoogleDiffMatchPatchService {
    String reconstructCodeWithGoogle(String originalCode, String modifiedCode);
}
