package com.translator.model.codactor.ide.file.search;

import java.util.List;

public class SearchResponseResource {
    private int page;
    private int pageSize;
    private int totalResults;
    private List<SearchResult> searchResults;

    public SearchResponseResource(int page, int pageSize, int totalResults, List<SearchResult> searchResults) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalResults = totalResults;
        this.searchResults = searchResults;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }
}
