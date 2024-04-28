package com.translator.service.codactor.ai.chat.functions.search;

import com.translator.model.codactor.ide.file.search.SearchResponseResource;

public interface ProjectSearchService {
    SearchResponseResource search(String query);

    SearchResponseResource search(String query, int page, int pageSize);
}
