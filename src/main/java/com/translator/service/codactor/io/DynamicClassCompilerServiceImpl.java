package com.translator.service.codactor.io;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompileStatusNotification;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.inject.Inject;
import java.io.File;

public class DynamicClassCompilerServiceImpl implements DynamicClassCompilerService {
    private final Project project;
    private final RelevantBuildOutputLocatorService relevantBuildOutputLocatorService;

    @Inject
    public DynamicClassCompilerServiceImpl(Project project,
                                           RelevantBuildOutputLocatorService relevantBuildOutputLocatorService) {
        this.project = project;
        this.relevantBuildOutputLocatorService = relevantBuildOutputLocatorService;
    }

    @Override
    public void dynamicallyCompileClass(String filePath) {
        // Set up a callback to handle the result of the compilation
        CompileStatusNotification callback = (aborted, errors, warnings, compileContext) -> {
            if (aborted) {
                System.out.println("Compilation aborted.");
            } else if (errors > 0) {
                System.out.println("Compilation finished with errors.");
            } else {
                System.out.println("Compilation completed successfully with " + warnings + " warnings.");
            }
        };
        dynamicallyCompileClass(filePath, callback);
    }

    @Override
    public void dynamicallyCompileClass(String filePath, CompileStatusNotification compileStatusNotification) {
        String isolatedClassName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));

        String buildOutputParentDirectoryPath = relevantBuildOutputLocatorService.locateRelevantBuildOutput(filePath);
        String buildOutputPath = buildOutputParentDirectoryPath + "/" + isolatedClassName + ".class";
        File buildOutputFile = new File(buildOutputPath);
        System.out.println("Build output file exists: " + buildOutputFile.exists());
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(new File(filePath));

        if (virtualFile == null) {
            System.out.println("Error: File not found: " + filePath);
            return;
        }

        // Use the CompilerManager to compile the specific file
        CompilerManager compilerManager = CompilerManager.getInstance(project);
        //ApplicationManager.getApplication().invokeLater(() -> {
            compilerManager.compile(new VirtualFile[]{virtualFile}, compileStatusNotification);
        //});
    }
}
