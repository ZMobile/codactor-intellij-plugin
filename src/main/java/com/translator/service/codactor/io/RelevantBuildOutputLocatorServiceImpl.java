package com.translator.service.codactor.io;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.vfs.VirtualFile;

import javax.inject.Inject;
import java.io.File;

public class RelevantBuildOutputLocatorServiceImpl implements RelevantBuildOutputLocatorService {
    private final Project project;

    @Inject
    public RelevantBuildOutputLocatorServiceImpl(Project project) {
        this.project = project;
    }

    @Override
    public String locateRelevantBuildOutput(String filePath) {
        filePath = filePath.replace("\\", "/");

        // Get the parent directory of the file (excluding the file name)
        String parentDirectory = filePath.substring(0, filePath.lastIndexOf("/"));

        Module[] modules = ModuleManager.getInstance(project).getModules();

        // Loop through each module to print the output paths
        for (Module module : modules) {
            // Get the compiler module extension to access the build output paths
            CompilerModuleExtension compilerModuleExtension = CompilerModuleExtension.getInstance(module);

            if (compilerModuleExtension != null) {
                // Get the output path for compiled main classes
                VirtualFile mainOutputPath = compilerModuleExtension.getCompilerOutputPath();

                // Get the output path for compiled test classes
                VirtualFile testOutputPath = compilerModuleExtension.getCompilerOutputPathForTests();

                // Print the output paths
                System.out.println("Module: " + module.getName());
                if (filePath.contains("/src/main/java/") && mainOutputPath != null) {
                    String expectedOutputDir = parentDirectory.replace("/src/main/java/", "/build/classes/java/main/");
                    File file = new File(expectedOutputDir);
                    if (file.exists()) {
                        return file.getPath();
                    }
                }

                // Check if the file belongs to the test source set and if the corresponding path exists
                if (filePath.contains("/src/test/java/") && testOutputPath != null) {
                    String expectedOutputDir = parentDirectory.replace("/src/test/java/", "/build/classes/java/test/");
                    File file = new File(expectedOutputDir);
                    if (file.exists()) {
                        return file.getPath();
                    }
                }
            }
        }
        return null;
    }
}
