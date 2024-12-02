package com.translator.service.codactor.ai.modification.test;

import com.github.javaparser.Position;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public interface MethodDeclarationService {
    String getBodyFromString(BodyDeclaration<?> methodDeclaration, String code);

    String getBodyFromStringWithoutEndBracket(BodyDeclaration<?> methodDeclaration, String code);

    int positionToIndex(Position pos, String code);
}
