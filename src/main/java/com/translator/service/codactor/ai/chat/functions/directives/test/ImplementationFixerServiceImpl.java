package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.model.codactor.ai.chat.function.GptFunction;
import com.translator.model.codactor.ai.chat.function.GptFunctionCall;
import com.translator.model.codactor.ai.chat.function.Parameters;
import com.translator.model.codactor.ai.chat.function.Property;
import com.translator.model.codactor.ai.chat.function.directive.test.ReplacedClassInfoResource;
import com.translator.service.codactor.ai.chat.inquiry.InquiryService;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.ide.editor.RangeReplaceService;
import com.translator.service.codactor.json.JsonExtractorService;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import javax.inject.Inject;
import java.util.*;

public class ImplementationFixerServiceImpl implements ImplementationFixerService {
    private final InquiryService inquiryService;
    private final CodeSnippetExtractorService codeSnippetExtractorService;
    private final RangeReplaceService rangeReplaceService;

    @Inject
    public ImplementationFixerServiceImpl(InquiryService inquiryService,
                                          CodeSnippetExtractorService codeSnippetExtractorService,
                                          RangeReplaceService rangeReplaceService) {
        this.inquiryService = inquiryService;
        this.codeSnippetExtractorService = codeSnippetExtractorService;
        this.rangeReplaceService = rangeReplaceService;
    }

    @Override
    public ReplacedClassInfoResource startFixing(String implementationFilePath, Map<String, Result> filePathToResultMap) {
        String code = codeSnippetExtractorService.getAllText(implementationFilePath);
        if (isUnitTestCulpable(code, filePathToResultMap)) {
            return fixUnitTest(implementationFilePath, filePathToResultMap);
        } else {
            return fixImplementation(implementationFilePath, filePathToResultMap);
        }
    }

    public ReplacedClassInfoResource fixImplementation(String implementationFilePath, Map<String, Result> filePathToResultMap) {
        String code = codeSnippetExtractorService.getAllText(implementationFilePath);
        StringBuilder failureString = assembleFailureString(code, filePathToResultMap);
        failureString.append("\nCan you fix the code to also pass this test?");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Inquiry inquiry = inquiryService.createHeadlessInquiry(failureString.toString(), "gpt-4o", false);
        InquiryChat inquiryChat = inquiry.getChats().get(inquiry.getChats().size() - 1);
        //Code is surrounded by "```" to indicate a code block. Isolate this code:
        String startOfCode = inquiryChat.getMessage().substring(inquiryChat.getMessage().indexOf("```") + 3);
        String newImplementationCode = startOfCode.substring(0, startOfCode.indexOf("```"));
        if (newImplementationCode.startsWith("java")) {
            newImplementationCode = newImplementationCode.substring(4);
        }
        if (newImplementationCode.startsWith("\n")) {
            newImplementationCode = newImplementationCode.substring(1);
        }
        System.out.println("Replacing implementation...");
        System.out.println("Implementation file path: " + implementationFilePath);
        System.out.println("Old code: " + code);
        System.out.println("New code: " + newImplementationCode);
        rangeReplaceService.replaceRange(implementationFilePath,0, code.length(), newImplementationCode, true);
        return new ReplacedClassInfoResource.Builder()
                .withFilePath(implementationFilePath)
                .withOldCode(code)
                .withNewCode(newImplementationCode)
                .withFormerResults(filePathToResultMap)
                .build();
    }

