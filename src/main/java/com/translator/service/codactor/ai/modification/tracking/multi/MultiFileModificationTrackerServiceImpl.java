package com.translator.service.codactor.ai.modification.tracking.multi;

import com.translator.model.codactor.ai.modification.MultiFileModification;

import java.util.ArrayList;
import java.util.List;

public class MultiFileModificationTrackerServiceImpl implements MultiFileModificationTrackerService {
    private final List<MultiFileModification> activeMultiFileModifications;

    public MultiFileModificationTrackerServiceImpl() {
        this.activeMultiFileModifications = new ArrayList<>();
    }
}
