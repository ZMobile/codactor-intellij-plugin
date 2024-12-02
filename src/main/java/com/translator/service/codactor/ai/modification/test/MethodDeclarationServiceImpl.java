package com.translator.service.codactor.ai.modification.test;

import com.github.javaparser.Position;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodDeclarationServiceImpl implements MethodDeclarationService {
    public String getBodyFromString(BodyDeclaration<?> methodDeclaration, String code) {
        return code.substring(getIndexOfBeginningOfLineOfPosition(methodDeclaration.getBegin().get(), code), positionToIndex(methodDeclaration.getEnd().get(), code) + 1);
    }

    public String getBodyFromStringWithoutEndBracket(BodyDeclaration<?> bodyDeclaration, String code) {
        return code.substring(positionToIndex(bodyDeclaration.getBegin().get(), code), positionToIndex(bodyDeclaration.getEnd().get(), code));
    }

    public int positionToIndex(Position pos, String code) {
        int index = 0;
        int line = 1;
        int column = 1;
        while (line < pos.line || (line == pos.line && column < pos.column)) {
            if (code.charAt(index) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            index++;
        }
        return index;
    }

    public int getIndexOfBeginningOfLineOfPosition(Position pos, String code) {
        int index = positionToIndex(pos, code);
        while (code.charAt(index) != '\n') {
            index--;
        }
        return index + 1;
    }
}
