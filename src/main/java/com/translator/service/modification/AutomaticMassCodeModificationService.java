package com.translator.service.modification;

import java.util.List;

public interface AutomaticMassCodeModificationService {
    void getModifiedCode(List<String> filePaths, String modification);

    void getFixedCode(List<String> filePaths, String error);

    void getTranslatedCode(List<String> filePaths, String newLanguage, String newFileType);
}
