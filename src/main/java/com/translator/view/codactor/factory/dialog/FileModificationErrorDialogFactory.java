package com.translator.view.codactor.factory.dialog;

import com.google.inject.assistedinject.Assisted;
import com.translator.model.codactor.ai.modification.ModificationType;
import com.translator.view.codactor.dialog.FileModificationErrorDialog;

public interface FileModificationErrorDialogFactory {
    FileModificationErrorDialog create(@Assisted("modificationId") String modificationId, @Assisted("filePath") String filePath, @Assisted("error") String error, ModificationType modificationType);
}
