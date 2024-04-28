package com.translator.view.codactor.factory.dialog;

import com.translator.model.codactor.ai.modification.FileModificationSuggestion;
import com.translator.view.codactor.dialog.modification.ProvisionalModificationCustomizerDialog;

public interface ProvisionalModificationCustomizerDialogFactory {
    ProvisionalModificationCustomizerDialog create(FileModificationSuggestion fileModificationSuggestion);
}
