package com.translator.view.codactor.factory.dialog;

import com.translator.view.codactor.dialog.ProvisionalModificationCustomizerDialog;
import com.translator.model.codactor.modification.FileModificationSuggestion;

public interface ProvisionalModificationCustomizerDialogFactory {
    ProvisionalModificationCustomizerDialog create(FileModificationSuggestion fileModificationSuggestion);
}
