package com.translator.view.codactor.dialog.modification;

import com.translator.model.codactor.ai.modification.FileModification;
import com.translator.model.codactor.ai.modification.FileModificationSuggestion;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.view.codactor.factory.dialog.ProvisionalModificationCustomizerDialogFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProvisionalModificationCustomizerDialogManager {
    private final ProvisionalModificationCustomizerDialogFactory provisionalModificationCustomizerDialogFactory;
    private final Map<String, List<ProvisionalModificationCustomizerDialog>> provisionalModificationCustomizerMap;

    @Inject
    public ProvisionalModificationCustomizerDialogManager(ProvisionalModificationCustomizerDialogFactory provisionalModificationCustomizerDialogFactory,
                                                          FileModificationTrackerService fileModificationTrackerService) {
        this.provisionalModificationCustomizerDialogFactory = provisionalModificationCustomizerDialogFactory;
        this.provisionalModificationCustomizerMap = new HashMap<>();
        fileModificationTrackerService.addModificationImplementedListener(this::disposeProvisionalModificationCustomizers);
        fileModificationTrackerService.addModificationRemovedListener(this::disposeProvisionalModificationCustomizers);
    }

    public void addProvisionalModificationCustomizerDialog(FileModificationSuggestion fileModificationSuggestion) {
        ProvisionalModificationCustomizerDialog provisionalModificationCustomizerDialog = provisionalModificationCustomizerDialogFactory.create(fileModificationSuggestion);
        provisionalModificationCustomizerDialog.setVisible(true);
        List<ProvisionalModificationCustomizerDialog> provisionalModificationCustomizerDialogList = provisionalModificationCustomizerMap.get(provisionalModificationCustomizerDialog.getFileModificationSuggestion().getId());
        if (provisionalModificationCustomizerDialogList == null) {
            provisionalModificationCustomizerDialogList = new ArrayList<>();
        }
        provisionalModificationCustomizerDialogList.add(provisionalModificationCustomizerDialog);
        provisionalModificationCustomizerMap.put(fileModificationSuggestion.getId(), provisionalModificationCustomizerDialogList);
    }

    public void disposeProvisionalModificationCustomizers(FileModification fileModification) {
        for (FileModificationSuggestion fileModificationSuggestion : fileModification.getModificationOptions()) {
            List<ProvisionalModificationCustomizerDialog> provisionalModificationCustomizerDialogList = provisionalModificationCustomizerMap.get(fileModificationSuggestion.getId());
            if (provisionalModificationCustomizerDialogList != null) {
                for (ProvisionalModificationCustomizerDialog provisionalModificationCustomizerDialog : provisionalModificationCustomizerDialogList) {
                    provisionalModificationCustomizerDialog.dispose();
                }
            }
            provisionalModificationCustomizerMap.remove(fileModificationSuggestion.getId());
        }
    }
}
