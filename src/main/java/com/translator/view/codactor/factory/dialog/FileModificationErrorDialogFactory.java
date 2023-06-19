package com.translator.view.codactor.factory.dialog;

import com.translator.model.codactor.modification.ModificationType;
import com.translator.view.codactor.dialog.FileModificationErrorDialog;

public interface FileModificationErrorDialogFactory {
    FileModificationErrorDialog create(String modificationId, String filePath, String error, ModificationType modificationType);
}
