package com.translator.service.codactor.io;

import com.translator.model.codactor.ai.chat.function.directive.CreateAndRunUnitTestDirective;
import com.translator.model.codactor.ai.chat.function.directive.CreateAndRunUnitTestDirectiveSession;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.List;

public interface DynamicClassLoaderService {
    CustomURLClassLoader dynamicallyLoadClass(List<String> targetFilePaths) throws MalformedURLException, FileNotFoundException, ClassNotFoundException;
}
