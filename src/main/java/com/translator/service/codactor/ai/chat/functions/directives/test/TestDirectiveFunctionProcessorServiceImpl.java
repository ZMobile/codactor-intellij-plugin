package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.google.gson.Gson;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.function.GptFunctionCall;
import com.translator.model.codactor.ai.chat.function.directive.                                                        CreateAndRunUnitTestDirective;
import com.translator.model.codactor.ai.chat.function.directive.CreateAndRunUnitTestDirectiveSession;
import com.translator.model.codactor.ai.modification.ModificationType;
import com.translator.service.codactor.ai.modification.AiUnitTestCodeModificationService;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.ide.editor.CodeSnippetIndexGetterService;
import com.translator.service.codactor.ide.file.FileCreatorService;
import com.translator.service.codactor.ide.file.FileRemoverService;
import com.translator.service.codactor.json.JsonExtractorService;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class TestDirectiveFunctionProcessorServiceImpl implements TestDirectiveFunctionProcessorService {
    private final Gson gson;
    private final AiUnitTestCodeModificationService aiUnitTestCodeModificationService;
    private final CodeSnippetExtractorService codeSnippetExtractorService;
    private final CodeSnippetIndexGetterService codeSnippetIndexGetterService;
    private final FileCreatorService fileCreatorService;
    private final FileRemoverService fileRemoverService;
    private final RunTestAndGetOutputService runTestAndGetOutputService;

    @Inject
    public TestDirectiveFunctionProcessorServiceImpl(Gson gson,
                                                     AiUnitTestCodeModificationService aiUnitTestCodeModificationService,
                                                     CodeSnippetExtractorService codeSnippetExtractorService,
                                                     CodeSnippetIndexGetterService codeSnippetIndexGetterService,
                                                     FileCreatorService fileCreatorService,
                                                     FileRemoverService fileRemoverService,
                                                     RunTestAndGetOutputService runTestAndGetOutputService) {
        this.gson = gson;
        this.aiUnitTestCodeModificationService = aiUnitTestCodeModificationService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.codeSnippetIndexGetterService = codeSnippetIndexGetterService;
        this.fileCreatorService = fileCreatorService;
        this.fileRemoverService = fileRemoverService;
        this.runTestAndGetOutputService = runTestAndGetOutputService;
    }

    public String processFunctionCall(Inquiry inquiry, GptFunctionCall gptFunctionCall) {
        CreateAndRunUnitTestDirective createAndRunUnitTestDirective = (CreateAndRunUnitTestDirective) inquiry.getActiveDirective();
        CreateAndRunUnitTestDirectiveSession createAndRunUnitTestDirectiveSession = createAndRunUnitTestDirective.getSession();
        try {
            //Moved to default function calls
            /*if (gptFunctionCall.getName().equalsIgnoreCase("create_and_run_unit_test")) {
                String filePath = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
                String content = codeSnippetExtractorService.getAllText(filePath);
                //Collects test dependency info from maven and gradle files
                StringBuilder response = new StringBuilder();
                response.append("You have chosen to create and run a unit test for the following Java code file: \n");
                response.append("path: " + filePath + "\n");
                response.append("content: {" + content + "}\n");
                response.append("The following are the unit test dependencies for this Java project: ");
                response.append(mavenAndGradleDependencyCollectorService.collectProjectTestDependencies());
                response.append("\n");
                response.append("In order to run this test, you will need to use the provided functions to create a unit test to run, but you may also temporarily place logs in the subject code file which will be triggered by the unit tests.");
                createAndRunUnitTestDirectiveSession.setFilePath(filePath);
                return response.toString();
            } else */
            if (gptFunctionCall.getName().equalsIgnoreCase("request_file_creation")) {
                return "Denied: you are conducting a unit test directive. Please use one of the test directive functions i.e. create_unit_test_code_file for this action.";
            } else if (gptFunctionCall.getName().equalsIgnoreCase("create_unit_test_code_file")) {
                File subjectFile = new File(createAndRunUnitTestDirectiveSession.getFilePath());
                String subjectFileName = subjectFile.getName();
                String testFileName = subjectFileName.replace(".java", "" +
                        "Test.java");
                String testFilePath = createAndRunUnitTestDirectiveSession.getFilePath()
                        .replace(".java", "Test.java");
                System.out.println("Test file path: " + testFilePath);
                File testFile = new File(testFilePath);
                File testFileDirectory = new File(testFile.getParent());
                if (!testFileDirectory.exists()) {
                    testFileDirectory.mkdirs();
                }
                if (testFile.exists()) {
                    int i = 2;
                    while (testFile.exists()) {
                        testFilePath = testFilePath.replace("Test", "Test" + i);
                        testFile = new File(testFilePath);
                        i++;
                    }
                }
                createAndRunUnitTestDirectiveSession.setTestFilePath(testFilePath);
                createAndRunUnitTestDirectiveSession.setUnitTestCreated(true);

                String testCode = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "code");
                fileCreatorService.createFile(testFileDirectory.getAbsolutePath(), testFileName, testCode);
                return "Unit test code file created at: " + testFilePath;
            } else if (gptFunctionCall.getName().equalsIgnoreCase("read_subject_code_file")) {
                return codeSnippetExtractorService.getAllText(createAndRunUnitTestDirectiveSession.getFilePath());
            } else if (gptFunctionCall.getName().equalsIgnoreCase("temp_log_inject")) {
                String path = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
                if (path == null) {
                    String packageName = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "package");
                    if (packageName != null) {
                        VirtualFile virtualFile = codeSnippetExtractorService.getVirtualFileFromPackage(packageName);
                        if (virtualFile != null) {
                            path = virtualFile.getPath();
                        }
                    }
                }
                if (path == null) {
                    return "Error: File not found.";
                }
                String description = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "description");
                String replacementCodeSnippetString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "replacementCodeSnippet");
                if (replacementCodeSnippetString == null) {
                    return "Error: You need to provide your modification as a replacement code snippet to mark what will be replacing the code snippet you selected.";
                }
                int startIndex;
                int endIndex;
                String code = codeSnippetExtractorService.getAllText(path);
                if (code == null) {
                    String packageName = path.replaceAll("/", ".");
                    VirtualFile virtualFile = codeSnippetExtractorService.getVirtualFileFromPackage(packageName);
                    if (virtualFile != null) {
                        path = virtualFile.getPath();
                    }
                    code = codeSnippetExtractorService.getAllText(path);
                }
                if (code == null) {
                    return "Error: File not found";
                }
                String codeSnippetString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "codeSnippet");
                String startSnippetString = null;
                String endSnippetString = null;
                if (codeSnippetString != null) {
                    startIndex = codeSnippetIndexGetterService.getStartIndex(code, codeSnippetString);
                    endIndex = codeSnippetIndexGetterService.getEndIndexAfterStartIndex(code, startIndex, codeSnippetString);
                } else {
                    startSnippetString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "startBoundary");

                    if (startSnippetString != null) {
                        try {
                            startIndex = codeSnippetIndexGetterService.getStartIndex(code, startSnippetString);
                        } catch (NumberFormatException e) {
                            startIndex = 0;
                        }
                    } else {
                        startIndex = 0;
                    }
                    endSnippetString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "endBoundary");
                    if (endSnippetString != null) {
                        try {
                            endIndex = codeSnippetIndexGetterService.getEndIndex(code, startSnippetString, endSnippetString);
                        } catch (NumberFormatException e) {
                            endIndex = code.length();
                        }
                    } else {
                        endIndex = code.length();
                    }
                    // Check if startSnippetString is after endSnippetString, and swap if necessary
                    if (startSnippetString != null && endSnippetString != null && startIndex > endIndex) {
                        String temp = startSnippetString;
                        startSnippetString = endSnippetString;
                        endSnippetString = temp;
                        try {
                            startIndex = codeSnippetIndexGetterService.getStartIndex(code, startSnippetString);
                            if (startIndex == -1) {
                                return "Error: Start boundary not found in code snippet.\n"
                                        + " Code: " + code
                                        + " Start boundary searched: " + startSnippetString;
                            }
                        } catch (NumberFormatException e) {
                            startIndex = 0;
                        }
                        try {
                            endIndex = codeSnippetIndexGetterService.getEndIndexAfterStartIndex(code, startIndex, endSnippetString);
                            if (endIndex == -1) {
                                return "Error: End boundary not found in targeted code snippet.\n"
                                        + " Code: " + code
                                        + " End boundary searched: " + endSnippetString;
                            }
                        } catch (NumberFormatException e) {
                            endIndex = code.length();
                        }
                    }
                }
                if (startIndex < 0) {
                    startIndex = 0;
                }
                String modificationTypeString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "modificationType");
                assert modificationTypeString != null;
                ModificationType modificationType;
                String modificationId = "Error: no modification type specified.";
                switch (modificationTypeString) {
                    case "modify":
                        if (startSnippetString == null && endSnippetString == null) {
                            modificationType = ModificationType.MODIFY;
                            modificationId = aiUnitTestCodeModificationService.getModifiedCode(path, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        } else {
                            System.out.println("Start snippet string: " + startSnippetString + " +\n End snippet string: " + endSnippetString);
                            modificationType = ModificationType.MODIFY_SELECTION;
                            modificationId = aiUnitTestCodeModificationService.getModifiedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        }
                        break;
                    case "fix":
                        if (startSnippetString == null && endSnippetString == null) {
                            modificationType = ModificationType.FIX;
                            modificationId = aiUnitTestCodeModificationService.getFixedCode(path, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        } else {
                            modificationType = ModificationType.FIX_SELECTION;
                            modificationId = aiUnitTestCodeModificationService.getFixedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        }
                        break;
                    case "create":
                        modificationId = aiUnitTestCodeModificationService.getCreatedCode(path, description, new ArrayList<>(), replacementCodeSnippetString);
                        break;
                }
                if (modificationId != null) {
                    if (modificationId.startsWith("Error")) {
                        return "{" +
                                "\"message\": \"" + modificationId + "\"" +
                                "}";
                    } else {
                        return "{" +
                                "\"message\": \"Modification requested. Modification id: " + modificationId + " \"" +
                                "}";
                    }
                } else {
                    return "{" +
                            "\"message\": \"Error: Unspecified.\"" +
                            "}";
                }
            } else if (gptFunctionCall.getName().equalsIgnoreCase("modify_unit_test")) {
                String path = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "path");
                if (path == null) {
                    String packageName = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "package");
                    if (packageName != null) {
                        VirtualFile virtualFile = codeSnippetExtractorService.getVirtualFileFromPackage(packageName);
                        if (virtualFile != null) {
                            path = virtualFile.getPath();
                        }
                    }
                }
                if (path == null) {
                    return "Error: File not found.";
                }
                String description = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "description");
                String replacementCodeSnippetString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "replacementCodeSnippet");
                if (replacementCodeSnippetString == null) {
                    return "Error: You need to provide your modification as a replacement code snippet to mark what will be replacing the code snippet you selected.";
                }
                int startIndex;
                int endIndex;
                String code = codeSnippetExtractorService.getAllText(path);
                if (code == null) {
                    String packageName = path.replaceAll("/", ".");
                    VirtualFile virtualFile = codeSnippetExtractorService.getVirtualFileFromPackage(packageName);
                    if (virtualFile != null) {
                        path = virtualFile.getPath();
                    }
                    code = codeSnippetExtractorService.getAllText(path);
                }
                if (code == null) {
                    return "Error: File not found";
                }
                String codeSnippetString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "codeSnippet");
                String startSnippetString = null;
                String endSnippetString = null;
                if (codeSnippetString != null) {
                    startIndex = codeSnippetIndexGetterService.getStartIndex(code, codeSnippetString);
                    endIndex = codeSnippetIndexGetterService.getEndIndexAfterStartIndex(code, startIndex, codeSnippetString);
                } else {
                    startSnippetString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "startBoundary");

                    if (startSnippetString != null) {
                        try {
                            startIndex = codeSnippetIndexGetterService.getStartIndex(code, startSnippetString);
                        } catch (NumberFormatException e) {
                            startIndex = 0;
                        }
                    } else {
                        startIndex = 0;
                    }
                    endSnippetString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "endBoundary");
                    if (endSnippetString != null) {
                        try {
                            endIndex = codeSnippetIndexGetterService.getEndIndex(code, startSnippetString, endSnippetString);
                        } catch (NumberFormatException e) {
                            endIndex = code.length();
                        }
                    } else {
                        endIndex = code.length();
                    }
                    // Check if startSnippetString is after endSnippetString, and swap if necessary
                    if (startSnippetString != null && endSnippetString != null && startIndex > endIndex) {
                        String temp = startSnippetString;
                        startSnippetString = endSnippetString;
                        endSnippetString = temp;
                        try {
                            startIndex = codeSnippetIndexGetterService.getStartIndex(code, startSnippetString);
                            if (startIndex == -1) {
                                return "Error: Start boundary not found in code snippet.\n"
                                        + " Code: " + code
                                        + " Start boundary searched: " + startSnippetString;
                            }
                        } catch (NumberFormatException e) {
                            startIndex = 0;
                        }
                        try {
                            endIndex = codeSnippetIndexGetterService.getEndIndexAfterStartIndex(code, startIndex, endSnippetString);
                            if (endIndex == -1) {
                                return "Error: End boundary not found in targeted code snippet.\n"
                                        + " Code: " + code
                                        + " End boundary searched: " + endSnippetString;
                            }
                        } catch (NumberFormatException e) {
                            endIndex = code.length();
                        }
                    }
                }
                if (startIndex < 0) {
                    startIndex = 0;
                }
                String modificationTypeString = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "modificationType");
                assert modificationTypeString != null;
                ModificationType modificationType;
                String modificationId = "Error: no modification type specified.";
                switch (modificationTypeString) {
                    case "modify":
                        if (startSnippetString == null && endSnippetString == null) {
                            modificationType = ModificationType.MODIFY;
                            modificationId = aiUnitTestCodeModificationService.getModifiedCode(path, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        } else {
                            System.out.println("Start snippet string: " + startSnippetString + " +\n End snippet string: " + endSnippetString);
                            modificationType = ModificationType.MODIFY_SELECTION;
                            modificationId = aiUnitTestCodeModificationService.getModifiedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        }
                        break;
                    case "fix":
                        if (startSnippetString == null && endSnippetString == null) {
                            modificationType = ModificationType.FIX;
                            modificationId = aiUnitTestCodeModificationService.getFixedCode(path, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        } else {
                            modificationType = ModificationType.FIX_SELECTION;
                            modificationId = aiUnitTestCodeModificationService.getFixedCode(path, startIndex, endIndex, description, modificationType, new ArrayList<>(), replacementCodeSnippetString);
                        }
                        break;
                    case "create":
                        modificationId = aiUnitTestCodeModificationService.getCreatedCode(path, description, new ArrayList<>(), replacementCodeSnippetString);
                        break;
                }
                if (modificationId != null) {
                    if (modificationId.startsWith("Error")) {
                        return "{" +
                                "\"message\": \"" + modificationId + "\"" +
                                "}";
                    } else {
                        return "{" +
                                "\"message\": \"Modification requested. Modification id: " + modificationId + " \"" +
                                "}";
                    }
                } else {
                    return "{" +
                            "\"message\": \"Error: Unspecified.\"" +
                            "}";
                }
            } else if (gptFunctionCall.getName().startsWith("run_test")) {
                String result = runTestAndGetOutputService.runTestAndGetOutput(createAndRunUnitTestDirectiveSession.getTestFilePath());
                createAndRunUnitTestDirectiveSession.setTestResult(result);
                return result;
            /*} else if (gptFunctionCall.getName().equalsIgnoreCase("run_unit_test_with_coverage")) {*/
            } else if (gptFunctionCall.getName().equalsIgnoreCase("end_test_and_report")) {
                inquiry.setActiveDirective(null);
                fileRemoverService.deleteCodeFile(createAndRunUnitTestDirectiveSession.getTestFilePath());
                return "Please provide a report detailing your findings, or if applicable, explaining the obstacles that prevented you from reaching a conclusion. Here is a collection of data acquired by this test: " + gson.toJson(createAndRunUnitTestDirectiveSession);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: The function call threw the following error and may be non functional: " + Arrays.toString(e.getStackTrace());
        }
    }
}