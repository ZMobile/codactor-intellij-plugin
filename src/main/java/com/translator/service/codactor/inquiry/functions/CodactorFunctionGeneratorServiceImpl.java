package com.translator.service.codactor.inquiry.functions;

import com.translator.model.codactor.inquiry.function.ChatGptFunction;
import com.translator.model.codactor.inquiry.function.Parameters;
import com.translator.model.codactor.inquiry.function.Property;

import java.util.ArrayList;
import java.util.List;

public class CodactorFunctionGeneratorServiceImpl implements CodactorFunctionGeneratorService {

    @Override
    public List<ChatGptFunction> generateCodactorFunctions() {
        List<ChatGptFunction> codactorFunctions = new ArrayList<>();

        // Create ChatGptFunction for "read_file_contents"
        Parameters readFileContentsParams = new Parameters("object");
        Property pathProperty = new Property("string", "The path of the code file eg. /Users/user/IdeaProjects/code_project/src/code.java", null, null);
        Property optionalPathProperty = new Property("string", "The path of the code file eg. /Users/user/IdeaProjects/code_project/src/code.java -Either this or the file package must be provided to locate a file.", null, null);
        Property optionalPackageProperty = new Property("string", "The package of the code file e.g. com.translator.view.uml.node.dialog.prompt -Either this or the file path must be provided to locate a file.", null, null);
        Property startIndexProperty = new Property("integer", "The start index of the code to be read in the file. Can be null which means 0", null, null);
        Property endIndexProperty = new Property("integer", "The start index of the code to be read in the file. Can be null which means the end of the code file", null, null);
        readFileContentsParams.getProperties().put("path", optionalPathProperty);
        readFileContentsParams.getProperties().put("package", optionalPackageProperty);
        readFileContentsParams.getProperties().put("startIndex", startIndexProperty);
        readFileContentsParams.getProperties().put("endIndex", endIndexProperty);

        ChatGptFunction readFileContents = new ChatGptFunction("read_file_contents", "Read the contents of a code or text file given its path", readFileContentsParams);
        codactorFunctions.add(readFileContents);

        // Create ChatGptFunction for "get_recent_historical_modifications"
        Parameters getHistoricalModificationParams = new Parameters("object");
        ChatGptFunction getRecentHistoricalModifications = new ChatGptFunction("get_recent_historical_modifications", "Get a list of recent historical modification ids", getHistoricalModificationParams);
        codactorFunctions.add(getRecentHistoricalModifications);

        Property fileModificationIdProperty = new Property("string", "The id of the file modification", null, null);

        // Create ChatGptFunction for "read_modification"
        Parameters readModificationParams = new Parameters("object");
        readModificationParams.getProperties().put("id", fileModificationIdProperty);
        readModificationParams.getRequired().add("id");
        ChatGptFunction readModificationPosition = new ChatGptFunction("read_modification", "Read the contents of a file modification given its id", readModificationParams);
        codactorFunctions.add(readModificationPosition);

        // Create ChatGptFunction for "get_recent_historical_inquiries"
        Parameters getHistoricalInquiryParams = new Parameters("object");
        ChatGptFunction getRecentHistoricalInquiries = new ChatGptFunction("get_recent_historical_inquiries", "Get a list of recent historical inquiry ids", getHistoricalInquiryParams);
        codactorFunctions.add(getRecentHistoricalInquiries);

        Property inquiryIdProperty = new Property("string", "The id of the inquiry", null, null);

        // Create ChatGptFunction for "read_inquiry"
        Parameters readInquiryParams = new Parameters("object");
        readInquiryParams.getProperties().put("id", inquiryIdProperty);
        readInquiryParams.getRequired().add("id");
        ChatGptFunction readInquiry = new ChatGptFunction("read_modification", "Read the contents of a modification given its id", readModificationParams);
        codactorFunctions.add(readInquiry);

        // Create ChatGptFunction for "read_directory_structure_at_path"
        Parameters readDirectoryStructureAtPathParams = new Parameters("object");
        Property directoryPathProperty = new Property("string", "The path of the directory eg. /Users/user/IdeaProjects/code_project/src", null, null);
        Property depthProperty = new Property("integer", "The depth of the directory structure returned. If set to 0, it will just return the files and directories immediately inside of the folder at the provided path. If set to 1, it will also return the files and directories immediately inside of its child directories one level deep, and so on.", null, null);
        readDirectoryStructureAtPathParams.getProperties().put("path", directoryPathProperty);
        readDirectoryStructureAtPathParams.getProperties().put("depth", depthProperty);
        readDirectoryStructureAtPathParams.getRequired().add("path");
        readDirectoryStructureAtPathParams.getRequired().add("depth");

        ChatGptFunction readDirectoryStructureAtPath = new ChatGptFunction("read_directory_structure_at_path", "Read the file directory structure at the provided path", readDirectoryStructureAtPathParams);
        codactorFunctions.add(readDirectoryStructureAtPath);

        // Create ChatGptFunction for "get_queued_modifications"
        Parameters getQueuedModificationIdsParams = new Parameters("object");
        ChatGptFunction getQueuedModificationIds = new ChatGptFunction("get_queued_modification_ids", "Get the list of queued modification ids", getQueuedModificationIdsParams);
        codactorFunctions.add(getQueuedModificationIds);

        // Create ChatGptFunction for "read_modification_in_queue_at_position"
        Parameters readModificationInQueueAtPositionParams = new Parameters("object");
        Property positionProperty = new Property("integer", "The position of the file modification in the queue", null, null);
        readModificationInQueueAtPositionParams.getProperties().put("position", positionProperty);
        readModificationInQueueAtPositionParams.getRequired().add("position");
        ChatGptFunction readModificationInQueueAtPosition = new ChatGptFunction("read_modification_in_queue_at_position", "Read the contents of a queued modification given its position in the queue", readModificationInQueueAtPositionParams);
        codactorFunctions.add(readModificationInQueueAtPosition);

        // Create ChatGptFunction for "retry_modification_in_queue"
        Parameters retryModificationInQueueParams = new Parameters("object");
        retryModificationInQueueParams.getProperties().put("id", fileModificationIdProperty);
        retryModificationInQueueParams.getRequired().add("id");

        ChatGptFunction retryModificationInQueue = new ChatGptFunction("retry_modification_in_queue", "Retry a queued modification", retryModificationInQueueParams);
        codactorFunctions.add(retryModificationInQueue);

        // Create ChatGptFunction for "remove_modification_from_queue"
        Parameters removeModificationInQueueParams = new Parameters("object");
        removeModificationInQueueParams.getProperties().put("id", fileModificationIdProperty);
        removeModificationInQueueParams.getRequired().add("id");

        ChatGptFunction removeModificationInQueue = new ChatGptFunction("remove_modification_in_queue", "Remove a queued modification", removeModificationInQueueParams);
        codactorFunctions.add(removeModificationInQueue);

        // Create ChatGptFunction for "request_file_modification"
        Parameters requestFileModificationParams = new Parameters("object");
        List<String> modificationTypes = new ArrayList<>();
        modificationTypes.add("modify");
        modificationTypes.add("fix");
        modificationTypes.add("create");
        Property modificationTypeProperty = new Property("string", "The type of file modification", modificationTypes, null);
        Property descriptionProperty = new Property("string", "The description of the requested file modification to be enacted on the file", null, null);
        requestFileModificationParams.getProperties().put("path", optionalPathProperty);
        requestFileModificationParams.getProperties().put("package", optionalPackageProperty);
        requestFileModificationParams.getProperties().put("modificationType", modificationTypeProperty);
        requestFileModificationParams.getProperties().put("description", descriptionProperty);
        requestFileModificationParams.getProperties().put("startIndex", startIndexProperty);
        requestFileModificationParams.getProperties().put("endIndex", endIndexProperty);
        requestFileModificationParams.getRequired().add("modificationType");
        requestFileModificationParams.getRequired().add("description");

        ChatGptFunction requestFileModification = new ChatGptFunction("request_file_modification", "Request a new file modification to be processed by the file modifier LLM", requestFileModificationParams);
        codactorFunctions.add(requestFileModification);

        ChatGptFunction requestFileModificationAndWait = new ChatGptFunction("request_file_modification_and_wait_for_response", "Request a new file modification to be processed and wait for response", requestFileModificationParams);
        codactorFunctions.add(requestFileModificationAndWait);

        // Create ChatGptFunction for "request_file_creation"
        Parameters requestFileCreationParams = new Parameters("object");
        requestFileCreationParams.getProperties().put("path", pathProperty);
        requestFileCreationParams.getProperties().put("description", descriptionProperty);
        requestFileCreationParams.getRequired().add("path");
        requestFileCreationParams.getRequired().add("description");

        ChatGptFunction requestFileCreation = new ChatGptFunction("request_file_creation", "Request a new file to be created following a provided description that will be processed by the file modifier LLM", requestFileCreationParams);
        codactorFunctions.add(requestFileCreation);

        // Create ChatGptFunction for "request_file_creation_and_wait_for_response"
        Parameters requestFileCreationAndWaitParams = new Parameters("object");
        requestFileCreationAndWaitParams.getProperties().put("path", pathProperty);
        requestFileCreationAndWaitParams.getProperties().put("description", descriptionProperty);
        requestFileCreationAndWaitParams.getRequired().add("path");
        requestFileCreationAndWaitParams.getRequired().add("description");

        ChatGptFunction requestFileCreationAndWait = new ChatGptFunction("request_file_creation_and_wait", "Request a new file to be created following a provided description that will be processed by the file modifier LLM, and wait to review the code it suggests.", requestFileCreationAndWaitParams);
        codactorFunctions.add(requestFileCreationAndWait);

        // Create ChatGptFunction for "request_file_deletion"
        Parameters requestFileDeletionParams = new Parameters("object");
        requestFileDeletionParams.getProperties().put("path", optionalPathProperty);
        requestFileDeletionParams.getProperties().put("package", optionalPackageProperty);
        requestFileDeletionParams.getProperties().put("description", descriptionProperty);
        requestFileDeletionParams.getRequired().add("description");

        ChatGptFunction requestFileDeletion = new ChatGptFunction("request_file_deletion", "Request a file be deleted", requestFileDeletionParams);
        codactorFunctions.add(requestFileDeletion);

        // Create ChatGptFunction for "run_program"
        Parameters runProgramParams = new Parameters("object");
        Property programPathProperty = new Property("string", "The path of the code file eg. /path/to/python/script.py", null, null);
        Property interpreterProperty = new Property("string", "The interpreter language for the selected file eg. python", null, null);
        runProgramParams.getProperties().put("path", programPathProperty);
        runProgramParams.getProperties().put("interpreter", interpreterProperty);
        runProgramParams.getRequired().add("path");
        runProgramParams.getRequired().add("interpreter");

        ChatGptFunction runProgram = new ChatGptFunction("run_program", "Run a program file and read its command line output", runProgramParams);
        codactorFunctions.add(runProgram);

        return codactorFunctions;
    }
}