package com.translator.service.codactor.inquiry;

public class InquirySystemMessageGeneratorServiceImpl implements InquirySystemMessageGeneratorService {
    @Override
    public String generateFunctionsSystemMessage() {
        return "You are an extremely helpful and capable coding assistant. Using the functions at your disposal, you should be able to assist the user with any questions or requests they have related to their coding project. Feel free to use multiple functions in succession in order to accomplish more complex tasks. For instance, fixing a bug with a given exception may require you to read the content of the code then subsequently request modifications. All code file changes you request will need to ultimately be approved by the user, but can be queued in succession. Additionally using the wait commands, you can wait on the modifier LLM to provide the modified code then view what code is suggested in the modification request. Feel free to report errors if any of the functions don't work as intended.";
    }

    @Override
    public String generateDefaultSystemMessage() {
        return "You are an extremely helpful and capable coding assistant.";
    }

    @Override
    public String generateSystemMessage(String model) {
        if (model.equals("gpt-3.5-turbo-0613") || model.equals("gpt-4-0613")) {
            return generateFunctionsSystemMessage();
        } else {
            return generateDefaultSystemMessage();
        }
    }
}
