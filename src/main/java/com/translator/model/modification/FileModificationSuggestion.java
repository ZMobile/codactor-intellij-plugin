package com.translator.model.modification;

import com.intellij.ui.components.JBTextArea;

import java.util.Objects;

public class FileModificationSuggestion {
    private final String filePath;
    private final String modificationId;
    private final String myId;
    private final String suggestedCode;

    public FileModificationSuggestion(String id, String filePath, String modificationId, String suggestedCode, String styleKey) {
        this.myId = id;
        this.filePath = filePath;
        this.modificationId = modificationId;
        this.suggestedCode = suggestedCode;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getId() {
        return myId;
    }

    public String getModificationId() {
        return modificationId;
    }

    public String getSuggestedCode() {
        return suggestedCode;
    }
}
