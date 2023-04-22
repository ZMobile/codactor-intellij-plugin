package com.translator.service.file;

import com.intellij.openapi.vfs.VirtualFile;

public interface SelectedFileFetcherService {
    VirtualFile[] getCurrentlySelectedFiles();

    VirtualFile[] getOpenFiles();
}
