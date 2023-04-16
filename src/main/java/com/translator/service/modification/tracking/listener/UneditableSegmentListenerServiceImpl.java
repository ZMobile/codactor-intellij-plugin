package com.translator.service.modification.tracking.listener;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.model.modification.FileModification;
import com.translator.model.modification.FileModificationSuggestion;
import com.translator.model.modification.FileModificationSuggestionModification;
import com.translator.service.modification.tracking.FileModificationTrackerService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class UneditableSegmentListenerServiceImpl implements UneditableSegmentListenerService {
    private FileModificationTrackerService fileModificationTrackerService;
    private Map<String, CompositeUneditableSegmentFilter> fileModificationCompositeFilterMap;
    private Map<String, CompositeUneditableSegmentFilter> fileModificationSuggestionCompositeFilterMap;
    @Inject
    public UneditableSegmentListenerServiceImpl(FileModificationTrackerService fileModificationTrackerService) {
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.fileModificationCompositeFilterMap = new HashMap<>();
        this.fileModificationSuggestionCompositeFilterMap = new HashMap<>();
    }

    @Override
    public void addUneditableFileModificationSegmentListener(String modificationId) {
        FileModification fileModification = fileModificationTrackerService.getModification(modificationId);
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(fileModification.getFilePath());
        if (virtualFile != null) {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

            if (document != null) {
                int startOffset = fileModification.getRangeMarker().getStartOffset();
                int endOffset = fileModification.getRangeMarker().getEndOffset();
                UneditableSegmentFilter filter = new UneditableSegmentFilter(startOffset, endOffset);

                String filePath = fileModification.getFilePath();
                CompositeUneditableSegmentFilter compositeFilter = fileModificationCompositeFilterMap.get(filePath);
                if (compositeFilter == null) {
                    compositeFilter = new CompositeUneditableSegmentFilter();
                    fileModificationCompositeFilterMap.put(filePath, compositeFilter);
                    document.addDocumentListener(compositeFilter);
                }

                compositeFilter.addUneditableSegmentFilter(filter);
            }
        }
    }

    @Override
    public void removeUneditableFileModificationSegmentListener(String modificationId) {
        FileModification fileModification = fileModificationTrackerService.getModification(modificationId);
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(fileModification.getFilePath());
        if (virtualFile != null) {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

            if (document != null) {
                String filePath = fileModification.getFilePath();
                CompositeUneditableSegmentFilter compositeFilter = fileModificationCompositeFilterMap.get(filePath);

                if (compositeFilter != null) {
                    int startOffset = fileModification.getRangeMarker().getStartOffset();
                    int endOffset = fileModification.getRangeMarker().getEndOffset();
                    UneditableSegmentFilter filter = new UneditableSegmentFilter(startOffset, endOffset);
                    compositeFilter.removeUneditableSegmentFilter(filter);
                }
            }
        }
    }

    @Override
    public void addUneditableFileModificationSuggestionModificationSegmentListener(String modificationSuggestionModificationId) {
        FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationTrackerService.getModificationSuggestionModification(modificationSuggestionModificationId);
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(fileModificationSuggestionModification.getSuggestionId());
        Document document = fileModificationSuggestion.getSuggestedCode().getDocument();
        int startOffset = fileModificationSuggestionModification.getRangeMarker().getStartOffset();
        int endOffset = fileModificationSuggestionModification.getRangeMarker().getEndOffset();
        UneditableSegmentFilter filter = new UneditableSegmentFilter(startOffset, endOffset);

        String suggestionId = fileModificationSuggestionModification.getSuggestionId();
        CompositeUneditableSegmentFilter compositeFilter = fileModificationSuggestionCompositeFilterMap.get(suggestionId);
        if (compositeFilter == null) {
            compositeFilter = new CompositeUneditableSegmentFilter();
            fileModificationSuggestionCompositeFilterMap.put(suggestionId, compositeFilter);
            document.addDocumentListener(compositeFilter);
        }
        compositeFilter.addUneditableSegmentFilter(filter);
    }

    @Override
    public void removeUneditableFileModificationSuggestionModificationSegmentListener(String modificationSuggestionModificationId) {
        FileModificationSuggestionModification fileModificationSuggestionModification = fileModificationTrackerService.getModificationSuggestionModification(modificationSuggestionModificationId);
        FileModificationSuggestion fileModificationSuggestion = fileModificationTrackerService.getModificationSuggestion(fileModificationSuggestionModification.getSuggestionId());
        Document document = fileModificationSuggestion.getSuggestedCode().getDocument();

        String suggestionId = fileModificationSuggestionModification.getSuggestionId();
        CompositeUneditableSegmentFilter compositeFilter = fileModificationSuggestionCompositeFilterMap.get(suggestionId);

        if (compositeFilter != null) {
            int startOffset = fileModificationSuggestionModification.getRangeMarker().getStartOffset();
            int endOffset = fileModificationSuggestionModification.getRangeMarker().getEndOffset();
            UneditableSegmentFilter filter = new UneditableSegmentFilter(startOffset, endOffset);
            compositeFilter.removeUneditableSegmentFilter(filter);
        }
    }
}