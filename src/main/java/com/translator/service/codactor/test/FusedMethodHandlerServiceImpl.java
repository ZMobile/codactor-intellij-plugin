package com.translator.service.codactor.test;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FusedMethodHandlerServiceImpl implements FusedMethodHandlerService {
    private JavaClassParserService javaClassParserService;
    private MethodDeclarationService methodDeclarationService;
    private DiffStringService diffStringService;

    public FusedMethodHandlerServiceImpl() {
        this.javaClassParserService = new JavaClassParserServiceImpl();
        this.methodDeclarationService = new MethodDeclarationServiceImpl();
        this.diffStringService = new DiffStringServiceImpl();
    }

    public String fixImproperlyFusedMethods(String beforeCode, String afterCode, String diffCode) {
        System.out.println("Fixing improperly infused methods");
        String diffCodeWithoutDiff = diffCode.replace("-[-]-", "")
                .replace("-[=]-", "")
                .replace("-[+]-", "");
        System.out.println("Count methods and constructors: " + javaClassParserService.countMethodOrConstructorSignatures(diffCodeWithoutDiff));
        System.out.println("Diff code without diff: " + diffCodeWithoutDiff);
        List<BodyDeclaration<?>> beforeMethods = javaClassParserService.collectMethodsAndConstructors(beforeCode);
        List<BodyDeclaration<?>> beforeOnlyMethods = new ArrayList<>(beforeMethods);
        List<BodyDeclaration<?>> afterMethods = javaClassParserService.collectMethodsAndConstructors(afterCode);
        List<BodyDeclaration<?>> maintainedMethods = new ArrayList<>();

        for (BodyDeclaration<?> afterMethod : afterMethods) {
            if (afterMethod instanceof MethodDeclaration) {
                MethodDeclaration afterMethodDeclaration = (MethodDeclaration) afterMethod;
                for (BodyDeclaration<?> beforeMethod : beforeMethods) {
                    if (beforeMethod instanceof MethodDeclaration) {
                        MethodDeclaration beforeMethodDeclaration = (MethodDeclaration) beforeMethod;
                        if (beforeMethodDeclaration.getNameAsString().equals(afterMethodDeclaration.getNameAsString()) && beforeMethodDeclaration.getParameters().equals(afterMethodDeclaration.getParameters())) {
                            maintainedMethods.add(afterMethod);
                            beforeOnlyMethods.remove(beforeMethod);
                            break;
                        }
                    }
                }
            } else if (afterMethod instanceof ConstructorDeclaration) {
                ConstructorDeclaration afterConstructorDeclaration = (ConstructorDeclaration) afterMethod;
                for (BodyDeclaration<?> beforeMethod : beforeMethods) {
                    if (beforeMethod instanceof ConstructorDeclaration) {
                        ConstructorDeclaration beforeConstructorDeclaration = (ConstructorDeclaration) beforeMethod;
                        if (beforeConstructorDeclaration.getNameAsString().equals(afterConstructorDeclaration.getNameAsString()) && beforeConstructorDeclaration.getParameters().equals(afterConstructorDeclaration.getParameters())) {
                            maintainedMethods.add(afterMethod);
                            beforeOnlyMethods.remove(beforeMethod);
                            break;
                        }
                    }
                }
            }
        }

        List<BodyDeclaration<?>> allUniqueMethods = new ArrayList<>();
        allUniqueMethods.addAll(beforeOnlyMethods);
        allUniqueMethods.addAll(afterMethods);
        List<CodeBodyResource> codeBodyResources = javaClassParserService.findImproperlyMergedMethodsAndConstructors(diffCodeWithoutDiff, allUniqueMethods);
        System.out.println("Improperly fused methods size: " + codeBodyResources.size());
        for (CodeBodyResource codeBodyResource : codeBodyResources) {
            System.out.println("Improperly fused method body: " + codeBodyResource.getBody());
        }
        for (BodyDeclaration<?> bodyDeclaration : afterMethods) {
            //Make sure the code body resources doesnt have the same method
            if (codeBodyResources.stream().noneMatch(codeBodyResource -> codeBodyResource.getBodyDeclaration().equals(bodyDeclaration))) {
                codeBodyResources.add(new CodeBodyResource(bodyDeclaration));
            }
        }

       for (CodeBodyResource codeBodyResource : codeBodyResources) {
            if (beforeOnlyMethods.contains(codeBodyResource.getBodyDeclaration())) {
                String containedMethodString = methodDeclarationService.getBodyFromString(codeBodyResource.getBodyDeclaration(), beforeCode);
                diffCode = diffStringService.replaceConsideringDiffMarkers(diffCode, codeBodyResource.getBody().trim(), containedMethodString, DiffType.REMOVE);
                /*newString.append(containedMethodString);
                if (i < beforeContainedMethods.size() - 1) {
                    newString.append("\n\n");
                }*/
            }
            //for (BodyDeclaration<?> containedMethod : maintainedContainedMethods) {
            if (maintainedMethods.contains(codeBodyResource.getBodyDeclaration())) {
                String containedMethodString = methodDeclarationService.getBodyFromString(codeBodyResource.getBodyDeclaration(), afterCode);
                diffCode = diffStringService.replaceConsideringDiffMarkers(diffCode, codeBodyResource.getBody().trim(), containedMethodString, DiffType.EQUAL);
                /*newString.append(containedMethodString);
                if (i < beforeContainedMethods.size() - 1) {
                    newString.append("\n\n");
                }*/
            }
            //for (BodyDeclaration<?> containedMethod : afterContainedMethods) {
            if (afterMethods.contains(codeBodyResource.getBodyDeclaration())) {
                String containedMethodString = methodDeclarationService.getBodyFromString(codeBodyResource.getBodyDeclaration(), afterCode);
                diffCode = diffStringService.replaceConsideringDiffMarkers(diffCode, containedMethodString, "", DiffType.ADD);
                diffCode = diffStringService.addMethodToEndOfClass(diffCode, containedMethodString, DiffType.ADD);
            }
        }
        return diffCode;
    }
}
