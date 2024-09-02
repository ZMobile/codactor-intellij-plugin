package com.translator.service.codactor.ai.chat.functions;

import com.translator.model.codactor.ai.chat.function.GptFunction;
import com.translator.model.codactor.ai.chat.function.Parameters;
import com.translator.model.codactor.ai.chat.function.Property;

import java.util.ArrayList;
import java.util.List;

public class CodactorFunctionGeneratorServiceImpl implements CodactorFunctionGeneratorService {

    @Override
    public List<GptFunction> generateCodactorFunctions() {
        List<GptFunction> codactorFunctions = new ArrayList<>();

        Parameters getProjectBasePathParams = new Parameters("object");
        GptFunction getProjectBasePath = new GptFunction("get_project_base_path", "Get the base path of the current opened project", getProjectBasePathParams);
        codactorFunctions.add(getProjectBasePath);

        // Create ChatGptFunction for "read_current_selected_file_in_editor"
        Parameters readCurrentSelectedFileInEditorParams = new Parameters("object");
        GptFunction readCurrentSelectedFileInEditor = new GptFunction("read_current_selected_file_in_editor", "Read the contents and file path of the currently selected code file in the Intellij code editor", readCurrentSelectedFileInEditorParams);
        codactorFunctions.add(readCurrentSelectedFileInEditor);

        // Create ChatGptFunction for "read_current_selected_files_in_tree_view"
        Parameters readCurrentSelectedFileInTreeViewParams = new Parameters("object");
        GptFunction readCurrentSelectedFileInTreeView = new GptFunction("read_current_selected_files_in_tree_view", "Read the file path of the currently selected file or directory in the Intellij directory tree view", readCurrentSelectedFileInTreeViewParams);
        codactorFunctions.add(readCurrentSelectedFileInTreeView);

        // Create ChatGptFunction for "read_file_at_path"
        Parameters readFileAtPathParams = new Parameters("object");
        Property pathProperty = new Property("string", "The file path of the code eg. /Users/user/IdeaProjects/code_project/src/code.java", null, null);
        readFileAtPathParams.getProperties().put("path", pathProperty);
        readFileAtPathParams.getRequired().add("path");

        GptFunction readFileAtPath = new GptFunction("read_file_at_path", "Read the contents of a code or text file given its path", readFileAtPathParams);
        codactorFunctions.add(readFileAtPath);

        // Create ChatGptFunction for "open_file_at_path_for_user"
        Parameters openFileAtPathInEditorParams = new Parameters("object");
        openFileAtPathInEditorParams.getProperties().put("path", pathProperty);
        openFileAtPathInEditorParams.getRequired().add("path");

        GptFunction openFileAtPathInEditor = new GptFunction("open_file_at_path_for_user", "Open a code file in the Intellij code editor given its path", openFileAtPathInEditorParams);
        codactorFunctions.add(openFileAtPathInEditor);

        /*//Create ChatGptFunction for "find_declarations_or_usages_of_code"
        Parameters findUsagesOfCodeParams = new Parameters("object");
        findUsagesOfCodeParams.getProperties().put("path", pathProperty);
        findUsagesOfCodeParams.getRequired().add("path");
        Property codeSnippetProperty = new Property("string", "A snippet of code from within the file marking the start and end of the selection.", null, null);
        findUsagesOfCodeParams.getProperties().put("codeSnippet", codeSnippetProperty);
        findUsagesOfCodeParams.getRequired().add("codeSnippet");

        ChatGptFunction findUsagesOfCode = new ChatGptFunction("find_declarations_or_usages_of_code", "Find declarations or usages of a code snippet within a file", findUsagesOfCodeParams);
        codactorFunctions.add(findUsagesOfCode);

        //Create ChatGptFunction for "find_implementations_of_code"
        Parameters findImplementationsOfCodeParams = new Parameters("object");
        findImplementationsOfCodeParams.getProperties().put("path", pathProperty);
        findImplementationsOfCodeParams.getRequired().add("path");
        findImplementationsOfCodeParams.getProperties().put("codeSnippet", codeSnippetProperty);
        findImplementationsOfCodeParams.getRequired().add("codeSnippet");

        ChatGptFunction findImplementationsOfCode = new ChatGptFunction("find_implementations_of_code", "Find implementations of methods in a code snippet within a file", findImplementationsOfCodeParams);
        codactorFunctions.add(findImplementationsOfCode);

        //Create ChatGptFunction for "find_compile_time_errors_in_code"
        Parameters findErrorsInCodeParams = new Parameters("object");
        findErrorsInCodeParams.getProperties().put("path", pathProperty);
        findErrorsInCodeParams.getRequired().add("path");
        findErrorsInCodeParams.getProperties().put("codeSnippet", codeSnippetProperty);
        findErrorsInCodeParams.getRequired().add("codeSnippet");
        Property includeWarningsProperty = new Property("boolean", "Whether to include warnings in the results", null, null);
        findErrorsInCodeParams.getProperties().put("includeWarnings", includeWarningsProperty);

        ChatGptFunction findErrorsInCode = new ChatGptFunction("find_compile_time_errors_in_code", "Find compile-time errors of a code snippet within a file", findErrorsInCodeParams);
        codactorFunctions.add(findErrorsInCode);*/

        // Create ChatGptFunction for "read_file_at_package"
        /*Parameters readFileAtPackageParams = new Parameters("object");
        Property packageProperty = new Property("string", "The class package of the code file e.g. com.translator.view.uml.node.dialog.prompt", null, null);
        readFileAtPackageParams.getProperties().put("package", packageProperty);
        readFileAtPackageParams.getRequired().add("package");

        GptFunction readFileAtPackage = new GptFunction("read_file_at_package", "Read the contents and file path of a code or text file given its package in the project directory", readFileAtPackageParams);
        codactorFunctions.add(readFileAtPackage);*/

        Parameters projectTextSearchParams = new Parameters("object");
        Property queryProperty = new Property("string", "The string to run an Intellij search query on", null, null);
        projectTextSearchParams.getProperties().put("query", queryProperty);
        Property pageProperty = new Property("integer", "The page of the query", null, null);
        projectTextSearchParams.getProperties().put("page", pageProperty);
        Property pageSizeProperty = new Property("integer", "The page size of the query", null, null);
        projectTextSearchParams.getProperties().put("pageSize", pageSizeProperty);
        projectTextSearchParams.getRequired().add("query");

        GptFunction projectTextSearch = new GptFunction("project_text_search", "Run a text search for files and contents throughout the currently open project and return the results. If page and pageSize are null, it will return the first 10 results. Powered by Intellij's PsiSearchHelper", projectTextSearchParams);
        codactorFunctions.add(projectTextSearch);

        // Create ChatGptFunction for "get_recent_historical_modifications"
        Parameters getHistoricalModificationParams = new Parameters("object");
        GptFunction getRecentHistoricalModifications = new GptFunction("get_recent_historical_modifications", "Get a list of recent historical modification ids", getHistoricalModificationParams);
        codactorFunctions.add(getRecentHistoricalModifications);

        Property fileModificationIdProperty = new Property("string", "The id of the file modification", null, null);

        // Create ChatGptFunction for "read_modification"
        Parameters readModificationParams = new Parameters("object");
        readModificationParams.getProperties().put("id", fileModificationIdProperty);
        readModificationParams.getRequired().add("id");
        GptFunction readModificationPosition = new GptFunction("read_modification", "Read the contents of a file modification given its id", readModificationParams);
        codactorFunctions.add(readModificationPosition);

        // Create ChatGptFunction for "get_recent_historical_inquiries"
        Parameters getHistoricalInquiryParams = new Parameters("object");
        GptFunction getRecentHistoricalInquiries = new GptFunction("get_recent_historical_inquiries", "Get a list of recent historical inquiry ids", getHistoricalInquiryParams);
        codactorFunctions.add(getRecentHistoricalInquiries);

        Property inquiryIdProperty = new Property("string", "The id of the inquiry", null, null);

        // Create ChatGptFunction for "read_inquiry"
        Parameters readInquiryParams = new Parameters("object");
        readInquiryParams.getProperties().put("id", inquiryIdProperty);
        readInquiryParams.getRequired().add("id");
        GptFunction readInquiry = new GptFunction("read_modification", "Read the contents of a modification given its id", readModificationParams);
        codactorFunctions.add(readInquiry);

        // Create ChatGptFunction for "read_directory_structure_at_path"
        Parameters readDirectoryStructureAtPathParams = new Parameters("object");
        Property directoryPathProperty = new Property("string", "The path of the directory eg. /Users/user/IdeaProjects/code_project/src", null, null);
        Property depthProperty = new Property("integer", "The depth of the directory structure returned. If set to 0, it will just return the files and directories immediately inside of the folder at the provided path. If set to 1, it will also return the files and directories immediately inside of its child directories one level deep, and so on.", null, null);
        readDirectoryStructureAtPathParams.getProperties().put("path", directoryPathProperty);
        readDirectoryStructureAtPathParams.getProperties().put("depth", depthProperty);
        readDirectoryStructureAtPathParams.getRequired().add("path");
        readDirectoryStructureAtPathParams.getRequired().add("depth");

        GptFunction readDirectoryStructureAtPath = new GptFunction("read_directory_structure_at_path", "Read the file directory structure at the provided path", readDirectoryStructureAtPathParams);
        codactorFunctions.add(readDirectoryStructureAtPath);

        // Create ChatGptFunction for "get_queued_modifications"
        Parameters getQueuedModificationIdsParams = new Parameters("object");
        GptFunction getQueuedModificationIds = new GptFunction("get_queued_modifications", "Get the list of queued modification ids", getQueuedModificationIdsParams);
        codactorFunctions.add(getQueuedModificationIds);

        // Create ChatGptFunction for "read_modification_in_queue_at_position"
        Parameters readModificationInQueueAtPositionParams = new Parameters("object");
        Property positionProperty = new Property("integer", "The position of the file modification in the queue", null, null);
        readModificationInQueueAtPositionParams.getProperties().put("position", positionProperty);
        readModificationInQueueAtPositionParams.getRequired().add("position");
        GptFunction readModificationInQueueAtPosition = new GptFunction("read_modification_in_queue_at_position", "Read the contents of a queued modification given its position in the queue", readModificationInQueueAtPositionParams);
        codactorFunctions.add(readModificationInQueueAtPosition);

        // Create ChatGptFunction for "retry_modification_in_queue"
        Parameters retryModificationInQueueParams = new Parameters("object");
        retryModificationInQueueParams.getProperties().put("id", fileModificationIdProperty);
        retryModificationInQueueParams.getRequired().add("id");

        GptFunction retryModificationInQueue = new GptFunction("retry_modification_in_queue", "Retry a queued modification", retryModificationInQueueParams);
        codactorFunctions.add(retryModificationInQueue);

        // Create ChatGptFunction for "remove_modification_from_queue"
        Parameters removeModificationInQueueParams = new Parameters("object");
        removeModificationInQueueParams.getProperties().put("id", fileModificationIdProperty);
        removeModificationInQueueParams.getRequired().add("id");

        GptFunction removeModificationInQueue = new GptFunction("remove_modification_in_queue", "Remove a queued modification", removeModificationInQueueParams);
        codactorFunctions.add(removeModificationInQueue);

        // Create ChatGptFunction for "request_file_modification"
        Parameters requestFileModificationParams = new Parameters("object");
        List<String> modificationTypes = new ArrayList<>();
        modificationTypes.add("modify");
        modificationTypes.add("fix");
        modificationTypes.add("create");
        Property startSnippetProperty = new Property("string", "A snippet of code from within the file marking the start boundary of the selection. Upon use, the start index of the modification will be at the start of this string. Ideally, the start snippet should include enough to make it unique so it can be used to find its location in the code with precision. If not utilized, it will automatically be set to the beginning of the file.", null, null);
        Property endSnippetProperty = new Property("string", "A snippet of code from within the file marking the end boundary of the selection. Upon use, the end index of the modification will be at the start of this string. Ideally the end snippet should include enough to make it unique so it can be used to find its location in the code with precision. If not utilized, it will automatically be set to the end of the file.", null, null);
        Property requestFileModificationCodeSnippetProperty = new Property("string", "A snippet of code from within the file marking the start and end of the selection. This property overrides the startSnippet and endSnippet properties--it should not be used start snippet or end snippet are used.", null, null);
        Property optionalPathProperty = new Property("string", "The path of the file to be modified. If not provided, the file modifier will attempt to find the file to be modified based on the provided description. Either this or the package must be provided for a modification", null, null);
        Property optionalPackageProperty = new Property("string", "The class package of the file to be modified. If not provided, the file modifier will attempt to find the file to be modified based on the provided description. Either this or the path must be provided for a modification", null, null);
        Property modificationTypeProperty = new Property("string", "The type of file modification types to choose from are 'modify' for modifying code in a file, 'fix' for reporting and fixing an error (like modify but for complaining), and 'create' for creating new code where none existed before, so it will ignore the startSnippet, endSnippet, and codeSnippet, placing its created code at index 0.", modificationTypes, null);
        Property descriptionProperty = new Property("string", "The description of the modification to be enacted on this specific code snippet provided within the boundaries", null, null);
        Property replacementCodeProperty = new Property("string", "The code to replace the target code snippet. This code must serve as a full replacement for the target code as it will be directly replacing the code snippet provided if accepted.", null, null);
        requestFileModificationParams.getProperties().put("path", optionalPathProperty);
        requestFileModificationParams.getProperties().put("package", optionalPackageProperty);
        requestFileModificationParams.getProperties().put("modificationType", modificationTypeProperty);
        requestFileModificationParams.getProperties().put("description", descriptionProperty);
        requestFileModificationParams.getProperties().put("startBoundary", startSnippetProperty);
        requestFileModificationParams.getProperties().put("endBoundary", endSnippetProperty);
        requestFileModificationParams.getProperties().put("codeSnippet", requestFileModificationCodeSnippetProperty);
        requestFileModificationParams.getProperties().put("replacementCodeSnippet", replacementCodeProperty);
        requestFileModificationParams.getRequired().add("modificationType");
        requestFileModificationParams.getRequired().add("description");
        requestFileModificationParams.getRequired().add("replacementCodeSnippet");

        GptFunction requestFileModification = new GptFunction("request_file_modification", "Request a new file modification at a specified file and optionally a specified range within the file. Warning: File modifications with overlapping ranges can not exist in the queue. File modifications can only exist in the same file if they don't overlap. A request for a modification with a range overlapping another existing modification currently in the queue will be automatically denied and return null.", requestFileModificationParams);
        codactorFunctions.add(requestFileModification);


        /*ChatGptFunction requestFileModificationAndWait = new ChatGptFunction("request_file_modification_and_wait_for_response", "Request a new file modification to be processed and wait for response", requestFileModificationParams);
        codactorFunctions.add(requestFileModificationAndWait);*/

        // Create ChatGptFunction for "request_file_creation"
        Parameters requestFileCreationParams = new Parameters("object");
        requestFileCreationParams.getProperties().put("path", pathProperty);
        requestFileCreationParams.getProperties().put("description", new Property("string", "Description of the new code file", null, null));
        requestFileCreationParams.getProperties().put("code",  new Property("string", "Contents of the new code file", null, null));
        requestFileCreationParams.getRequired().add("path");
        requestFileCreationParams.getRequired().add("description");
        requestFileCreationParams.getRequired().add("code");

        GptFunction requestFileCreation = new GptFunction("request_file_creation", "Request a new file to be created following a provided description that will be processed by the file modifier LLM", requestFileCreationParams);
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

        GptFunction requestFileDeletion = new GptFunction("request_file_deletion", "Request a file be deleted", requestFileDeletionParams);
        codactorFunctions.add(requestFileDeletion);

        // Create ChatGptFunction for "run_program"
        Parameters runProgramParams = new Parameters("object");
        Property programPathProperty = new Property("string", "The path of the code file eg. /path/to/python/script.py", null, null);
        Property interpreterProperty = new Property("string", "The interpreter language for the selected file eg. python", null, null);
        runProgramParams.getProperties().put("path", programPathProperty);
        runProgramParams.getProperties().put("interpreter", interpreterProperty);
        runProgramParams.getRequired().add("path");
        runProgramParams.getRequired().add("interpreter");

        GptFunction runProgram = new GptFunction("run_program", "Run a program file and read its command line output", runProgramParams);
        codactorFunctions.add(runProgram);

        Parameters createAndRunUnitTestParams = new Parameters("object");
        createAndRunUnitTestParams.getProperties().put("path", pathProperty);
        createAndRunUnitTestParams.getRequired().add("path");
        Property testDescriptionProperty = new Property("string", "The description of the test being created and what is being tested and/or the goal of the test", null, null);
        createAndRunUnitTestParams.getProperties().put("description", testDescriptionProperty);
        createAndRunUnitTestParams.getRequired().add("description");

        GptFunction createAndRunUnitTest = new GptFunction("create_and_run_unit_test", "Begin the process of creating and running a unit for a code file. It will be deleted at the conclusion of this process", createAndRunUnitTestParams);
        codactorFunctions.add(createAndRunUnitTest);

        return codactorFunctions;
    }
}