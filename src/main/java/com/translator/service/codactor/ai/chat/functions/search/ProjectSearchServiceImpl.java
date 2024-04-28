package com.translator.service.codactor.ai.chat.functions.search;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.search.TextOccurenceProcessor;
import com.intellij.psi.search.UsageSearchContext;
import com.translator.model.codactor.ide.file.search.SearchResult;
import com.translator.model.codactor.ide.file.search.SearchResponseResource;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectSearchServiceImpl implements ProjectSearchService {

    private final Project project;

    @Inject
    public ProjectSearchServiceImpl(Project project) {
        this.project = project;
    }

    @Override
    public SearchResponseResource search(String query) {
        return search(query, 1, 10);
    }

    @Override
    public SearchResponseResource search(String query, int page, int pageSize) {
        PsiSearchHelper searchHelper = PsiSearchHelper.getInstance(project);

        List<PsiElement> foundElements = new ArrayList<>();

        TextOccurenceProcessor processor = new TextOccurenceProcessor() {
            @Override
            public boolean execute(@NotNull PsiElement element, int offsetInElement) {
                if (foundElements.size() >= page * pageSize) {
                    return false; // Stop the search
                }
                if (foundElements.size() >= (page - 1) * pageSize) { // Additional condition
                    foundElements.add(element);
                }
                return true; // Continue the search
            }
        };

        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        searchHelper.processElementsWithWord(processor, scope, query, UsageSearchContext.ANY, true);

        List<SearchResult> searchResults = new ArrayList<>();
        for (PsiElement element : foundElements) {
            ApplicationManager.getApplication().runReadAction(new Runnable() {
                @Override
                public void run() {
                    searchResults.add(new SearchResult(element.getContainingFile().getVirtualFile().getPath(), element.getText(), element.getTextRange()));
                }
            });
        }

        List<SearchResult> pageResultList = searchResults.stream()
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        int total = searchResults.size();

        return new SearchResponseResource(page, pageSize, total, pageResultList);
    }
}