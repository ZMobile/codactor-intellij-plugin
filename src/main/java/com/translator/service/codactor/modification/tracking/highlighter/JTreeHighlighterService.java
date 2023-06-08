package com.translator.service.codactor.modification.tracking.highlighter;

import javax.swing.*;
import java.io.File;

public interface JTreeHighlighterService {
    void repaint();

    void setJTree(JTree jTree);

    void setCurrentEditingDirectory(File currentEditingFileDirectory);
}
