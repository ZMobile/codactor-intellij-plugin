package com.translator.view.codactor.viewer.inquiry;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.model.codactor.ai.chat.InquiryChatType;
import com.translator.service.codactor.ide.editor.GptToLanguageTransformerService;
import com.translator.service.codactor.ide.editor.GptToLanguageTransformerServiceImpl;
import com.translator.service.codactor.ai.chat.functions.CodactorFunctionToLabelMapperService;
import com.translator.service.codactor.ai.chat.functions.CodactorFunctionToLabelMapperServiceImpl;
import com.translator.view.codactor.panel.FixedHeightPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

public class InquiryChatViewer extends JPanel {

    public static class Builder {
        private InquiryChat inquiryChat;
        private List<InquiryChat> functionCalls;
        private String headerString;
        private String filePath;
        private String message;
        private String likelyCodingLanguage;
        private InquiryChatType inquiryChatType;

        public Builder withInquiryChat(InquiryChat inquiryChat) {
            this.inquiryChat = inquiryChat;
            return this;
        }

        public Builder withFunctionCalls(List<InquiryChat> functionCalls) {
            this.functionCalls = functionCalls;
            return this;
        }

        public Builder withHeaderString(String headerString) {
            this.headerString = headerString;
            return this;
        }

        public Builder withFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withLikelyCodingLanguage(String likelyCodingLanguage) {
            this.likelyCodingLanguage = likelyCodingLanguage;
            return this;
        }

        public Builder withInquiryChatType(InquiryChatType inquiryChatType) {
            this.inquiryChatType = inquiryChatType;
            return this;
        }

        public InquiryChatViewer build() {
            if (functionCalls == null) {
                functionCalls = new ArrayList<>();
            }

            if (likelyCodingLanguage == null) {
                GptToLanguageTransformerService gptToLanguageTransformerService1 = new GptToLanguageTransformerServiceImpl();
                if (filePath == null && message != null) {
                    likelyCodingLanguage = gptToLanguageTransformerService1.convert(message);
                } else if (filePath == null) {
                    likelyCodingLanguage = "text";
                } else {
                    likelyCodingLanguage = gptToLanguageTransformerService1.getFromFilePath(filePath);
                }
            }

            if (inquiryChat == null) {
                inquiryChat = new InquiryChat.Builder()
                        .withFilePath(filePath)
                        .withFrom(headerString)
                        .withMessage(message)
                        .withLikelyCodeLanguage(likelyCodingLanguage)
                        .withInquiryChatType(inquiryChatType)
                        .build();
            } else if (headerString == null) {
                headerString = inquiryChat.getFrom();
            }

            return new InquiryChatViewer(inquiryChat, headerString, functionCalls);
        }
    }

    private static final Pattern CODE_BLOCK_PATTERN = Pattern.compile("```(.*?)```", Pattern.DOTALL);

    private InquiryChat inquiryChat;
    private List<InquiryChat> functionCalls;
    private JToolBar jToolBar1;
    private JLabel jLabel1;
    private String headerString;
    private String message;
    private GptToLanguageTransformerService gptToLanguageTransformerService;
    private CodactorFunctionToLabelMapperService codactorFunctionToLabelMapperService;
    private List<Editor> editorList;

    private InquiryChatViewer(InquiryChat inquiryChat, String headerString, List<InquiryChat> functionCalls) {
        this.gptToLanguageTransformerService = new GptToLanguageTransformerServiceImpl();
        this.codactorFunctionToLabelMapperService = new CodactorFunctionToLabelMapperServiceImpl(new Gson());
        this.inquiryChat = inquiryChat;
        this.editorList = new ArrayList<>();
        this.headerString = headerString;
        this.functionCalls = functionCalls;
        setLayout(new GridBagLayout());
        if (inquiryChat.getInquiryChatType() == InquiryChatType.CODE_SNIPPET && !inquiryChat.getMessage().startsWith("```")) {
            inquiryChat.setMessage("```" + inquiryChat.getMessage().trim() + "```");
        }
        jToolBar1 = new JToolBar();
        jToolBar1.setBackground(Color.darkGray);
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
        ArrayList<InquiryChat> newFunctionCalls = new ArrayList<>(functionCalls);
        if ((inquiryChat.getFunctionCall() != null || inquiryChat.getFrom().equalsIgnoreCase("function"))
        && !newFunctionCalls.contains(inquiryChat)) {
            newFunctionCalls.add(inquiryChat);
        }
        FixedHeightPanel fixedHeightPanel = createFunctionCallsPanel(newFunctionCalls);
        addComponent(fixedHeightPanel, 1, 0);
        if (inquiryChat.getMessage() != null) {
            List<Component> components = createComponentsFromMessage(inquiryChat.getMessage());
            int gridy = 2;
            for (Component component : components) {
                addComponent(component, gridy++, 0);
            }
            if (inquiryChat.getLikelyCodeLanguage() == null) {
                inquiryChat.setLikelyCodeLanguage(gptToLanguageTransformerService.convert(inquiryChat.getMessage()));
            }
        }
    }

