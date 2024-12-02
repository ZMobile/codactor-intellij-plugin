package com.translator.service.codactor.ai.modification.test;

public class JavaClassManipulatorServiceImpl implements JavaClassManipulatorService {
    public String addMethodToEndOfClass(String classString, String methodString) {
        System.out.println("Method added to class: " + methodString);
        int lastBraceIndex = classString.lastIndexOf("}");
        if (lastBraceIndex == -1) {
            throw new IllegalArgumentException("Invalid class string: No closing brace found.");
        }
        // Insert the method before the last closing brace
        String beforeLastBrace = classString.substring(0, lastBraceIndex);
        String afterLastBrace = classString.substring(lastBraceIndex);
        return beforeLastBrace + "\n    " + methodString + "\n" + afterLastBrace;
    }
}
