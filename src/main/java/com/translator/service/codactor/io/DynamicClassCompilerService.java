package com.translator.service.codactor.io;

import com.intellij.openapi.compiler.CompileStatusNotification;

import java.util.List;

public interface DynamicClassCompilerService {
    void dynamicallyCompileClass(String filePath);

    void dynamicallyCompileClass(String filePath, CompileStatusNotification compileStatusNotification);

    void dynamicallyRebuildAllClasses();

    void dynamicallyCompileFiles(List<String> filePaths, CompileStatusNotification compileStatusNotification);

    void dynamicallyCompileDirectory(String directoryPath, CompileStatusNotification compileStatusNotification);

    void dynamicallyRebuildAllClasses(CompileStatusNotification compileStatusNotification);
}
