package com.translator.service.codactor.io;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

public interface DynamicClassLoaderService {
    CustomURLClassLoader dynamicallyLoadClass(String filePath) throws MalformedURLException, FileNotFoundException, ClassNotFoundException;
}
