package com.translator.service.codactor.modification;

import com.google.inject.Inject;
import com.translator.model.codactor.modification.FileModification;
import com.translator.model.codactor.modification.ModificationType;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;

public class FileModificationRestarterServiceImpl implements FileModificationRestarterService {
    private final FileModificationTrackerService fileModificationTrackerService;
    private final AutomaticCodeModificationService automaticCodeModificationService;

    @Inject
    public FileModificationRestarterServiceImpl(FileModificationTrackerService fileModificationTrackerService,
                                                AutomaticCodeModificationService automaticCodeModificationService) {
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.automaticCodeModificationService = automaticCodeModificationService;
    }

    @Override
    public void restartFileModification(FileModification fileModification) {
        if (fileModification != null) {
            if (fileModification.getModificationType() == ModificationType.MODIFY) {
                automaticCodeModificationService.getModifiedCode(fileModification.getFilePath(), fileModification.getModification(), fileModification.getModificationType(), fileModification.getPriorContext());
            } else if (fileModification.getModificationType() == ModificationType.MODIFY_SELECTION) {
                automaticCodeModificationService.getModifiedCode(fileModification.getFilePath(), fileModification.getRangeMarker().getStartOffset(), fileModification.getRangeMarker().getEndOffset(), fileModification.getModification(), fileModification.getModificationType(), fileModification.getPriorContext());
            } else if (fileModification.getModificationType() == ModificationType.FIX) {
                automaticCodeModificationService.getFixedCode(fileModification.getFilePath(), fileModification.getModification(), fileModification.getModificationType(), fileModification.getPriorContext());
            } else if (fileModification.getModificationType() == ModificationType.FIX_SELECTION) {
                automaticCodeModificationService.getFixedCode(fileModification.getFilePath(), fileModification.getRangeMarker().getStartOffset(), fileModification.getRangeMarker().getEndOffset(), fileModification.getModification(), fileModification.getModificationType(), fileModification.getPriorContext());
            } else if (fileModification.getModificationType() == ModificationType.CREATE) {
                automaticCodeModificationService.getCreatedCode(fileModification.getFilePath(), fileModification.getModification(), fileModification.getPriorContext());
            } else if (fileModification.getModificationType() == ModificationType.TRANSLATE) {
                automaticCodeModificationService.getTranslatedCode(fileModification.getFilePath(), fileModification.getNewLanguage(), fileModification.getNewFileType(), fileModification.getPriorContext());
            }
        }
    }
}
