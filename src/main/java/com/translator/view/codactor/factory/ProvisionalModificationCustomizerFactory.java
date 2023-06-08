package com.translator.view.codactor.factory;

import com.translator.ProvisionalModificationCustomizer;
import com.translator.model.codactor.modification.FileModificationSuggestion;

public interface ProvisionalModificationCustomizerFactory {
    ProvisionalModificationCustomizer create(FileModificationSuggestion fileModificationSuggestion);
}
