package com.translator.service.codactor.modification;

import com.translator.model.codactor.modification.FileModification;

public interface FileModificationRestarterService {
    void restartFileModification(FileModification fileModification);
}
