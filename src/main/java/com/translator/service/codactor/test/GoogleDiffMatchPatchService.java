package com.translator.service.codactor.test;

public interface GoogleDiffMatchPatchService {
    String reconstructCodeWithGoogle(String originalCode, String modifiedCode);
}
