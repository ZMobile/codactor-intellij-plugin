package com.translator.service.code;

import com.intellij.openapi.fileTypes.FileType;

public interface CodeToFileTypeTransformerService {
    FileType convert(String code);

    String detectLanguage(String code);
}