    public ReplacedClassInfoResource fixUnitTest(String implementationFilePath, Map<String, Result> filePathToResultMap) {
        String code = codeSnippetExtractorService.getAllText(implementationFilePath);
        StringBuilder failureString = assembleFailureString(code, filePathToResultMap);
        failureString.append("\nIn this case, the unit test was determined to be culpable. Can you fix the unit test to have this test pass?");
        Inquiry inquiry = inquiryService.createHeadlessInquiry(failureString.toString(), "gpt-4o", false);
        InquiryChat inquiryChat = inquiry.getChats().get(inquiry.getChats().size() - 1);
        //Code is surrounded by "```" to indicate a code block. Isolate this code:
        String startOfCode = inquiryChat.getMessage().substring(inquiryChat.getMessage().indexOf("```") + 3);
        String newUnitTestCode = startOfCode.substring(0, startOfCode.indexOf("```"));
        if (newUnitTestCode.startsWith("java")) {
            newUnitTestCode = newUnitTestCode.substring(4);
        }
        if (newUnitTestCode.startsWith("\n")) {
            newUnitTestCode = newUnitTestCode.substring(1);
        }
        String unitTestFilePath = filePathToResultMap.keySet().toArray()[0].toString();
        System.out.println("Replacing unit test");
        System.out.println("Unit test file path: " + unitTestFilePath);
        String unitTestCode = codeSnippetExtractorService.getAllText(unitTestFilePath);
        System.out.println("Old code: " + unitTestCode);
        System.out.println("New code: " + newUnitTestCode);
        rangeReplaceService.replaceRange(unitTestFilePath,0, unitTestCode.length(), newUnitTestCode, true);
        return new ReplacedClassInfoResource.Builder()
                .withFilePath(unitTestFilePath)
                .withOldCode(unitTestCode)
                .withNewCode(newUnitTestCode)
                .withFormerResults(filePathToResultMap)
                .build();
    }

    public boolean isUnitTestCulpable(String implementationCode, Map<String, Result> filePathToResultMap) {
        StringBuilder failureString = assembleFailureString(implementationCode, filePathToResultMap);
        failureString.append("What needs to be changed? A. The Unit Test, or B. The Implementation");

        // Create ChatGptFunction for "choose_between_a_or_b"
        Parameters chooseBetweenAorBParams = new Parameters("object");

        // Create Property for "choice" with options "A" or "B"
        Property choiceProperty = new Property(
                "string",
                "Select either 'A' or 'B'",
                Arrays.asList("A", "B"),
                null
        );

        // Create Property for "reasoning"
        Property reasoningProperty = new Property(
                "string",
                "Provide reasoning for your choice",
                null,
                null
        );

        // Add properties to parameters
        chooseBetweenAorBParams.getProperties().put("choice", choiceProperty);
        chooseBetweenAorBParams.getProperties().put("reasoning", reasoningProperty);

        // Mark properties as required
        chooseBetweenAorBParams.getRequired().add("choice");
        chooseBetweenAorBParams.getRequired().add("reasoning");

        // Create and add the GptFunction
        GptFunction chooseBetweenAorB = new GptFunction(
                "choose_between_a_or_b",
                "Make a selection between 'A' or 'B' and provide a reasoning for the choice",
                chooseBetweenAorBParams
        );

        List<GptFunction> chooseBetweenAorBList = new ArrayList<>();
        chooseBetweenAorBList.add(chooseBetweenAorB);

        Inquiry inquiry = inquiryService.createHeadlessInquiry(failureString.toString(), "gpt-4o", chooseBetweenAorBList);
        InquiryChat inquiryChat = inquiry.getChats().get(inquiry.getChats().size() - 1);
        GptFunctionCall gptFunctionCall = inquiryChat.getFunctionCall();
        String choice = JsonExtractorService.extractField(gptFunctionCall.getArguments(), "choice");
        assert choice != null;
        return choice.equals("A");
    }

    private StringBuilder assembleFailureString(String implementationCode, Map<String, Result> filePathToResultMap) {
        Map<String, Result> failedResults = new HashMap<>();
        Map<String, Result> passedResults = new HashMap<>();
        for (String filePath : filePathToResultMap.keySet()) {
            Result result = filePathToResultMap.get(filePath);
            if (result.wasSuccessful()) {
                passedResults.put(filePath, result);
            } else {
                failedResults.put(filePath, result);
            }
        }
        StringBuilder failureString = new StringBuilder();
        String failedResultPath = failedResults.keySet().toArray()[0].toString();
        Result failedResult = failedResults.get(failedResultPath);
        failureString.append("This code: ").append(implementationCode).append(" passed (")
                .append(passedResults.size())
                .append("/")
                .append(filePathToResultMap.size())
                .append(") tests. The following is a test that failed: \n")
                .append("Test code:\n")
                .append(codeSnippetExtractorService.getAllText(failedResultPath))
                .append("\n")
                .append("Failures:\n");
        for (Failure failure: failedResult.getFailures()) {
            failureString.append(failure.getMessage());
        }
        return failureString;
    }
}
