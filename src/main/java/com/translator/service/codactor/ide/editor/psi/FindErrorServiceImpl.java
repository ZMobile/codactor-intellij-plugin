package com.translator.service.codactor.ide.editor.psi;

import com.google.inject.Inject;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.translator.model.codactor.ide.psi.error.ErrorResult;
import com.translator.service.codactor.ide.editor.CodeSnippetIndexGetterService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class FindErrorServiceImpl implements FindErrorService {
    private final Project project;
    private final PsiFileService psiFileService;
    private final CodeSnippetIndexGetterService codeSnippetIndexGetterService;
    private final PsiElementCollectorService psiElementCollectorService;

    @Inject
    public FindErrorServiceImpl(Project project,
                                PsiFileService psiFileService,
                                CodeSnippetIndexGetterService codeSnippetIndexGetterService,
                                PsiElementCollectorService psiElementCollectorService) {
        this.project = project;
        this.psiFileService = psiFileService;
        this.codeSnippetIndexGetterService = codeSnippetIndexGetterService;
        this.psiElementCollectorService = psiElementCollectorService;
    }

    public List<ErrorResult> getErrorsWithinRange(String filePath, int startOffset, int endOffset, boolean includeWarnings) {
        List<ErrorResult> results = new ArrayList<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            AtomicReference<PsiFile> psiFile = new AtomicReference<>(psiFileService.getPsiFileFromPath(filePath));
            ApplicationManager.getApplication().invokeAndWait(() -> psiFile.set(psiFileService.getPsiFileFromPath(filePath)));

            Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile.get());
            if (document != null) {
                MarkupModel markupModel = DocumentMarkupModel.forDocument(document, project, true);
                for (RangeHighlighter highlighter : markupModel.getAllHighlighters()) {
                    if (!(highlighter.getEndOffset() < startOffset || highlighter.getStartOffset() > endOffset)) {
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
            }
        });

        return results;
    }

    public List<ErrorResult> getAllErrorsInFile(String filePath, boolean includeWarnings) {
        List<ErrorResult> results = new ArrayList<>();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            AtomicReference<PsiFile> psiFile = new AtomicReference<>(psiFileService.getPsiFileFromPath(filePath));
            ApplicationManager.getApplication().invokeAndWait(() -> psiFile.set(psiFileService.getPsiFileFromPath(filePath)));

            Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile.get());
            if (document != null) {
                MarkupModel markupModel = DocumentMarkupModel.forDocument(document, project, true);
                for (RangeHighlighter highlighter : markupModel.getAllHighlighters()) {
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
        });

        return results;
    }

    @Override
    public List<ErrorResult> getErrorsWithinRange(String filePath, String codeSnippet, boolean includeWarnings) {
        AtomicInteger startOffset = new AtomicInteger();
        AtomicInteger endOffset = new AtomicInteger();
        ApplicationManager.getApplication().invokeAndWait(() -> {
            startOffset.set(codeSnippetIndexGetterService.getStartIndexInFilePath(filePath, codeSnippet));
            endOffset.set(codeSnippetIndexGetterService.getEndIndexInFilePath(filePath, codeSnippet));
        });
        return getErrorsWithinRange(filePath, startOffset.get(), endOffset.get(), includeWarnings);
    }

    public List<ErrorResult> findErrorReferences(PsiClass targetClass) {
        // Get references in other classes to target class
        List<ErrorResult> errors = new ArrayList<>();
        Collection<PsiReference> references = psiElementCollectorService.findClassReferences(targetClass);

        // Iterate over the references
        for (PsiReference reference : references) {
            PsiElement referencingElement = reference.getElement();
            // Now we need to check for potential errors in the referencing element caused by changes in the target class
            // This would typically include type checking, method existence checks and similar
            // These exact checks could be very varied and depend on the specific change in the target class

            // the specific requirements in the real use case


            // Get the containing file for the referencing element
            PsiFile referencingFile = referencingElement.getContainingFile();

            // Get the text offset for the referencing element
            int startOffset = referencingElement.getTextOffset();
            int endOffset = startOffset + referencingElement.getTextLength();

            // Check for errors within the range
            errors.addAll(getErrorsWithinRange(referencingFile.getVirtualFile().getCanonicalPath(), startOffset, endOffset, true));
        }
        return errors;
    }
}
