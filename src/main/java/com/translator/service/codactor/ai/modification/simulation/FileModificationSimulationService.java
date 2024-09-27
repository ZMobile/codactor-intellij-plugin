package com.translator.service.codactor.ai.modification.simulation;

public interface FileModificationSimulationService {
    String simulateFileModification(String modificationId, String suggestedCode);
}
