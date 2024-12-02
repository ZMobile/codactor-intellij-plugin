package com.translator.service.codactor.ai.modification.test;

import java.io.IOException;

public interface StringCodeOmittedRestorerService {
    //String restoreOmittedCode(String originalCode, String modifiedCode) throws IOException;
    String restoreOmittedString(String beforeCode, String afterCode);

    String restoreOmittedString(String diffCode);

    boolean containsOmittedString(String diffCode);

    boolean containsOmittedStringWithoutDiffMarkers(String diffCode);
}