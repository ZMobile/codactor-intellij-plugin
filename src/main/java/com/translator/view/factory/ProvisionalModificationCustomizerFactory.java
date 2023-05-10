package com.translator.view.factory;

import com.translator.view.dialog.ProvisionalModificationCustomizer;
import com.translator.model.modification.FileModificationSuggestion;

public interface ProvisionalModificationCustomizerFactory {
    ProvisionalModificationCustomizer create(FileModificationSuggestion fileModificationSuggestion);
}
