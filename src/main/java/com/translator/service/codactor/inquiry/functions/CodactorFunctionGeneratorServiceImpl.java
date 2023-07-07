package com.translator.service.codactor.inquiry.functions;

import com.translator.model.codactor.api.translator.inquiry.function.ChatGptFunction;
import com.translator.model.codactor.api.translator.inquiry.function.Parameters;
import com.translator.model.codactor.api.translator.inquiry.function.Property;

import java.util.ArrayList;
import java.util.List;

public class CodactorFunctionGeneratorServiceImpl implements CodactorFunctionGeneratorService {

    @Override
    public List<ChatGptFunction> generateCodactorFunctions() {
        List<ChatGptFunction> codactorFunctions = new ArrayList<>();

        // Create ChatGptFunction for "read_file_at_path"
        Parameters readFileAtPathParams = new Parameters("object");
        Property pathProperty = new Property("string", "The path of the code file eg. /Users/user/IdeaProjects/code_project/src/code.java", null, null);
        Property startIndexProperty = new Property("integer", "The start index of the code to be read in the file. Can be null which means 0", null, null);
        Property endIndexProperty = new Property("integer", "The start index of the code to be read in the file. Can be null which means the end of the code file", null, null);
        readFileAtPathParams.getProperties().put("path", pathProperty);
        readFileAtPathParams.getProperties().put("startIndex", startIndexProperty);
        readFileAtPathParams.getProperties().put("endIndex", endIndexProperty);
        readFileAtPathParams.getRequired().add("path");

        ChatGptFunction readFileAtPath = new ChatGptFunction("read_file_at_path", "Read the contents of a code or text file given its path", readFileAtPathParams);
        codactorFunctions.add(readFileAtPath);

        // Create ChatGptFunction for "read_file_at_package"
        Parameters readFileAtPackageParams = new Parameters("object");
        Property packageProperty = new Property("string", "The package of the code file e.g. com.translator.view.uml.node.dialog.prompt", null, null);
        Property startIndexProperty2 = new Property("integer", "The start index of the code to be read in the file. Can be null which means 0", null, null);
        Property endIndexProperty2 = new Property("integer", "The start index of the code to be read in the file. Can be null which means the end of the code file", null, null);
        readFileAtPackageParams.getProperties().put("package", packageProperty);
        readFileAtPackageParams.getProperties().put("startIndex", startIndexProperty2);
        readFileAtPackageParams.getProperties().put("endIndex", endIndexProperty2);
        readFileAtPackageParams.getRequired().add("package");

        ChatGptFunction readFileAtPackage = new ChatGptFunction("read_file_at_package", "Read the contents of a code or text file given its package in the project directory", readFileAtPackageParams);
        codactorFunctions.add(readFileAtPackage);

        // Create ChatGptFunction for "get_queued_modification_ids"
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

        // Create ChatGptFunction for "read_modification_in_queue"
        Parameters readModificationInQueueParams = new Parameters("object");
        Property idProperty = new Property("string", "The id of the file modification in the queue", null, null);
        readModificationInQueueParams.getProperties().put("id", idProperty);
        readModificationInQueueParams.getRequired().add("id");

        ChatGptFunction readModificationInQueue = new ChatGptFunction("read_modification_in_queue", "Read the contents of a queued modification given its id", readModificationInQueueParams);
        codactorFunctions.add(readModificationInQueue);

        // Create ChatGptFunction for "retry_modification_in_queue"
        Parameters retryModificationInQueueParams = new Parameters("object");
        retryModificationInQueueParams.getProperties().put("id", idProperty);
        retryModificationInQueueParams.getRequired().add("id");

        ChatGptFunction retryModificationInQueue = new ChatGptFunction("retry_modification_in_queue", "Retry a queued modification", retryModificationInQueueParams);
        codactorFunctions.add(retryModificationInQueue);

        // Create ChatGptFunction for "remove_modification_in_queue"
        Parameters removeModificationInQueueParams = new Parameters("object");
        removeModificationInQueueParams.getProperties().put("id", idProperty);
        removeModificationInQueueParams.getRequired().add("id");

        ChatGptFunction removeModificationInQueue = new ChatGptFunction("remove_modification_in_queue", "Remove a queued modification", removeModificationInQueueParams);
        codactorFunctions.add(removeModificationInQueue);

        // Create ChatGptFunction for "request_file_modification"
        Parameters requestFileModificationParams = new Parameters("object");
        Property modificationTypeProperty = new Property("string", "The type of file modification, e.g. modify, fix, create", null, null);
        Property descriptionProperty = new Property("string", "The description of the requested file modification to be enacted on the file", null, null);
        requestFileModificationParams.getProperties().put("path", pathProperty);
        requestFileModificationParams.getProperties().put("modificationType", modificationTypeProperty);
        requestFileModificationParams.getProperties().put("description", descriptionProperty);
        requestFileModificationParams.getProperties().put("startIndex", startIndexProperty);
        requestFileModificationParams.getProperties().put("endIndex", endIndexProperty);
        requestFileModificationParams.getRequired().add("path");
        requestFileModificationParams.getRequired().add("modificationType");
        requestFileModificationParams.getRequired().add("description");

        ChatGptFunction requestFileModification = new ChatGptFunction("request_file_modification", "Request a new file modification to be processed by the file modifier LLM", requestFileModificationParams);
        codactorFunctions.add(requestFileModification);

        // Create ChatGptFunction for "request_file_modification_and_wait_for_response"
        Parameters requestFileModificationAndWaitParams = new Parameters("object");
        requestFileModificationAndWaitParams.getProperties().put("path", pathProperty);
        requestFileModificationAndWaitParams.getProperties().put("modificationType", modificationTypeProperty);
        requestFileModificationAndWaitParams.getProperties().put("description", descriptionProperty);
        requestFileModificationAndWaitParams.getProperties().put("startIndex", startIndexProperty);
        requestFileModificationAndWaitParams.getProperties().put("endIndex", endIndexProperty);
        requestFileModificationAndWaitParams.getRequired().add("path");
        requestFileModificationAndWaitParams.getRequired().add("modificationType");
        requestFileModificationAndWaitParams.getRequired().add("description");

        ChatGptFunction requestFileModificationAndWait = new ChatGptFunction("request_file_modification_and_wait_for_response", "Request a new file modification to be processed and wait for response", requestFileModificationAndWaitParams);
        codactorFunctions.add(requestFileModificationAndWait);

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