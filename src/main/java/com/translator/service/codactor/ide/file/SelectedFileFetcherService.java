package com.translator.service.codactor.ide.file;

import com.intellij.openapi.vfs.VirtualFile;

public interface SelectedFileFetcherService {
    VirtualFile[] getCurrentlySelectedFiles();

    VirtualFile[] getOpenFiles();

    VirtualFile getSelectedFileInTreeView();

    VirtualFile[] getSelectedFilesInTreeView();
}
