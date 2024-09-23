package com.translator.service.codactor.io;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DynamicClassLoaderServiceImpl implements DynamicClassLoaderService {
    private final RelevantBuildOutputLocatorService relevantBuildOutputLocatorService;

    @Inject
    public DynamicClassLoaderServiceImpl(RelevantBuildOutputLocatorService relevantBuildOutputLocatorService) {
        this.relevantBuildOutputLocatorService = relevantBuildOutputLocatorService;
    }

    public CustomURLClassLoader dynamicallyLoadClass(String filePath) throws MalformedURLException, FileNotFoundException, ClassNotFoundException {
        String isolatedClassName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
        String packagePath = filePath.substring(filePath.indexOf("java/") + 5, filePath.lastIndexOf("/")).replace("/", ".");
        String className = packagePath + "." + isolatedClassName;

        String buildOutputParentDirectoryPath = relevantBuildOutputLocatorService.locateRelevantBuildOutput(filePath);
        String buildOutputPath = buildOutputParentDirectoryPath + "/" + isolatedClassName + ".class";
        File buildOutputFile = new File(buildOutputPath);
        if (!buildOutputFile.exists()) {
            throw new FileNotFoundException("Error: build output file not found at: " + buildOutputPath);
        }

        String buildOutputRootDirPath;
        if (buildOutputPath.contains("/main/")) {
            buildOutputRootDirPath = buildOutputPath.substring(0,
                    buildOutputPath.indexOf("build/classes/java/main/") + "build/classes/java/main/".length());
        } else if (buildOutputPath.contains("/test/")) {
            buildOutputRootDirPath = buildOutputPath.substring(0,
                    buildOutputPath.indexOf("build/classes/java/test/") + "build/classes/java/test/".length());
        } else {
            throw new FileNotFoundException("Error: Could not determine build output root directory");
        }

        File buildOutputRootDir = new File(buildOutputRootDirPath);
        System.out.println("Build output root dir path: " + buildOutputRootDirPath);
        URL[] urls = { buildOutputRootDir.toURI().toURL() };
        return new CustomURLClassLoader(urls, getClass().getClassLoader(), className);
    }
}
