package com.translator.service.codactor.ai.modification.test.junit;

import com.translator.model.codactor.ai.chat.Inquiry;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.model.codactor.ai.modification.test.UnitTestData;
import com.translator.service.codactor.ai.chat.inquiry.InquiryService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnitTestListGeneratorServiceImpl implements UnitTestListGeneratorService {
    private final InquiryService inquiryService;

    @Inject
    public UnitTestListGeneratorServiceImpl(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    public List<UnitTestData> generateUnitTestList(Inquiry inquiry, InquiryChat interfaceInquiryChat) {
        System.out.println("Generating unit test list...");
        String question = "Under the principle of Test Driven Development, what unit tests should be developed to make sure this code works? Please list their names and describe them";
        InquiryChat inquiryChat = inquiryService.continueHeadlessInquiry(inquiry, interfaceInquiryChat.getId(), question, "gpt-4o", false);
        //Break down the response into a list of UnitTestData.
        String fullMessage = inquiryChat.getMessage();
        System.out.println("Full message: " + fullMessage);
        return parseUnitTestData(fullMessage);
    }

    private List<UnitTestData> parseUnitTestData(String message) {
        List<UnitTestData> unitTestDataList = new ArrayList<>();

        // Use regex to find each test block starting with a number
        Pattern pattern = Pattern.compile("(\\d+)\\.\\s*(.+?)\\n+((?:[^\\n]+\\n?)*)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            // Extract the test name and description
            String testName = matcher.group(2).trim();

            // Remove all non-alphanumeric characters
            testName = testName.replaceAll("[^a-zA-Z0-9]", "");

            // Ensure the testName follows the desired format
            if (testName.startsWith("test")) {
                testName = testName.substring(4);
                testName += "Test";
            }

            // Check if the first letter is lowercase and make it uppercase
            if (!testName.isEmpty() && Character.isLowerCase(testName.charAt(0))) {
                testName = Character.toUpperCase(testName.charAt(0)) + testName.substring(1);
            }

            String testDescription = matcher.group(3).trim(); // Group 3 is the description

            // Create a UnitTestData object and add it to the list
            UnitTestData unitTestData = new UnitTestData(testName, testDescription);
            unitTestData.setName(testName);
            unitTestData.setDescription(testDescription);
            unitTestDataList.add(unitTestData);
        }

        return unitTestDataList;
    }
}