    private List<Component> createComponentsFromMessage(String message) {
        List<Component> components = new ArrayList<>();
        Matcher matcher = CODE_BLOCK_PATTERN.matcher(message);

        int lastIndex = 0;
        while (matcher.find()) {
            String plainText = message.substring(lastIndex, matcher.start()).trim();
            if (!plainText.isEmpty() && !plainText.equals("null")) {
                components.add(createPlainTextComponent(plainText));
            }

            String codeSnippet = matcher.group(1).trim();
            if (!codeSnippet.isEmpty()) {
                components.add(createCodeEditor(codeSnippet, inquiryChat.getFilePath()));
            }

            lastIndex = matcher.end();
        }

        String remainingPlainText = message.substring(lastIndex).trim();
        if (!remainingPlainText.isEmpty() && !remainingPlainText.equals("null")) {
            components.add(createPlainTextComponent(remainingPlainText));
        }

        return components;
    }

    /*private Component createPlainTextComponent(String text) {
        JBTextArea textArea = new JBTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setSize(new Dimension((int)textArea.getPreferredSize().getWidth(), Integer.MAX_VALUE));
        textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        textArea.addComponentListener(new ComponentAdapter() {
        });

        return textArea;
    }*/
    private Component createPlainTextComponent(String text) {
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        String htmlText = "<html><body style='font-family: sans-serif;'>"
                + escapeHtml(text).replace("\n", "<br>")
                + "</body></html>";
        textPane.setText(htmlText);
        textPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        return textPane;
    }


    private FixedHeightPanel createFunctionCallsPanel(List<InquiryChat> functionCalls) {
        // Add the function call names as vertical list
        int height = 0;
        List<JLabel> functionCallLabels = new ArrayList<>();
        for (InquiryChat functionCall : functionCalls) {
            JLabel functionCallLabel = new JLabel(codactorFunctionToLabelMapperService.getLabel(functionCall));
            functionCallLabels.add(functionCallLabel);
            height += functionCallLabel.getPreferredSize().getHeight();
        }
        FixedHeightPanel functionCallsPanel = new FixedHeightPanel(height);
        for (JLabel functionCallLabel : functionCallLabels) {
            functionCallsPanel.add(functionCallLabel);
        }
        functionCallsPanel.setLayout(new GridBagLayout());
        //functionCallsPanel.setBackground(JBColor.WHITE);
        return functionCallsPanel;
    }

    private FixedHeightPanel createCodeEditor(String code, String filePath) {
        final FixedHeightPanel[] fixedHeightPanel = new FixedHeightPanel[1];
        EditorFactory editorFactory = EditorFactory.getInstance();
        String extension = null;
        if (inquiryChat.getLikelyCodeLanguage() == null
        || Objects.equals(inquiryChat.getLikelyCodeLanguage(), "txt")
        || Objects.equals(inquiryChat.getLikelyCodeLanguage(), "text")) {
            String[] words = code.split(" ");
            String word = words[0];
            String firstWordInCode = word.split("\n")[0].trim();
            extension = gptToLanguageTransformerService.getExtensionFromLanguage(firstWordInCode.toLowerCase());
            if (extension == null) {
                inquiryChat.setLikelyCodeLanguage("text");
            } else {
                inquiryChat.setLikelyCodeLanguage(firstWordInCode);
            }
        } else {
            extension = gptToLanguageTransformerService.getExtensionFromLanguage(inquiryChat.getLikelyCodeLanguage());
        }
        if (extension == null) {
            extension = "txt";
        }
        if (filePath != null) {
            extension = filePath.substring(filePath.lastIndexOf(".") + 1);
        }
        FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension(extension);
        ApplicationManager.getApplication().invokeAndWait(() -> {
            Document document = editorFactory.createDocument(code);
            Editor editor = editorFactory.createEditor(document, null);
            editorList.add(editor);
            EditorHighlighter editorHighlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(fileType, EditorColorsManager.getInstance().getGlobalScheme(), null);
            ((EditorEx) editor).setHighlighter(editorHighlighter);
            ((EditorEx) editor).setViewer(true);
            editor.getComponent().setPreferredSize(new Dimension(Integer.MAX_VALUE, editor.getComponent().getPreferredSize().height));
            fixedHeightPanel[0] = new FixedHeightPanel(editor);
            fixedHeightPanel[0].setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            fixedHeightPanel[0].add(editor.getComponent());
        });
        return fixedHeightPanel[0];
    }

    public int getLineCount(String code) {
        String[] lines = code.split("\\r?\\n");
        return lines.length;
    }

    public InquiryChat getInquiryChat() {
        return inquiryChat;
    }

    private void addComponent(Component component, int gridy, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.weightx = 1.0;
        gbc.weighty = weighty;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        ApplicationManager.getApplication().invokeAndWait(() -> {
            try {
                add(component, gbc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void componentResized() {
        for (Component component : getComponents()) {
            if (component instanceof FixedHeightPanel) {
                FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                if (fixedHeightPanel.getEditor() != null) {
                    Editor editor = (Editor) fixedHeightPanel.getComponent(0);
                    editor.getComponent().setSize(new Dimension(fixedHeightPanel.getWidth(), fixedHeightPanel.getHeight()));
                }
            }
        }
    }

    public void dispose() {
        for (Editor editor : editorList) {
            EditorFactory.getInstance().releaseEditor(editor);
        }
    }

    public void setFunctionCalls(List<InquiryChat> functionCalls) {
        this.functionCalls = functionCalls;
    }

    public List<InquiryChat> getFunctionCalls() {
        return functionCalls;
    }
}
