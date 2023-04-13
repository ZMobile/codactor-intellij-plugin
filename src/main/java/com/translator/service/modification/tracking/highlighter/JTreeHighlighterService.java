package com.translator.service.modification.tracking.highlighter;

import javax.swing.*;
import java.io.File;

public interface JTreeHighlighterService {
    void repaint();

    void setJTree(JTree jTree);

    void setCurrentEditingDirectory(File currentEditingFileDirectory);
}
