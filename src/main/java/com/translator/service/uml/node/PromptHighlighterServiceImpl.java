package com.translator.service.uml.node;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextArea;
import com.translator.model.uml.draw.connection.Connection;
import com.translator.view.codactor.panel.FixedHeightPanel;
import com.translator.view.codactor.viewer.inquiry.InquiryChatViewer;
import com.translator.view.uml.node.dialog.prompt.PromptConnectionViewer;
import com.translator.view.uml.node.dialog.prompt.PromptNodeDialog;
import com.translator.view.uml.node.dialog.prompt.PromptViewer;

import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.List;

public class PromptHighlighterServiceImpl implements PromptHighlighterService {
    @Override
    public void highlightPrompts(PromptNodeDialog promptNodeDialog) {
        PromptConnectionViewer promptConnectionViewer = promptNodeDialog.getPromptConnectionViewer();
        PromptViewer promptViewer = promptNodeDialog.getPromptViewer();
        List<Connection> promptInputs = promptConnectionViewer.getInputs();
        List<InquiryChatViewer> prompts = promptViewer.getPrompts();
        ApplicationManager.getApplication().invokeLater(() -> {
            for (InquiryChatViewer prompt : prompts) {
                for (Component component : prompt.getComponents()) {
                    if (component instanceof JBTextArea) {
                        JBTextArea selectedJBTextArea = (JBTextArea) component;
                        selectedJBTextArea.getHighlighter().removeAllHighlights();
                        highlightTextArea(selectedJBTextArea, promptInputs);
                    } else if (component instanceof FixedHeightPanel) {
                        FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                        if (fixedHeightPanel.getEditor() != null) {
                            Editor editor = fixedHeightPanel.getEditor();
                            editor.getMarkupModel().removeAllHighlighters();
                            highlightEditor(editor, promptInputs);
                        }
                    }
                }
            }
            promptViewer.getPromptJList().repaint();
        });
    }

    public void highlightPromptsWithoutRemoval(PromptNodeDialog promptNodeDialog) {
        PromptConnectionViewer promptConnectionViewer = promptNodeDialog.getPromptConnectionViewer();
        PromptViewer promptViewer = promptNodeDialog.getPromptViewer();
        List<Connection> promptInputs = promptConnectionViewer.getInputs();
        List<InquiryChatViewer> prompts = promptViewer.getPrompts();
        ApplicationManager.getApplication().invokeLater(() -> {
            for (InquiryChatViewer prompt : prompts) {
                for (Component component : prompt.getComponents()) {
                    if (component instanceof JBTextArea) {
                        JBTextArea selectedJBTextArea = (JBTextArea) component;
                        highlightTextArea(selectedJBTextArea, promptInputs);
                    } else if (component instanceof FixedHeightPanel) {
                        FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                        if (fixedHeightPanel.getEditor() != null) {
                            Editor editor = fixedHeightPanel.getEditor();
                            highlightEditor(editor, promptInputs);
                        }
                    }
                }
            }
            promptViewer.getPromptJList().repaint();
        });
    }

    private void highlightTextArea(JBTextArea textArea, List<Connection> promptInputs) {
        for (Connection connection : promptInputs) {
            String key = connection.getOutputKey();
            if (key == null || key.isEmpty()) {
                continue;
            }
            int startIdx = 0;
            while (startIdx != -1) {
                startIdx = textArea.getText().indexOf(key, startIdx);
                if (startIdx != -1) {
                    int endIdx = startIdx + key.length();
                    int finalStartIdx = startIdx;
                    ApplicationManager.getApplication().invokeLater(() -> {
                        try {
                            textArea.getHighlighter().addHighlight(finalStartIdx, endIdx, new DefaultHighlighter.DefaultHighlightPainter(JBColor.YELLOW));
                            textArea.revalidate();
                            textArea.repaint();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    startIdx = endIdx;
                }
            }
        }
    }

    private void highlightEditor(Editor editor, List<Connection> promptInputs) {
        for (Connection connection : promptInputs) {
            String key = connection.getOutputKey();
            if (key == null || key.isEmpty()) {
                continue;
            }
            int startIdx = 0;
            while (startIdx != -1) {
                startIdx = editor.getDocument().getText().indexOf(key, startIdx);
                if (startIdx != -1) {
                    int endIdx = startIdx + key.length();
                    int finalStartIdx = startIdx;
                    ApplicationManager.getApplication().invokeLater(() -> {
                        try {
                            editor.getMarkupModel().addRangeHighlighter(finalStartIdx, endIdx, HighlighterLayer.SELECTION - 1, new TextAttributes(null, JBColor.YELLOW, null, EffectType.BOXED, Font.PLAIN), HighlighterTargetArea.EXACT_RANGE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    startIdx = endIdx;
                }
            }
        }
    }
}