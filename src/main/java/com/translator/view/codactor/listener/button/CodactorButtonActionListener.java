package com.translator.view.codactor.listener.button;

import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBTextArea;
import com.translator.model.codactor.file.FileItem;
import com.translator.model.codactor.modification.ModificationType;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.inquiry.InquiryService;
import com.translator.service.codactor.modification.AutomaticCodeModificationService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.dialog.MultiFileCreateDialog;
import com.translator.view.codactor.factory.InquiryViewerFactory;
import com.translator.view.codactor.factory.dialog.MultiFileCreateDialogFactory;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;

import java.awt.event.ActionEvent;

public class CodactorButtonActionListener {
    /*private final JBTextArea textArea;
    private final ComboBox<FileItem> fileComboBox;
    private final ComboBox<String> modificationTypeComboBox;
    private final CodeSnippetExtractorService codeSnippetExtractorService;
    private final CodactorToolWindowService codactorToolWindowService;
    private final AutomaticCodeModificationService automaticCodeModificationService;
    private final PromptContextService promptContextService;
    private final InquiryService inquiryService;
    private final InquiryViewerFactory inquiryViewerFactory;
    private final MultiFileCreateDialogFactory multiFileCreateDialogFactory;

    @Override
    public void actionPerformed(ActionEvent e) {
        FileItem fileItem = (FileItem) fileComboBox.getSelectedItem();
        assert fileItem != null;
        assert modificationTypeComboBox.getSelectedItem() != null;
        if (modificationTypeComboBox.getSelectedItem().toString().equals("Modify")) {
            String code = codeSnippetExtractorService.getAllText(fileItem.getFilePath());
            if (!code.isEmpty() && !textArea.getText().isEmpty()) {
                codactorToolWindowService.openModificationQueueViewerToolWindow();
                automaticCodeModificationService.getModifiedCode(fileItem.getFilePath(), textArea.getText(), ModificationType.MODIFY, promptContextService.getPromptContext());
                promptContextService.clearPromptContext();
            }
        } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Modify Selected")) {
            SelectionModel selectionModel = codeSnippetExtractorService.getSelectedText(fileItem.getFilePath());
            String code = null;
            if (selectionModel != null) {
                code = selectionModel.getSelectedText();
            }
            if (code != null && !code.isEmpty() && !textArea.getText().isEmpty()) {
                codactorToolWindowService.openModificationQueueViewerToolWindow();
                automaticCodeModificationService.getModifiedCode(fileItem.getFilePath(), selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), textArea.getText(), ModificationType.MODIFY_SELECTION, promptContextService.getPromptContext());
                promptContextService.clearPromptContext();
            }
        } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Fix")) {
            String code = codeSnippetExtractorService.getAllText(fileItem.getFilePath());
            if (!code.isEmpty() && !textArea.getText().isEmpty()) {
                codactorToolWindowService.openModificationQueueViewerToolWindow();
                automaticCodeModificationService.getFixedCode(fileItem.getFilePath(), textArea.getText(), ModificationType.FIX, promptContextService.getPromptContext());
                promptContextService.clearPromptContext();
            }
        } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Fix Selected")) {
            codactorToolWindowService.openModificationQueueViewerToolWindow();
            SelectionModel selectionModel = codeSnippetExtractorService.getSelectedText(fileItem.getFilePath());
            String code = null;
            if (selectionModel != null) {
                code = selectionModel.getSelectedText();
            }
            if (code != null && !code.isEmpty() && !textArea.getText().isEmpty()) {
                codactorToolWindowService.openModificationQueueViewerToolWindow();
                automaticCodeModificationService.getFixedCode(fileItem.getFilePath(), selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), textArea.getText(), ModificationType.FIX_SELECTION, promptContextService.getPromptContext());
                promptContextService.clearPromptContext();
            }
        } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Create")) {
            if (!textArea.getText().isEmpty()) {
                codactorToolWindowService.openModificationQueueViewerToolWindow();
                automaticCodeModificationService.getCreatedCode(fileItem.getFilePath(), textArea.getText(), promptContextService.getPromptContext());
                promptContextService.clearPromptContext();
            }
        } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Create Files")) {
            if (!textArea.getText().isEmpty()) {
                MultiFileCreateDialog multiFileCreateDialog = multiFileCreateDialogFactory.create(null, textArea.getText(), promptContextService, openAiModelService);
                multiFileCreateDialog.setVisible(true);
                promptContextService.clearPromptContext();
            }
        } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Inquire")) {
            if (!textArea.getText().isEmpty()) {
                String code = codeSnippetExtractorService.getAllText(fileItem.getFilePath());
                String question = textArea.getText();
                InquiryViewer inquiryViewer = inquiryViewerFactory.create();
                inquiryViewer = inquiryService.createInquiry(inquiryViewer, fileItem.getFilePath(), code, question, promptContextService.getPromptContext(), openAiModelService.getSelectedOpenAiModel());
                codactorToolWindowService.createInquiryViewerToolWindow(inquiryViewer);
                promptContextService.clearPromptContext();
            }
        } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Inquire Selected")) {
            SelectionModel selectionModel = codeSnippetExtractorService.getSelectedText(fileItem.getFilePath());
            String code = null;
            if (selectionModel != null) {
                code = selectionModel.getSelectedText();
            }
            String question = textArea.getText();
            InquiryViewer inquiryViewer = inquiryViewerFactory.create();
            inquiryService.createInquiry(inquiryViewer, fileItem.getFilePath(), code, question, promptContextService.getPromptContext(), openAiModelService.getSelectedOpenAiModel());
            promptContextService.clearPromptContext();
        } else if (modificationTypeComboBox.getSelectedItem().toString().equals("Translate")) {
            codactorToolWindowService.openModificationQueueViewerToolWindow();
            automaticCodeModificationService.getTranslatedCode(fileItem.getFilePath(), languageInputTextField.getText(), fileTypeTextField.getText(), promptContextService.getPromptContext());
            promptContextService.clearPromptContext();
        }
    }*/
}
