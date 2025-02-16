package com.translator.service.codactor.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CustomURLClassLoader extends URLClassLoader {

    private final List<String> targetClassNames;

    public CustomURLClassLoader(URL[] urls, ClassLoader parent, List<String> targetClassNames) {
        super(urls, parent);
        this.targetClassNames = targetClassNames;
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // If it's the class we want to reload, force reloading from file
        for (String targetClassName : targetClassNames) {
            if (name.equals(targetClassName)) {
                return loadTestedClassFromFile(name);
            }
        }
        // Delegate to parent for other classes
        return super.loadClass(name, resolve);
    }

    private Class<?> loadTestedClassFromFile(String name) throws ClassNotFoundException {
        try {
            String classPath = name.replace('.', File.separatorChar) + ".class";
            Path path = Path.of(getURLs()[0].getPath(), classPath);

            if (Files.exists(path)) {
                byte[] classData = Files.readAllBytes(path);
                return defineClass(name, classData, 0, classData.length);
            } else {
                throw new ClassNotFoundException("Class not found on path: " + path);
            }
        } catch (IOException e) {
            throw new ClassNotFoundException("Error loading class from file system", e);
        }
    }
}
