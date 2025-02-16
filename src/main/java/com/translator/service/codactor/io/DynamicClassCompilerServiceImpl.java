package com.translator.service.codactor.io;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompileStatusNotification;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public void dynamicallyCompileDirectory(String directoryPath, CompileStatusNotification compileStatusNotification) {
        VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(directoryPath));

        if (directory == null || !directory.isDirectory()) {
            System.out.println("Error: Directory not found: " + directoryPath);
            return;
        }

        List<VirtualFile> javaFiles = Arrays.stream(directory.getChildren())
                .filter(file -> file.getName().endsWith(".java"))
                .collect(Collectors.toList());


        if (javaFiles.isEmpty()) {
            System.out.println("No Java files found in directory: " + directoryPath);
            return;
        }
        //String buildOutputParentDirectoryPath = relevantBuildOutputLocatorService.locateRelevantBuildOutput(javaFiles.get(0).getPath());

        CompilerManager compilerManager = CompilerManager.getInstance(project);
        ApplicationManager.getApplication().invokeLater(() ->
                compilerManager.compile(javaFiles.toArray(new VirtualFile[0]), compileStatusNotification)
        );
        System.out.println("Compilation request for directory sent.");
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
        System.out.println("Compiling class...");
        // Use the CompilerManager to compile the specific file
        CompilerManager compilerManager = CompilerManager.getInstance(project);
        ApplicationManager.getApplication().invokeLater(() -> compilerManager.compile(new VirtualFile[]{virtualFile}, compileStatusNotification));
        System.out.println("Compilation request sent.");
    }

    // New method to rebuild all classes
    @Override
    public void dynamicallyRebuildAllClasses() {
        // Set up a callback to handle the result of the rebuild
        CompileStatusNotification callback = (aborted, errors, warnings, compileContext) -> {
            if (aborted) {
                System.out.println("Rebuild aborted.");
            } else if (errors > 0) {
                System.out.println("Rebuild finished with errors.");
            } else {
                System.out.println("Rebuild completed successfully with " + warnings + " warnings.");
            }
        };
        dynamicallyRebuildAllClasses(callback);
    }

    @Override
    public void dynamicallyRebuildAllClasses(CompileStatusNotification compileStatusNotification) {
        System.out.println("Rebuilding all classes...");
        // Use the CompilerManager to rebuild the entire project
        CompilerManager compilerManager = CompilerManager.getInstance(project);
        ApplicationManager.getApplication().invokeAndWait(() ->
                compilerManager.rebuild(compileStatusNotification)
        );
        System.out.println("Rebuild request sent.");
    }
}
