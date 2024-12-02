package com.translator.service.codactor.ai.modification.test;

public interface FusedMethodHandlerService {
    String fixImproperlyFusedMethods(String beforeCode, String afterCode, String diffCode);
}
