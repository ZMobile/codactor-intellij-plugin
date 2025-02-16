package com.translator.service.codactor.io;

import com.intellij.openapi.compiler.CompileStatusNotification;

public interface DynamicClassCompilerService {
    void dynamicallyCompileClass(String filePath);

    void dynamicallyCompileClass(String filePath, CompileStatusNotification compileStatusNotification);

    void dynamicallyRebuildAllClasses();

    void dynamicallyCompileDirectory(String directoryPath, CompileStatusNotification compileStatusNotification);

    void dynamicallyRebuildAllClasses(CompileStatusNotification compileStatusNotification);
}
