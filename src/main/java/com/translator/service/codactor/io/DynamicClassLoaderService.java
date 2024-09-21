package com.translator.service.codactor.io;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public interface DynamicClassLoaderService {
    Class<?> dynamicallyLoadClass(String filePath) throws MalformedURLException, FileNotFoundException, ClassNotFoundException;
}
