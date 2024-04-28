package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.google.rpc.Code;
import com.translator.model.codactor.ide.file.search.SearchResponseResource;
import com.translator.model.codactor.ide.file.search.SearchResult;
import com.translator.service.codactor.ai.chat.functions.search.ProjectSearchService;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.ide.editor.CodeSnippetIndexGetterService;

import javax.inject.Inject;
import java.util.List;
import java.util.ArrayList;

public class MavenAndGradleDependencyCollectorServiceImpl implements MavenAndGradleDependencyCollectorService {
    private ProjectSearchService projectSearchService;
    private CodeSnippetExtractorService codeSnippetExtractorService;
    private CodeSnippetIndexGetterService codeSnippetIndexGetterService;
    private int pageSize;

    @Inject
    public MavenAndGradleDependencyCollectorServiceImpl(ProjectSearchService projectSearchService,
                                                        CodeSnippetExtractorService codeSnippetExtractorService,
                                                        CodeSnippetIndexGetterService codeSnippetIndexGetterService) {
        this.projectSearchService = projectSearchService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.codeSnippetIndexGetterService = codeSnippetIndexGetterService;
        this.pageSize = 10;
    }

    @Override
    public List<String> collectProjectTestDependencies() {
        String[] searchKeywords = new String[]{"<scope>test</scope>", "testImplementation"};
        List<String> dependencies = new ArrayList<>();

        for (String keyword : searchKeywords) {
            int pageCount = getNumberOfPages(keyword);

            for (int i = 0; i < pageCount; i++) {
                collectDependenciesForKeyword(dependencies, keyword, i+1);
            }
        }

        return dependencies;
    }

    private int getNumberOfPages(String keyword) {
        SearchResponseResource searchResponseResource = projectSearchService.search(keyword, 1, pageSize);
        int totalResults = searchResponseResource.getTotalResults();
        return totalResults / pageSize + (totalResults % pageSize == 0 ? 0 : 1);
    }

    private void collectDependenciesForKeyword(List<String> dependencies, String keyword, int pageIndex) {
        SearchResponseResource searchResponseResource = projectSearchService.search(keyword, pageIndex, pageSize);
        List<SearchResult> searchResults = searchResponseResource.getSearchResults();

        for (SearchResult searchResult : searchResults) {
            getDependencySnippet(dependencies, searchResult, keyword);
        }
    }

    private void getDependencySnippet(List<String> dependencies, SearchResult searchResult, String keyword) {
        if (keyword.equals("testImplementation")) {
            getGradleDependencySnippet(dependencies, searchResult);
        } else {
            getMavenDependencySnippet(dependencies, searchResult);
        }
    }

    private void getGradleDependencySnippet(List<String> dependencies, SearchResult searchResult) {
        dependencies.add(codeSnippetExtractorService.getCurrentLineCodeAtIndex(searchResult.getFilePath(), searchResult.getTextRange().getStartOffset()));
    }

    private void getMavenDependencySnippet(List<String> dependencies, SearchResult searchResult) {
        String dependencyStart = "<dependency>";
        String content = codeSnippetExtractorService.getAllText(searchResult.getFilePath());
        int dependencyStartIndex = codeSnippetIndexGetterService.getStartIndexBeforeEndIndex(content, dependencyStart, searchResult.getTextRange().getStartOffset());
        String dependencyEnd = "</dependency>";
        int dependencyEndIndex = codeSnippetIndexGetterService.getEndIndexAfterStartIndex(searchResult.getFilePath(), searchResult.getTextRange().getStartOffset(), dependencyEnd);
        dependencies.add(codeSnippetExtractorService.getSnippet(searchResult.getFilePath(), dependencyStartIndex, dependencyEndIndex));
    }
}