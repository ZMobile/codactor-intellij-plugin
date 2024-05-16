package com.translator.service.codactor.test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.gson.internal.ConstructorConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaClassParserServiceImpl implements JavaClassParserService {
    private JavaParser javaParser;
    private MethodDeclarationService methodDeclarationService;
    private StringCodeOmittedRestorerService stringCodeOmittedRestorerService;
    private DiffStringService diffStringService;

    public JavaClassParserServiceImpl() {
        this.javaParser = new JavaParser();
        this.methodDeclarationService = new MethodDeclarationServiceImpl();
        this.stringCodeOmittedRestorerService = new StringCodeOmittedRestorerServiceImpl();
        this.diffStringService = new DiffStringServiceImpl();
    }

    @Override
    public List<ConstructorDeclaration> collectConstructors(String code) {
        System.out.println("$%$Collecting constructors");
        ParseResult<CompilationUnit> cu = javaParser.parse(code);

        if (!cu.isSuccessful()) {
            System.out.println("Parsing failed for : " + code);
            return null;
        }

        if (cu.getResult().isEmpty()) {
            System.out.println("Parsing failed");
            return null;
        }

        for (ConstructorDeclaration constructorDeclaration : cu.getResult().get().findAll(ConstructorDeclaration.class)) {
            System.out.println("$%$Constructor: " + constructorDeclaration.toString());
        }
        return cu.getResult().get().findAll(ConstructorDeclaration.class);
    }

    public List<MethodDeclaration> collectClassLevelMethods(String code) {
        System.out.println("This gets called 1");
        ParseResult<CompilationUnit> cu = javaParser.parse(code);

        if (!cu.isSuccessful()) {
            System.out.println("Parsing failed for : " + code);
            List<MethodDeclaration> improperlyMergedMethods = findImproperlyMergedMethods(code);

            for (MethodDeclaration method : improperlyMergedMethods) {
                System.out.println("Improperly merged method: " + methodDeclarationService.getBodyFromString(method, code));
            }
            return null;
        }

        if (cu.getResult().isEmpty()) {
            System.out.println("Parsing failed");
            return null;
        }
        CompilationUnit compilationUnit = cu.getResult().get();

        // List to hold class-level methods
        List<MethodDeclaration> classLevelMethods = new ArrayList<>();

        // Visit each ClassOrInterfaceDeclaration
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classOrInterface -> {
            // Check if it's a top-level class (no parent ClassOrInterfaceDeclaration)
            if (!(classOrInterface.getParentNode().isPresent()
                    && classOrInterface.getParentNode().get() instanceof ClassOrInterfaceDeclaration)) {
                // Add all methods from this top-level class to the list
                classLevelMethods.addAll(classOrInterface.getMethods());
            }
        });

        return classLevelMethods;
    }

    public String findSignature(String code, int startIndex) {
        // Find the start of the line from startIndex
        int lineStart = code.lastIndexOf('\n', startIndex - 1);
        if (lineStart == -1) {
            lineStart = 0; // The code starts at the beginning of the file
        } else {
            lineStart += 1; // Move past the newline character
        }

        // Find the index of the next opening brace after lineStart
        int openBraceIndex = code.indexOf('{', lineStart);
        if (openBraceIndex == -1) {
            return null;  // No opening brace found
        }

        // Extract potential signature from lineStart to just before the open brace
        String potentialSignature = code.substring(lineStart, openBraceIndex).trim();

        String[] potentialSignatureLines = potentialSignature.split("\n");
        for (String line : potentialSignatureLines) {
            if (line.trim().isEmpty()) {
                return null;
            }
        }

        // Check if it's a valid method or constructor signature
        if (isMethodOrConstructorSignature(potentialSignature + " {")) {
            return potentialSignature + code.substring(openBraceIndex, openBraceIndex); // Include the opening brace
        }
        return null;
    }

    // You would use the JavaParser to check if it's a valid signature
    public boolean isMethodOrConstructorSignature(String code) {
        String classBody = "class Dummy { " + code + " } }";
        try {
            CompilationUnit cu = new JavaParser().parse(classBody).getResult().get();
            boolean isMethod = cu.findFirst(MethodDeclaration.class).isPresent();
            boolean isConstructor = cu.findFirst(ConstructorDeclaration.class).isPresent();
            return isMethod || isConstructor;
        } catch (Exception e) {
            return false;  // Handle parsing exceptions or invalid code
        }
    }

    public boolean isMethodOrConstructor(String code) {
        String classBody = "class Dummy { " + code + " }";
        try {
            CompilationUnit cu = new JavaParser().parse(classBody).getResult().get();
            boolean isMethod = cu.findFirst(MethodDeclaration.class).isPresent();
            boolean isConstructor = cu.findFirst(ConstructorDeclaration.class).isPresent();
            return isMethod || isConstructor;
        } catch (Exception e) {
            return false;  // Handle parsing exceptions or invalid code
        }
    }


    public List<String> collectMethodOrConstructorSignatures(String code) {
        List<String> signatures = new ArrayList<>();
        String[] lines = code.split("\n");
        int currentLineIndex = 0; // This will keep track of our current position in terms of line numbers
        int trackedLineStartIndex = 0;
        while (currentLineIndex < lines.length) {
            String line = lines[currentLineIndex].trim();
            if (!line.isEmpty()) {
                int signatureStartIndex = code.indexOf(line, trackedLineStartIndex); // Find the first occurrence of this line in the entire code
                if (signatureStartIndex != -1) {
                    String signatureToCheck = findSignature(code, signatureStartIndex);
                    if (signatureToCheck != null) {
                        System.out.println("Found signature: " + signatureToCheck);
                        signatures.add(signatureToCheck);
                        int endOfSignature = signatureStartIndex + signatureToCheck.length();
                        int linesInSignature = countLines(code.substring(signatureStartIndex, endOfSignature));
                        if (linesInSignature > 1) {
                            currentLineIndex += linesInSignature - 1;
                        }
                    }
                }
            }
            currentLineIndex++;
            if (currentLineIndex < lines.length) {
                trackedLineStartIndex += lines[currentLineIndex - 1].length() + 1; // include newline character
            }
        }
        return signatures;
    }


    // Helper method to count the number of lines in a string
    private int countLines(String str) {
        return str.split("\n").length;
    }

    public String collectBody(String code, int signatureStartIndex) {
        int lineStartIndex = code.lastIndexOf('\n', signatureStartIndex);
        if (lineStartIndex == -1) {
            lineStartIndex = 0;  // If no newline, start at the beginning of the file
        } else {
            lineStartIndex += 1; // Move past the newline character
        }
        // Find the start of the method or constructor body by looking for the first opening brace after the signature
        int bodyStartIndex = code.indexOf("{", signatureStartIndex);
        if (bodyStartIndex == -1) {
            return null;  // No opening brace found
        }
        bodyStartIndex += 1; // Move past the opening brace

        // Determine the indentation level of the method or constructor
        String indentation = code.substring(lineStartIndex, signatureStartIndex);
        int indentationLevel = indentation.replaceAll("\\S", "").length(); // Count only whitespace

        // Iterate through the code to find the matching closing brace with the same indentation level
        int depth = 1;
        int trackedIndex = bodyStartIndex;
        while (trackedIndex < code.length()) {
            char currentChar = code.charAt(trackedIndex);
            if (currentChar == '{') {
                depth++;
            } else if (currentChar == '}') {
                depth--;
                if (depth == 0) {
                    // Check if this brace is at the correct indentation level
                    int endLineIndex = code.lastIndexOf('\n', trackedIndex);
                    String currentLineIndentation = code.substring(endLineIndex + 1, trackedIndex).replaceAll("\\S", "");
                    /*if (currentLineIndentation.length() == indentationLevel) {
                        return code.substring(bodyStartIndex - 1, trackedIndex + 1);
                    }*/
                    return null;
                }
            }
            trackedIndex++;
        }
        return null; // No matching closing brace found at the correct indentation level
    }

    public int countMethodOrConstructorSignatures(String code) {
        String[] lines = code.split("\n");
        int count = 0;
        int trackedIndex = 0;  // To keep track of where to start the next search

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (!line.isEmpty()) {
                int signatureStartIndex = code.indexOf(line, trackedIndex);
                if (signatureStartIndex != -1) {
                    // Update the tracked index to the end of this signature
                    String signatureToCheck = findSignature(code, signatureStartIndex);
                    if (signatureToCheck != null) {
                        trackedIndex = signatureStartIndex + signatureToCheck.length();
                        if (isMethodOrConstructorSignature(signatureToCheck)) {
                            count++;

                        }
                    }
                }
            }
        }

        return count;
    }

    @Override
    public List<BodyDeclaration<?>> collectMethodsAndConstructors(String code) {
        List<BodyDeclaration<?>> bodyDeclarations = new ArrayList<>();
        List<ConstructorDeclaration> constructorDeclarations = collectConstructors(code);
        if (constructorDeclarations != null) {
            bodyDeclarations.addAll(constructorDeclarations);
        }
        List<MethodDeclaration> methodDeclarations = collectClassLevelMethods(code);
        if (methodDeclarations != null) {
            bodyDeclarations.addAll(methodDeclarations);
        }
        return bodyDeclarations;
    }

    public List<MethodDeclaration> findImproperlyMergedMethods(String code) {
        List<MethodDeclaration> mergedMethods = new ArrayList<>();

        CompilationUnit cu = javaParser.parse(code).getResult().orElse(null);
        if (cu == null) {
            System.out.println("CompilationUnit is null for code: " + code);
            return mergedMethods;
        }
        cu.accept(new MethodVisitor(), mergedMethods);

        return mergedMethods;
        }


    @Override
    public List<ConstructorDeclaration> findImproperlyMergedConstructors(String code) {
        System.out.println("Finding improperly merged constructors");
        List<ConstructorDeclaration> mergedConstructors = new ArrayList<>();

        CompilationUnit cu = javaParser.parse(code).getResult().orElse(null);
        assert cu != null;
        cu.accept(new ConstructorVisitor(), mergedConstructors);
        System.out.println("Merged constructors: " + mergedConstructors);

        return mergedConstructors;
    }

    @Override
    public List<CodeBodyResource> findImproperlyMergedMethodsAndConstructors(String code, List<BodyDeclaration<?>> allUniqueMethods) {
        List<CodeBodyResource> improperlyMergedCodeBodyResources = new ArrayList<>();
        List<CodeBodyResource> codeBodyResources = collectCodeBodyResources(code, allUniqueMethods);
        System.out.println("Code body resources size: " + codeBodyResources.size());
        for (CodeBodyResource codeBodyResource : codeBodyResources) {
            boolean syntaxCorrect = isMethodOrConstructor(codeBodyResource.getBody());
            if (!syntaxCorrect) {
                improperlyMergedCodeBodyResources.add(codeBodyResource);
            }
        }

        return improperlyMergedCodeBodyResources;
    }

    @Override
    public List<CodeBodyResource> collectCodeBodyResources(String code, List<BodyDeclaration<?>> allUniqueMethods) {
        List<String> signatures = collectMethodOrConstructorSignatures(code);
        List<CodeBodyResource> codeBodyResources = new ArrayList<>();
        //Map<BodyDeclaration<?>, String> bodyDeclarationToStringMap = new HashMap<>();
        //Map<BodyDeclaration<?>, String> improperlyMergedMethodsMap = new HashMap<>();
        System.out.println("This gets called");
        //First things first, remove duplicate signatures:
        List<String> uniqueSignatures = new ArrayList<>();
        for (String signature : signatures) {
            String signatureName = signature.substring(0, signature.indexOf("("));
            //If the point after the "(" doesnt have a newline, add a new line
            String trimmedSignature = signature.trim();
            String spacesAndNewLinesRemovedSignature = trimmedSignature.replace(" ", "").replace("\n", "");
            boolean containsSignature = uniqueSignatures.stream().anyMatch(s -> s.replace(" ", "").replace("\n", "").equals(spacesAndNewLinesRemovedSignature));
            boolean containsAdjacentSignatureWithOmittedString = false;
            /*for (String uniqueSignature : uniqueSignatures) {
                //Specifically the last two words before the "(" e.g. public List<Result> modifyCode()
                //It should be List<Result> modifyCode
                //First check if it contains two spaces
                if (signatureName.contains(" ") && signatureName.indexOf(" ") != signatureName.lastIndexOf(" ")) {
                    // Cut off the signature name after the last space (to handle generics)
                    signatureName = signatureName.substring(signatureName.lastIndexOf(" ", signatureName.lastIndexOf(" ") - 1));
                }
                String signatureNameWithoutSpacesOrNewLines = signatureName.replace(" ", "").replace("\n", "");
                String uniqueSignatureName = uniqueSignature.substring(0, uniqueSignature.indexOf("("));
                if (uniqueSignatureName.contains(" ") && uniqueSignatureName.indexOf(" ") != uniqueSignatureName.lastIndexOf(" ")) {
                    uniqueSignatureName = uniqueSignatureName.substring(uniqueSignatureName.lastIndexOf(" ", uniqueSignatureName.lastIndexOf(" ") - 1));
                }
                String uniqueSignatureNameWithoutSpacesOrNewLines = uniqueSignatureName.replace(" ", "").replace("\n", "");
                if (signatureNameWithoutSpacesOrNewLines.equals(uniqueSignatureNameWithoutSpacesOrNewLines)) {
                    System.out.println("Found string with same name:");
                    System.out.println("Signature: " + signature);
                    System.out.println("Unique signature: " + uniqueSignature);
                    String signatureWithNewLineAfterOpeningBracket = signature;
                    if (signature.indexOf("(") + 1 < signature.length() && signature.charAt(signature.indexOf("(") + 1) != '\n') {
                        signatureWithNewLineAfterOpeningBracket = signature.substring(0, signature.indexOf("(") + 1) + "\n" + signature.substring(signature.indexOf("(") + 1);
                    }
                    String uniqueSignatureWithNewLineAfterOpeningBracket = uniqueSignature;
                    if (uniqueSignature.indexOf("(") + 1 < uniqueSignature.length() && uniqueSignature.charAt(uniqueSignature.indexOf("(") + 1) != '\n') {
                        uniqueSignatureWithNewLineAfterOpeningBracket = uniqueSignature.substring(0, uniqueSignature.indexOf("(") + 1) + "\n" + uniqueSignature.substring(uniqueSignature.indexOf("(") + 1);
                    }
                    boolean signatureContainsOmittedString = stringCodeOmittedRestorerService.containsOmittedStringWithoutDiffMarkers(signature);
                    boolean uniqueSignatureContainsOmittedString = stringCodeOmittedRestorerService.containsOmittedStringWithoutDiffMarkers(uniqueSignature);
                    String diffString;
                    if (signatureContainsOmittedString) {
                        diffString = diffStringService.getDiffString(uniqueSignatureWithNewLineAfterOpeningBracket, signatureWithNewLineAfterOpeningBracket);
                    } else if (uniqueSignatureContainsOmittedString) {
                        diffString = diffStringService.getDiffString(signatureWithNewLineAfterOpeningBracket, uniqueSignatureWithNewLineAfterOpeningBracket);
                    } else {
                        continue;
                    }
                    System.out.println("Diff string: " + diffString);
                    String restoredString = stringCodeOmittedRestorerService.restoreOmittedString(diffString);
                    System.out.println("Restored string: " + restoredString);
                    if (restoredString.trim().replace(" ", "").replace("\n", "").equals(signature.replace(" ", "").replace("\n", ""))) {
                        boolean isAdjacent = Math.abs(signatures.indexOf(signature) - signatures.indexOf(uniqueSignature)) == 1;
                        if (isAdjacent) {
                            System.out.println("Match system worked");
                            containsAdjacentSignatureWithOmittedString = true;
                            break;
                        }
                    }
                }
            }*/
            if (!uniqueSignatures.contains(trimmedSignature) && !containsSignature && !containsAdjacentSignatureWithOmittedString) {
                uniqueSignatures.add(trimmedSignature);
            } else {
                System.out.println("Duplicate signature: " + signature);
                System.out.println("Duplicated with: " + signatures.stream().filter(s -> s.replace(" ", "").replace("\n", "").equals(spacesAndNewLinesRemovedSignature)).findFirst().orElse(null));
            }
        }
        System.out.println("Signatures size: " + signatures.size());
        System.out.println("Unique signatures size: " + uniqueSignatures.size());
        int startIndex = code.indexOf("{") + 1;
        int endIndex = code.lastIndexOf("}");
        for (int i = 0; i < uniqueSignatures.size(); i++) {
            String signature = uniqueSignatures.get(i);
            String nextSignature = i + 1 < uniqueSignatures.size() ? uniqueSignatures.get(i + 1) : null;
            int signatureIndex = code.indexOf(signature, startIndex);
            System.out.println("Signature: " + signature);

            int nextSignatureIndex = nextSignature != null ? code.indexOf(nextSignature, signatureIndex + signature.length()) : endIndex;
            String body = code.substring(signatureIndex, nextSignatureIndex);
            System.out.println("Body: " + body);
            startIndex = signatureIndex + signature.length();
            for (BodyDeclaration<?> bodyDeclaration : allUniqueMethods) {
                //Get name and parameters of body declaration string:
                String bodyDeclarationString = null;
                if (bodyDeclaration instanceof ConstructorDeclaration) {
                    ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) bodyDeclaration;
                    String parameters = constructorDeclaration.getParameters().stream()
                            .map(p -> p.getType().toString() + " " + p.getName().toString())
                            .reduce((p1, p2) -> p1 + ", " + p2)
                            .orElse("");
                    bodyDeclarationString = constructorDeclaration.getNameAsString() + "(" + parameters + ")";
                } else if (bodyDeclaration instanceof MethodDeclaration) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
                    String parameters = methodDeclaration.getParameters().stream()
                            .map(p -> p.getType().toString() + " " + p.getName().toString())
                            .reduce((p1, p2) -> p1 + ", " + p2)
                            .orElse("");
                    bodyDeclarationString = methodDeclaration.getNameAsString() + "(" + parameters + ")";
                }
                assert bodyDeclarationString != null;
                System.out.println("Body declaration string: " + bodyDeclarationString);
                System.out.println("Body: " + body.replace("\n", ""));
                if (body.replace("\n", "").replace(" ", "").contains(bodyDeclarationString.replace(" ", ""))) {
                    System.out.println("Body found: " + bodyDeclarationString);
                    System.out.println("Body declaration: " + bodyDeclaration);
                    System.out.println("Body: " + body);
                    CodeBodyResource codeBodyResource = new CodeBodyResource(signature, body, bodyDeclaration);
                    codeBodyResources.add(codeBodyResource);
                }
            }
        }
        return codeBodyResources;
    }

    private static class MethodVisitor extends VoidVisitorAdapter<List<MethodDeclaration>> {
        @Override
        public void visit(MethodDeclaration n, List<MethodDeclaration> mergedMethods) {
            super.visit(n, mergedMethods);
            System.out.println("###################");
            System.out.println("Visiting method:" + n.toString());
            if (n.getBody().isPresent()) {
                System.out.println("This gets called 1");
                BlockStmt body = n.getBody().get();
                if (body.getStatements().isEmpty()) {
                    System.out.println("This gets called 2");
                    // Empty body, potentially merged
                    mergedMethods.add(n);
                } else {
                    System.out.println("This gets called 3");
                    NodeList<Statement> stmts = body.getStatements();
                    Node lastStmt = stmts.get(stmts.size() - 1);
                    if (lastStmt.getBegin().isPresent() && n.getEnd().isPresent() &&
                            lastStmt.getBegin().get().line > n.getEnd().get().line) {
                        // Last statement starts after method declaration ends, potentially merged
                        mergedMethods.add(n);
                    }
                }
            }
        }
    }

    private static class ConstructorVisitor extends VoidVisitorAdapter<List<ConstructorDeclaration>> {
        @Override
        public void visit(ConstructorDeclaration n, List<ConstructorDeclaration> mergedMethods) {
            super.visit(n, mergedMethods);
            System.out.println("###################");
            System.out.println("Visiting constructor:" + n.toString());
            System.out.println("This gets called 1");
            BlockStmt body = n.getBody().asBlockStmt();
            if (body.getStatements().isEmpty()) {
                System.out.println("This gets called 2");
                // Empty body, potentially merged
                mergedMethods.add(n);
            } else {
                System.out.println("This gets called 3");
                NodeList<Statement> stmts = body.getStatements();
                Node lastStmt = stmts.get(stmts.size() - 1);
                if (lastStmt.getBegin().isPresent() && n.getEnd().isPresent() &&
                        lastStmt.getBegin().get().line > n.getEnd().get().line) {
                    // Last statement starts after constructor declaration ends, potentially merged
                    mergedMethods.add(n);
                }
            }
        }
    }

    //Might move this to fused method handler service
    public void comparePossiblyDuplicateMethods(List<String> signatures) {
        List<String> duplicateMethods = new ArrayList<>();
        for (int i = 0; i < signatures.size(); i++) {
            String signature = signatures.get(i);
            for (int j = i + 1; j < signatures.size(); j++) {
                String otherSignature = signatures.get(j);
                String signatureName = signature.substring(0, signature.indexOf("("));
                if (signatureName.contains(" ")) {
                    // Cut off the signature name after the last space (to handle generics)
                    signatureName = signatureName.substring(signatureName.lastIndexOf(" "));
                }
                String otherSignatureName = otherSignature.substring(0, otherSignature.indexOf("("));
                if (otherSignatureName.contains(" ")) {
                    // Cut off the signature name after the last space (to handle generics)
                    otherSignatureName = otherSignatureName.substring(otherSignatureName.lastIndexOf(" "));
                }
                if (signatureName.trim().equals(otherSignatureName.trim())) {
                    //Matching names found, now to check if the parameters are the same
                }
            }
        }
    }
}
