package com.translator.view.viewer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.translator.model.inquiry.InquiryChat;
import com.translator.model.inquiry.InquiryChatType;
import com.intellij.ui.components.JBTextArea;
import com.translator.service.code.CodeToFileTypeTransformerService;
import com.translator.service.code.CodeToFileTypeTransformerServiceImpl;
import com.translator.view.panel.FixedHeightPanel;

import javax.swing.*;
import javax.swing.text.PlainView;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InquiryChatViewer extends JPanel {
    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```(.*?)```", Pattern.DOTALL);

    private InquiryChat inquiryChat;
    private InquiryChatType inquiryChatType;
    private JToolBar jToolBar1;
    private JLabel jLabel1;
    private String message;
    private CodeToFileTypeTransformerService codeToFileTypeTransformerService;

    public InquiryChatViewer(String message, String headerString, InquiryChatType inquiryChatType) {
        this.codeToFileTypeTransformerService = new CodeToFileTypeTransformerServiceImpl();
        this.message = message;
        this.inquiryChat = new InquiryChat(null, null, null, null, headerString, message, inquiryChatType);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        jToolBar1 = new JToolBar();
        jToolBar1.setFloatable(false);
        jToolBar1.setBorderPainted(false);
        jLabel1 = new JLabel();
        jToolBar1.setRollover(true);
        if (inquiryChat.getAlternateInquiryChatIds() != null && inquiryChat.getAlternateInquiryChatIds().size() > 1) {
            int numerator = inquiryChat.getAlternateInquiryChatIds().indexOf(inquiryChat.getId()) + 1;
            int denominator = inquiryChat.getAlternateInquiryChatIds().size();
            if (numerator == 0) {
                denominator++;
                numerator = denominator;
            }
            headerString += " (" + numerator + "/" + denominator + ")";
        }
        jLabel1.setText(headerString);
        jToolBar1.add(jLabel1);
        addComponent(jToolBar1, 0, 0);

        List<Component> components = createComponentsFromMessage(message);
        int gridy = 1;
        for (Component component : components) {
            addComponent(component, gridy++, 0);
        }
    }

    public InquiryChatViewer(InquiryChat inquiryChat, String headerString) {
        this.inquiryChat = inquiryChat;
        this.inquiryChatType = inquiryChat.getInquiryChatType();
        setLayout(new GridBagLayout());

        jToolBar1 = new JToolBar();
        jToolBar1.setFloatable(false);
        jToolBar1.setBorderPainted(false);
        jLabel1 = new JLabel();
        jToolBar1.setRollover(true);
        if (inquiryChat.getAlternateInquiryChatIds() != null && inquiryChat.getAlternateInquiryChatIds().size() > 1) {
            int numerator = inquiryChat.getAlternateInquiryChatIds().indexOf(inquiryChat.getId()) + 1;
            int denominator = inquiryChat.getAlternateInquiryChatIds().size();
            if (numerator == 0) {
                denominator++;
                numerator = denominator;
            }
            headerString += " (" + numerator + "/" + denominator + ")";
        }
        jLabel1.setText(headerString);
        jToolBar1.add(jLabel1);
        addComponent(jToolBar1, 0, 0);

        List<Component> components = createComponentsFromMessage(inquiryChat.getMessage());
        int gridy = 1;
        for (Component component : components) {
            addComponent(component, gridy++, 0);
        }
    }

    public InquiryChatViewer(InquiryChat inquiryChat) {
        this(inquiryChat, inquiryChat.getFrom());
    }

    private List<Component> createComponentsFromMessage(String message) {
        List<Component> components = new ArrayList<>();
        Matcher matcher = CODE_BLOCK_PATTERN.matcher(message);

        int lastIndex = 0;
        while (matcher.find()) {
            String plainText = message.substring(lastIndex, matcher.start()).trim();
            if (!plainText.isEmpty()) {
                components.add(createPlainTextComponent(plainText));
            }

            String codeSnippet = matcher.group(1).trim();
            if (!codeSnippet.isEmpty()) {
                components.add(createCodeEditor(codeSnippet));
            }

            lastIndex = matcher.end();
        }

        String remainingPlainText = message.substring(lastIndex).trim();
        if (!remainingPlainText.isEmpty()) {
            components.add(createPlainTextComponent(remainingPlainText));
        }

        return components;
    }

    private Component createPlainTextComponent(String text) {
        JBTextArea textArea = new JBTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setSize(new Dimension((int)textArea.getPreferredSize().getWidth(), Integer.MAX_VALUE));
        textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        textArea.addComponentListener(new ComponentAdapter() {
        });

        return textArea;
    }

    private FixedHeightPanel createCodeEditor(String code) {
        EditorFactory editorFactory = EditorFactory.getInstance();
        FileType fileType = codeToFileTypeTransformerService.convert(code);
        Document document = editorFactory.createDocument(code);
        Editor editor = editorFactory.createEditor(document, null);
        EditorHighlighter editorHighlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(fileType, EditorColorsManager.getInstance().getGlobalScheme(), null);
        ((EditorEx) editor).setHighlighter(editorHighlighter);
        ((EditorEx) editor).setViewer(true);
        editor.getComponent().setPreferredSize(new Dimension(Integer.MAX_VALUE, editor.getComponent().getPreferredSize().height));
        FixedHeightPanel fixedHeightPanel = new FixedHeightPanel(editor);
        fixedHeightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fixedHeightPanel.add(editor.getComponent());
        return fixedHeightPanel;
    }

    public int getLineCount(String code) {
        String[] lines = code.split("\\r?\\n");
        return lines.length;
    }

    public InquiryChat getInquiryChat() {
        return inquiryChat;
    }

    public InquiryChatType getInquiryChatType() {
        return inquiryChatType;
    }

    private void addComponent(Component component, int gridy, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.weightx = 1.0;
        gbc.weighty = weighty;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(component, gbc);
    }

    public void componentResized() {
        for (Component component : getComponents()) {
            if (component instanceof FixedHeightPanel) {
                FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                Editor editor = (Editor) fixedHeightPanel.getComponent(0);
                editor.getComponent().setSize(new Dimension(fixedHeightPanel.getWidth(), fixedHeightPanel.getHeight()));
            }
        }
    }
}
