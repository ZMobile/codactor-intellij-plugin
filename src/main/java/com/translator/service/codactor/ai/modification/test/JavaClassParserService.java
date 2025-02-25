package com.translator.service.codactor.ai.modification.test;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.translator.model.codactor.ai.modification.test.CodeBodyResource;

import java.util.List;

public interface JavaClassParserService {
    List<String> collectMethodOrConstructorSignatures(String code);

    List<MethodDeclaration> collectClassLevelMethods(String code);

    List<ConstructorDeclaration> collectConstructors(String code);

    List<MethodDeclaration> findImproperlyMergedMethods(String code);

    List<ConstructorDeclaration> findImproperlyMergedConstructors(String code);

    List<BodyDeclaration<?>> collectMethodsAndConstructors(String code);

   List<CodeBodyResource> collectCodeBodyResources(String code, List<BodyDeclaration<?>> allUniqueMethods);

    List<CodeBodyResource> findImproperlyMergedMethodsAndConstructors(String diffCodeWithoutDiff, List<BodyDeclaration<?>> allUniqueMethods);

    boolean isMethodOrConstructorSignature(String line);

    int countMethodOrConstructorSignatures(String code);
}
