package com.translator.service.codactor.editor.psi;

import com.google.inject.Inject;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoFilter;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.translator.model.codactor.editor.psi.ErrorResult;
import com.translator.service.codactor.editor.CodeSnippetIndexGetterService;

import java.util.ArrayList;
import java.util.List;

public class FindErrorServiceImpl implements FindErrorService {
    private final Project project;
    private final PsiFileService psiFileService;
    private final CodeSnippetIndexGetterService codeSnippetIndexGetterService;

    @Inject
    public FindErrorServiceImpl(Project project,
                                PsiFileService psiFileService,
                                CodeSnippetIndexGetterService codeSnippetIndexGetterService) {
        this.project = project;
        this.psiFileService = psiFileService;
        this.codeSnippetIndexGetterService = codeSnippetIndexGetterService;
    }

    public List<ErrorResult> getErrorsWithinRange(String filePath, int startOffset, int endOffset, boolean includeWarnings) {
        PsiFile psiFile = psiFileService.getPsiFileFromPath(filePath);
        List<ErrorResult> results = new ArrayList<>();

        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        if (document == null) return results;

        MarkupModel markupModel = DocumentMarkupModel.forDocument(document, project, true);
        for (RangeHighlighter highlighter : markupModel.getAllHighlighters()) {
            if (highlighter.getStartOffset() >= startOffset && highlighter.getEndOffset() <= endOffset) {
                Object tooltip = highlighter.getErrorStripeTooltip();
                if (tooltip instanceof HighlightInfo) {
                    HighlightInfo info = (HighlightInfo) tooltip;
                    if (info.getSeverity() == HighlightSeverity.ERROR ||
                            (includeWarnings && info.getSeverity() == HighlightSeverity.WARNING)) {
                        results.add(new ErrorResult(info.getDescription(), info.getDescription()));
                    }
                }
            }
        }

        return results;
    }

    @Override
    public List<ErrorResult> getErrorsWithinRange(String filePath, String codeSnippet, boolean includeWarnings) {
        int startOffset = codeSnippetIndexGetterService.getStartIndexInFilePath(filePath, codeSnippet);
        int endOffset = codeSnippetIndexGetterService.getEndIndexInFilePath(filePath, codeSnippet);
        return getErrorsWithinRange(filePath, startOffset, endOffset, includeWarnings);
    }
}
