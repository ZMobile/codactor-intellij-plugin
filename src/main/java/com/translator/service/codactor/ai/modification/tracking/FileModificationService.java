package com.translator.service.codactor.ai.modification.tracking;

import com.google.inject.Injector;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiFile;
import com.translator.CodactorInjector;
import com.translator.model.codactor.ai.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.ai.modification.*;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface FileModificationService {
    FileModification addModification(String filePath, String modification, int startIndex, int endIndex, ModificationType modificationType, List<HistoricalContextObjectHolder> priorContext);

    void removeModification(FileModification fileModification);

    void implementModification(FileModification fileModification, String modification, boolean silent);

    void readyFileModificationUpdate(FileModification fileModification, String subjectLine, List<FileModificationSuggestionRecord> modificationOptions);

    void undoReadyFileModification(FileModification fileModification);

    void errorFileModification(FileModification fileModification);
}
