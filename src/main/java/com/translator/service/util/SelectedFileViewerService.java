package com.translator.service.util;

import com.intellij.openapi.vfs.VirtualFile;

public interface SelectedFileViewerService {
    VirtualFile getSelectedFileInEditor();

    VirtualFile getSelectedFileInTreeView();
}
