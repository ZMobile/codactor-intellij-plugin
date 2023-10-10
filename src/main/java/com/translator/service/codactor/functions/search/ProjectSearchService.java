package com.translator.service.codactor.functions.search;

import com.translator.model.codactor.functions.search.SearchResponseResource;
import com.translator.model.codactor.functions.search.SearchResult;

import java.util.List;

public interface ProjectSearchService {
    SearchResponseResource search(String query);

    SearchResponseResource search(String query, int page, int pageSize);
}
