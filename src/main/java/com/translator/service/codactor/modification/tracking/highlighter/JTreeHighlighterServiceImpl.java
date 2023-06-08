package com.translator.service.codactor.modification.tracking.highlighter;

import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.FileModificationTracker;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.File;

public class JTreeHighlighterServiceImpl implements JTreeHighlighterService {
    private File currentEditingDirectory;
    private final FileModificationTrackerService fileModificationTrackerService;
    private JTree tree;
    private CustomTreeCellRenderer customTreeCellRenderer;

    public JTreeHighlighterServiceImpl(FileModificationTrackerService fileModificationTrackerService, JTree tree) {
        this.currentEditingDirectory = null;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.tree = tree;
        this.customTreeCellRenderer = new CustomTreeCellRenderer();
    }

    public void repaint() {
        if (tree == null) {
            return;
        }
        tree.setCellRenderer(customTreeCellRenderer);
        tree.repaint();
    }

    class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                Color selectionColor = Color.WHITE;
                Color nonSelectionColor = Color.BLACK;
                if (value instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    String filePath = currentEditingDirectory.getParent() + "/" + getPathString(node.getPath());
                    setBackground(Color.decode("#228B22"));
                    if (((DefaultMutableTreeNode) value).getChildCount() == 0) {
                        for (FileModificationTracker fileModificationTracker : fileModificationTrackerService.getActiveModificationFiles().values()) {
                            if (fileModificationTracker.getFilePath().startsWith(filePath)) {
                                boolean allModificationsDone = true;
                                for (FileModification fileModification : fileModificationTracker.getModifications()) {
                                    if (!fileModification.isDone()) {
                                        allModificationsDone = false;
                                        break;
                                    }
                                }
                                if (allModificationsDone) {
                                    selectionColor = Color.decode("#228B22");
                                    nonSelectionColor = Color.decode("#228B22");
                                } else {
                                    selectionColor = Color.decode("#009688");
                                    nonSelectionColor = Color.decode("#009688");
                                }
                            }
                        }
                    }
                }
                setTextSelectionColor(selectionColor);
                setTextNonSelectionColor(nonSelectionColor);
                return this;
            }
        }

    private String getPathString(TreeNode[] nodes) {
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 0; i < nodes.length; i++) {
            Object userObject = ((DefaultMutableTreeNode) nodes[i]).getUserObject();
            if (userObject != null) {
                if (i == 0) {
                    pathBuilder.append(userObject);
                } else {
                    pathBuilder.append("/").append(userObject);
                }
            }
        }
        return pathBuilder.toString();
    }

    public void setJTree(JTree jTree) {
        this.tree = jTree;
        repaint();
    }

    public void setCurrentEditingDirectory(File currentEditingDirectory) {
        this.currentEditingDirectory = currentEditingDirectory;
        repaint();
    }
}
