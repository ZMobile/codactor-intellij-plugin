package com.translator.view.factory;

import com.google.inject.assistedinject.AssistedInject;
import com.translator.ProvisionalModificationCustomizer;
import com.translator.model.modification.FileModification;

public interface ProvisionalModificationCustomizerFactory {
    ProvisionalModificationCustomizer create(FileModification currentEditingFileModification);
}
