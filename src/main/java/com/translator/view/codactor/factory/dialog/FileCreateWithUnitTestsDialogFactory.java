package com.translator.view.codactor.factory.dialog;

import com.intellij.psi.PsiDirectory;
import com.translator.view.codactor.dialog.test.FileCreateWithUnitTestsDialog;

public interface FileCreateWithUnitTestsDialogFactory {
    FileCreateWithUnitTestsDialog create(PsiDirectory directory);
}

