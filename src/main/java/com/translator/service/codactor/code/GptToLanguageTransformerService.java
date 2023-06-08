package com.translator.service.codactor.code;

import java.util.List;

public interface GptToLanguageTransformerService {
    String convert(String text);

    String convert(List<String> texts);

    String getFromFilePath(String filePath);

    String getExtensionFromLanguage(String language);
}
