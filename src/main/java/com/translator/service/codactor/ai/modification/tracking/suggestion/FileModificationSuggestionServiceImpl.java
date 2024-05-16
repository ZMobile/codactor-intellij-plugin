package com.translator.service.codactor.ai.modification.tracking.suggestion;

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
import com.intellij.openapi.project.Project;
import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestion;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionRecord;
import com.translator.model.codactor.ai.modification.ModificationType;
import com.translator.service.codactor.ide.editor.GptToLanguageTransformerService;
import com.translator.service.codactor.ide.editor.GptToLanguageTransformerServiceImpl;
import com.translator.service.codactor.ide.editor.diff.DiffEditorGeneratorService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class FileModificationSuggestionServiceImpl implements FileModificationSuggestionService {
    private final Project project;
    private final DiffEditorGeneratorService diffEditorGeneratorService;
    private final FileModificationSuggestionExtensionGeneratorService fileModificationSuggestionExtensionGeneratorService;

    @Inject
    public FileModificationSuggestionServiceImpl(Project project,
                                                 DiffEditorGeneratorService diffEditorGeneratorService) {
        this.project = project;
        this.diffEditorGeneratorService = diffEditorGeneratorService;
        this.fileModificationSuggestionExtensionGeneratorService = new FileModificationSuggestionExtensionGeneratorServiceImpl();
    }

    public FileModificationSuggestion getFileModificationSuggestion(FileModification fileModification, String suggestionId) {
        return fileModification.getModificationOptions().stream()
                .filter(m -> m.getId().equals(suggestionId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void createFileModificationSuggestions(FileModification fileModification, List<FileModificationSuggestionRecord> modificationOptions) {
        List<FileModificationSuggestion> suggestions = new ArrayList<>();
        String extension = fileModificationSuggestionExtensionGeneratorService.generateExtension(fileModification);
        for (FileModificationSuggestionRecord modificationOption : modificationOptions) {
            ApplicationManager.getApplication().invokeLater(() -> {
                try {
                    EditorFactory editorFactory = EditorFactory.getInstance();
                    FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension(extension);
                    Document document = editorFactory.createDocument(modificationOption.getSuggestedCode());
                    Editor suggestedCodeEditor = editorFactory.createEditor(document, project, fileType, true);
                    Editor diffEditor = diffEditorGeneratorService.createDiffEditor(fileModification.getBeforeText(), modificationOption.getSuggestedCode());
                    EditorHighlighter editorHighlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(fileType, EditorColorsManager.getInstance().getGlobalScheme(), null);
                    ((EditorEx) diffEditor).setHighlighter(editorHighlighter);
                    FileModificationSuggestion fileModificationSuggestion = new FileModificationSuggestion(fileModification.getFilePath(), fileModification.getId(), modificationOption.getId(), fileModification.getBeforeText(), modificationOption.getSuggestedCodeBeforeRestoration(), modificationOption.getSuggestedCode(), diffEditor, suggestedCodeEditor);
                    suggestions.add(fileModificationSuggestion);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        fileModification.setModificationOptions(suggestions);
    }
}
