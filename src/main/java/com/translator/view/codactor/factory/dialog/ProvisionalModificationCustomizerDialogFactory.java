package com.translator.view.codactor.factory.dialog;

import com.translator.model.codactor.modification.FileModificationSuggestion;
import com.translator.view.codactor.dialog.ProvisionalModificationCustomizerDialog;

public interface ProvisionalModificationCustomizerDialogFactory {
    ProvisionalModificationCustomizerDialog create(FileModificationSuggestion fileModificationSuggestion);
}
