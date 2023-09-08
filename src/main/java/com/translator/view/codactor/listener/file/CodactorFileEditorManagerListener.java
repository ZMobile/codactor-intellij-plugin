package com.translator.view.codactor.listener.file;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileEditor.ex.FileEditorWithProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.model.codactor.file.FileItem;
import com.translator.service.codactor.file.SelectedFileFetcherService;
import com.translator.view.uml.editor.CodactorUmlBuilderSVGEditor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class CodactorFileEditorManagerListener implements FileEditorManagerListener {
    private final Project project;
    private final SelectedFileFetcherService selectedFileFetcherService;
    private final ComboBox<FileItem> fileComboBox;

    public CodactorFileEditorManagerListener(Project project, SelectedFileFetcherService selectedFileFetcherService, ComboBox<FileItem> fileComboBox) {
        this.project = project;
        this.selectedFileFetcherService = selectedFileFetcherService;
        this.fileComboBox = fileComboBox;
    }

    @Override
    public void fileOpenedSync(@NotNull FileEditorManager source, @NotNull VirtualFile file, @NotNull Pair<FileEditor[], FileEditorProvider[]> editors) {
        FileEditorManagerListener.super.fileOpenedSync(source, file, editors);
        System.out.println("This gets called 1");
        if (file.getExtension().equals("svg")) {
            System.out.println("This gets called 3");
            if (editors != null) {
                for (FileEditor fileEditor : editors.getFirst()) {
                    if (fileEditor instanceof CodactorUmlBuilderSVGEditor) {
                        CodactorUmlBuilderSVGEditor codactorUmlBuilderSVGEditor = (CodactorUmlBuilderSVGEditor) fileEditor;
                        //codactorUmlBuilderSVGEditor.activateView();
                    }
                }
            }
        }
    }

    @Override
    public void fileOpenedSync(@NotNull FileEditorManager source, @NotNull VirtualFile file, @NotNull List<FileEditorWithProvider> editorsWithProviders) {
        FileEditorManagerListener.super.fileOpenedSync(source, file, editorsWithProviders);
        System.out.println("This gets called 2: " + file.getExtension());
        if (Objects.equals(file.getExtension(), "svg")) {
            System.out.println("This gets called too");
            for (FileEditorWithProvider fileEditorWithProvider : editorsWithProviders) {
                if (fileEditorWithProvider.getFileEditor() instanceof CodactorUmlBuilderSVGEditor) {
                    System.out.println("This gets called three");
                    CodactorUmlBuilderSVGEditor codactorUmlBuilderSVGEditor = (CodactorUmlBuilderSVGEditor) fileEditorWithProvider.getFileEditor();
                    //codactorUmlBuilderSVGEditor.activateView();
                }
            }
        }
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        FileEditorManagerListener.super.fileOpened(source, file);
        VirtualFile[] selectedFiles = selectedFileFetcherService.getCurrentlySelectedFiles();
        VirtualFile[] openFiles = selectedFileFetcherService.getOpenFiles();
        fileComboBox.setVisible(
                (selectedFiles != null && selectedFiles.length > 1)
                        || (openFiles != null && openFiles.length > 1));fileComboBox.removeAllItems();
        VirtualFile currentlyOpenFile = getSelectedFile();
        if (currentlyOpenFile != null) {
            fileComboBox.addItem(new FileItem(currentlyOpenFile.getPath()));
            if (selectedFiles.length > 1) {
                for (VirtualFile selectedFile : selectedFiles) {
                    if (!selectedFile.equals(currentlyOpenFile)) {
                        fileComboBox.addItem(new FileItem(selectedFile.getPath()));
                    }
                }
            } else if (openFiles.length > 1) {
                for (VirtualFile openFile : openFiles) {
                    if (!openFile.equals(currentlyOpenFile)) {
                        fileComboBox.addItem(new FileItem(openFile.getPath()));
                    }
                }
            }
        }
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        FileEditorManagerListener.super.fileClosed(source, file);
        VirtualFile[] selectedFiles = selectedFileFetcherService.getCurrentlySelectedFiles();
        VirtualFile[] openFiles = selectedFileFetcherService.getOpenFiles();
        fileComboBox.setVisible(
                (selectedFiles != null && selectedFiles.length > 1)
                        || (openFiles != null && openFiles.length > 1));
        fileComboBox.removeAllItems();
        VirtualFile currentlyOpenFile = getSelectedFile();
        if (currentlyOpenFile != null) {
            fileComboBox.addItem(new FileItem(currentlyOpenFile.getPath()));
            if (selectedFiles.length > 1) {
                for (VirtualFile selectedFile : selectedFiles) {
                    if (!selectedFile.equals(currentlyOpenFile)) {
                        fileComboBox.addItem(new FileItem(selectedFile.getPath()));
                    }
                }
            } else if (openFiles.length > 1) {
                for (VirtualFile openFile : openFiles) {
                    if (!openFile.equals(currentlyOpenFile)) {
                        fileComboBox.addItem(new FileItem(openFile.getPath()));
                    }
                }
            }
        }
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        FileEditorManagerListener.super.selectionChanged(event);
        VirtualFile newFile = event.getNewFile();
        FileEditor newEditor = event.getNewEditor();
        FileEditor oldEditor = event.getOldEditor();
        if (newFile != null && Objects.equals(newFile.getExtension(), "svg")) {
            if (newEditor instanceof CodactorUmlBuilderSVGEditor) {
                CodactorUmlBuilderSVGEditor codactorUmlBuilderSVGEditor = (CodactorUmlBuilderSVGEditor) newEditor;
                codactorUmlBuilderSVGEditor.activateView();
            }
        } else if (oldEditor instanceof CodactorUmlBuilderSVGEditor) {
            CodactorUmlBuilderSVGEditor codactorUmlBuilderSVGEditor = (CodactorUmlBuilderSVGEditor) oldEditor;
            codactorUmlBuilderSVGEditor.deactivateView();
        }
        VirtualFile[] selectedFiles = selectedFileFetcherService.getCurrentlySelectedFiles();
        VirtualFile[] openFiles = selectedFileFetcherService.getOpenFiles();
        fileComboBox.setVisible(
                (selectedFiles != null && selectedFiles.length > 1)
                        || (openFiles != null && openFiles.length > 1));fileComboBox.removeAllItems();
        VirtualFile currentlyOpenFile = getSelectedFile();
        if (currentlyOpenFile != null) {
            fileComboBox.addItem(new FileItem(currentlyOpenFile.getPath()));
            if (selectedFiles.length > 1) {
                for (VirtualFile selectedFile : selectedFiles) {
                    if (!selectedFile.equals(currentlyOpenFile)) {
                        fileComboBox.addItem(new FileItem(selectedFile.getPath()));
                    }
                }
            } else if (openFiles.length > 1) {
                for (VirtualFile openFile : openFiles) {
                    if (!openFile.equals(currentlyOpenFile)) {
                        fileComboBox.addItem(new FileItem(openFile.getPath()));
                    }
                }
            }
        }
    }

    private VirtualFile getSelectedFile() {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        Editor editor = fileEditorManager.getSelectedTextEditor();
        if (editor != null) {
            Document document = editor.getDocument();
            return FileDocumentManager.getInstance().getFile(document);
        }
        return null;
    }
}
