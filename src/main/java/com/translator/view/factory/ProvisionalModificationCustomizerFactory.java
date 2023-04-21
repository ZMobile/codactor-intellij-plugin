package com.translator.view.factory;

import com.translator.ProvisionalModificationCustomizer;
import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationSuggestion;

public interface ProvisionalModificationCustomizerFactory {
    ProvisionalModificationCustomizer create(FileModificationSuggestion fileModificationSuggestion);
}
