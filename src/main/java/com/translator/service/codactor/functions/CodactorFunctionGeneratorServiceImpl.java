package com.translator.service.codactor.functions;

import com.translator.model.codactor.inquiry.function.ChatGptFunction;
import com.translator.model.codactor.inquiry.function.Parameters;
import com.translator.model.codactor.inquiry.function.Property;

import java.util.ArrayList;
import java.util.List;

public class CodactorFunctionGeneratorServiceImpl implements CodactorFunctionGeneratorService {

    @Override
    public List<ChatGptFunction> generateCodactorFunctions() {
        List<ChatGptFunction> codactorFunctions = new ArrayList<>();

        Parameters getProjectBasePathParams = new Parameters("object");
        ChatGptFunction getProjectBasePath = new ChatGptFunction("get_project_base_path", "Get the base path of the current opened project", getProjectBasePathParams);
        codactorFunctions.add(getProjectBasePath);

        // Create ChatGptFunction for "read_current_selected_file_in_editor"
        Parameters readCurrentSelectedFileInEditorParams = new Parameters("object");
        ChatGptFunction readCurrentSelectedFileInEditor = new ChatGptFunction("read_current_selected_file_in_editor", "Read the contents and file path of the currently selected code file in the Intellij code editor", readCurrentSelectedFileInEditorParams);
        codactorFunctions.add(readCurrentSelectedFileInEditor);


        // Create ChatGptFunction for "read_current_selected_file_in_tree_view"
        Parameters readCurrentSelectedFileInTreeViewParams = new Parameters("object");
        ChatGptFunction readCurrentSelectedFileInTreeView = new ChatGptFunction("read_current_selected_file_in_tree_view", "Read the file path of the currently selected file or directory in the Intellij directory tree view", readCurrentSelectedFileInTreeViewParams);
        codactorFunctions.add(readCurrentSelectedFileInTreeView);

        // Create ChatGptFunction for "read_file_at_path"
        Parameters readFileAtPathParams = new Parameters("object");
        Property pathProperty = new Property("string", "The path of the code file eg. /Users/user/IdeaProjects/code_project/src/code.java", null, null);
        readFileAtPathParams.getProperties().put("path", pathProperty);
        readFileAtPathParams.getRequired().add("path");

        ChatGptFunction readFileAtPath = new ChatGptFunction("read_file_at_path", "Read the contents of a code or text file given its path", readFileAtPathParams);
        codactorFunctions.add(readFileAtPath);

        // Create ChatGptFunction for "open_file_at_path_in_editor"
        Parameters openFileAtPathInEditorParams = new Parameters("object");
        openFileAtPathInEditorParams.getProperties().put("path", pathProperty);
        openFileAtPathInEditorParams.getRequired().add("path");

        ChatGptFunction openFileAtPathInEditor = new ChatGptFunction("open_file_at_path_in_editor", "Open a code file in the Intellij code editor given its path", openFileAtPathInEditorParams);
        codactorFunctions.add(openFileAtPathInEditor);

        // Create ChatGptFunction for "read_file_at_package"
        Parameters readFileAtPackageParams = new Parameters("object");
        Property packageProperty = new Property("string", "The class package of the code file e.g. com.translator.view.uml.node.dialog.prompt", null, null);
        readFileAtPackageParams.getProperties().put("package", packageProperty);
        readFileAtPackageParams.getRequired().add("package");

        ChatGptFunction readFileAtPackage = new ChatGptFunction("read_file_at_package", "Read the contents and file path of a code or text file given its package in the project directory", readFileAtPackageParams);
        codactorFunctions.add(readFileAtPackage);

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
        ChatGptFunction getQueuedModificationIds = new ChatGptFunction("get_queued_modification_s", "Get the list of queued modification ids", getQueuedModificationIdsParams);
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
        Property startSnippetProperty = new Property("string", "A snippet of code from within the file marking the start boundary of the selection. Upon use, the start index of the modification will be at the start of this string. Ideally, the start snippet should include enough to make it unique so it can be used to find its location in the code with precision. If not utilized, it will automatically be set to the beginning of the file.", null, null);
        Property endSnippetProperty = new Property("string", "A snippet of code from within the file marking the end boundary of the selection. Upon use, the end index of the modification will be at the start of this string. Ideally the end snippet should include enough to make it unique so it can be used to find its location in the code with precision. If not utilized, it will automatically be set to the end of the file.", null, null);
        Property codeSnippetProperty = new Property("string", "A snippet of code from within the file marking the start and end of the selection. This property overrides the startSnippet and endSnippet properties--it should not be used start snippet or end snippet are used.", null, null);
        Property optionalPathProperty = new Property("string", "The path of the file to be modified. If not provided, the file modifier will attempt to find the file to be modified based on the provided description. Either this or the package must be provided for a modification", null, null);
        Property optionalPackageProperty = new Property("string", "The class package of the file to be modified. If not provided, the file modifier will attempt to find the file to be modified based on the provided description. Either this or the path must be provided for a modification", null, null);
        Property modificationTypeProperty = new Property("string", "The type of file modification types to choose from are 'modify' for modifying code in a file, 'fix' for reporting and fixing an error (like modify but for complaining), and 'create' for creating new code where none existed before, so it will ignore the startSnippet, endSnippet, and codeSnippet, placing its created code at index 0.", modificationTypes, null);
        Property descriptionProperty = new Property("string", "The description of the requested file modification to be enacted on the file", null, null);
        requestFileModificationParams.getProperties().put("path", optionalPathProperty);
        requestFileModificationParams.getProperties().put("package", optionalPackageProperty);
        requestFileModificationParams.getProperties().put("modificationType", modificationTypeProperty);
        requestFileModificationParams.getProperties().put("description", descriptionProperty);
        requestFileModificationParams.getProperties().put("startBoundary", startSnippetProperty);
        requestFileModificationParams.getProperties().put("endBoundary", endSnippetProperty);
        requestFileModificationParams.getProperties().put("codeSnippet", codeSnippetProperty);
        requestFileModificationParams.getRequired().add("modificationType");
        requestFileModificationParams.getRequired().add("description");

        ChatGptFunction requestFileModification = new ChatGptFunction("request_file_modification", "Request a new file modification at a specified file and optionally a specified range within the file to be processed by the file modifier LLM. Ideally you should have the modification range encapsulate the modification and potentially needed context as its all the modifier LLM sees in addition to the description, though not too big as the modifier LLM has limited a context window. Warning: File modifications with overlapping ranges can not exist in the queue. File modifications can only exist in the same file if they don't overlap. A request for a modification with a range overlapping another existing modification currently in the queue will be automatically denied and return null.", requestFileModificationParams);
        codactorFunctions.add(requestFileModification);

        /*ChatGptFunction requestFileModificationAndWait = new ChatGptFunction("request_file_modification_and_wait_for_response", "Request a new file modification to be processed and wait for response", requestFileModificationParams);
        codactorFunctions.add(requestFileModificationAndWait);*/

        // Create ChatGptFunction for "request_file_creation"
        Parameters requestFileCreationParams = new Parameters("object");
        requestFileCreationParams.getProperties().put("path", pathProperty);
        requestFileCreationParams.getProperties().put("description", descriptionProperty);
        requestFileCreationParams.getRequired().add("path");
        requestFileCreationParams.getRequired().add("description");

        ChatGptFunction requestFileCreation = new ChatGptFunction("request_file_creation", "Request a new file to be created following a provided description that will be processed by the file modifier LLM", requestFileCreationParams);
        codactorFunctions.add(requestFileCreation);

        /* Create ChatGptFunction for "request_file_creation_and_wait_for_response"
        Parameters requestFileCreationAndWaitParams = new Parameters("object");
        requestFileCreationAndWaitParams.getProperties().put("path", pathProperty);
        requestFileCreationAndWaitParams.getProperties().put("description", descriptionProperty);
        requestFileCreationAndWaitParams.getRequired().add("path");
        requestFileCreationAndWaitParams.getRequired().add("description");

        ChatGptFunction requestFileCreationAndWait = new ChatGptFunction("request_file_creation_and_wait", "Request a new file to be created following a provided description that will be processed by the file modifier LLM, and wait to review the code it suggests.", requestFileCreationAndWaitParams);
        codactorFunctions.add(requestFileCreationAndWait);*/

        // Create ChatGptFunction for "request_file_deletion"
        Parameters requestFileDeletionParams = new Parameters("object");
        requestFileDeletionParams.getProperties().put("path", pathProperty);
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