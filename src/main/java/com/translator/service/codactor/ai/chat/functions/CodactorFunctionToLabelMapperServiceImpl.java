package com.translator.service.codactor.ai.chat.functions;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.inject.Inject;
import com.translator.model.codactor.ai.chat.InquiryChat;
import com.translator.model.codactor.ai.chat.function.GptFunctionCall;
import com.translator.service.codactor.json.JsonExtractorService;

public class CodactorFunctionToLabelMapperServiceImpl implements CodactorFunctionToLabelMapperService {
    private Gson gson;

    @Inject
    public CodactorFunctionToLabelMapperServiceImpl(Gson gson) {
        this.gson = gson;
    }

    public String fixMalformedJson(String jsonString) {
        String fixedJson = jsonString.replace("\" +\n\"", "");
        // Return the fixed JSON
        return fixedJson;
    }
    /*
    Before:{
  "modificationType": "modify",
  "description": "Provide a function snippet that fixes a malformed json due to multi line code",
  "replacementCodeSnippet":
"public String fixMalformedJson(String jsonString) {\n" +
"    // Pattern to find any strings that have newline characters in them\n" +
"    Pattern pattern = Pattern.compile(\"\\\\\\\"([^\\\\\\\"]*?\\\\n[^\\\\\\\"]*?)\\\\\\\"\");\n" +
"    Matcher matcher = pattern.matcher(jsonString);\n" +
"\n" +
"    // Replace any newline characters within strings with a special token\n" +
"    String fixedJson = matcher.replaceAll(matcherResult -> matcherResult.group().replace('\\n', ' '));\n" +
"    // Return the fixed JSON\n" +
"    return fixedJson;\n" +
"}",
  "startBoundary": "",
  "endBoundary": "",
  "path": "/path/to/your/File.java"
}
After: {
  "modificationType": "modify",
  "description": "Provide a function snippet that fixes a malformed json due to multi line code",
  "replacementCodeSnippet":
"public String fixMalformedJson(String jsonString) {\n" +
"    // Pattern to find any strings that have newline characters in them\n" +
"    Pattern pattern = Pattern.compile(\"\\\\\\\"([^\\\\\\\"]*?\\\\n[^\\\\\\\"]*?)\\\\\\\"\");\n" +
"    Matcher matcher = pattern.matcher(jsonString);\n" +
"\n" +
"    // Replace any newline characters within strings with a special token\n" +
"    String fixedJson = matcher.replaceAll(matcherResult -> matcherResult.group().replace('\\n', ' '));\n" +
"    // Return the fixed JSON\n" +
"    return fixedJson;\n" +
"}",
  "startBoundary": "",
  "endBoundary": "",
  "path": "/path/to/your/File.java"
}
     */

    @Override
    public String getLabel(InquiryChat inquiryChat) {
        if (inquiryChat.getFrom().equalsIgnoreCase("function")) {
            if (inquiryChat.getMessage() == null) {
                return "Error";
            } else if (inquiryChat.getMessage().toLowerCase().startsWith("error")) {
                return inquiryChat.getMessage();
            } else {
                return null;
            }
        }
        GptFunctionCall functionCall = inquiryChat.getFunctionCall();
        if (functionCall.getName().equals("get_project_base_path")) {
            return "Reading project base path...";
        } else if (functionCall.getName().equals("read_current_selected_file_in_editor")) {
            return "Reading current selected file in editor...";
        } else if (functionCall.getName().equals("read_current_selected_files_in_tree_view")) {
            return "Viewing current selected file in tree view...";
        } else if (functionCall.getName().equals("read_directory_structure_at_path")) {
            String filePath = JsonExtractorService.extractField(functionCall.getArguments(), "path");
            return "Reading directory structure at " + filePath + "...";
        } else if (functionCall.getName().equals("read_file_at_path")) {
            String filePath = JsonExtractorService.extractField(functionCall.getArguments(), "path");
            return "Reading file at " + filePath + "...";
        } else if (functionCall.getName().equals("open_file_at_path_for_user")) {
            String filePath = JsonExtractorService.extractField(functionCall.getArguments(), "path");
            return "Opening file at " + filePath + "...";
        } else if (functionCall.getName().equals("read_file_at_package")) {
            String packageName = JsonExtractorService.extractField(functionCall.getArguments(), "package");
            return "Reading package at " + packageName + "...";
        } else if (functionCall.getName().equals("get_recent_historical_modifications")) {
            return "Retrieving recent modifications...";
        } else if (functionCall.getName().equals("read_modification")) {
            String id = JsonExtractorService.extractField(functionCall.getArguments(), "id");
            return "Reading modification id " + id + "...";
        } else if (functionCall.getName().equals("get_queued_modifications")) {
            return "Retrieving queued modifications...";
        } else if (functionCall.getName().equals("read_modification_in_queue_at_position")) {
            String position = JsonExtractorService.extractField(functionCall.getArguments(), "position");
            return "Reading modification in queue at position " + position + "...";
        } else if (functionCall.getName().equals("get_recent_historical_inquiries")) {
            return "Retrieving recent inquiries...";
        } else if (functionCall.getName().equals("read_inquiry")) {
            String id = JsonExtractorService.extractField(functionCall.getArguments(), "id");
            return "Reading inquiry id " + id + "...";
        } else if (functionCall.getName().equals("retry_modification_in_queue")) {
            String id = JsonExtractorService.extractField(functionCall.getArguments(), "id");
            return "Retrying modification id " + id + "...";
        } else if (functionCall.getName().equals("remove_modification_in_queue")) {
        String id = JsonExtractorService.extractField(functionCall.getArguments(), "id");
        return "Removing modification id " + id + "...";
        } else if (functionCall.getName().equals("request_file_modification")) {
            System.out.println("Testooooo arguments: " + functionCall.getArguments());
            String location;
            try {
                location = JsonExtractorService.extractField(functionCall.getArguments(), "path");
            } catch (JsonParseException e) {
                functionCall.setArguments(fixMalformedJson(functionCall.getArguments()));
                location = JsonExtractorService.extractField(functionCall.getArguments(), "path");
            }
            if (location == null) {
                location = JsonExtractorService.extractField(functionCall.getArguments(), "package");
            }
            if (location != null)
            return "Requesting file modification for " + location + "...";
        /*} else if (functionCall.getName().equals("request_file_modification_and_wait_for_response")) {
            String path = JsonExtractorService.extractField(functionCall.getArguments(), "path");
            return "Requesting file modification for " + path + " and waiting for response...";*/
        } else if (functionCall.getName().equals("redo_file_modification")) {
            String id = JsonExtractorService.extractField(functionCall.getArguments(), "id");
            return "Redoing modification id " + id + "...";
        } else if (functionCall.getName().equals("request_file_creation")) {
            String path = JsonExtractorService.extractField(functionCall.getArguments(), "path");
            return "Requesting file creation for " + path + "...";
        } else if (functionCall.getName().equals("request_file_creation_and_wait_for_response")) {
            String path = JsonExtractorService.extractField(functionCall.getArguments(), "path");
            return "Requesting file creation for " + path + " and waiting for response...";
        } else if (functionCall.getName().equals("request_file_deletion")) {
            String path = JsonExtractorService.extractField(functionCall.getArguments(), "path");
            return "Requesting file deletion of " + path + "...";
        } else if (functionCall.getName().equals("run_program")) {
            String path = JsonExtractorService.extractField(functionCall.getArguments(), "path");
            return "Running program " + path + "...";
        } else if (functionCall.getName().equals("project_text_search")) {
            String query = JsonExtractorService.extractField(functionCall.getArguments(), "query");
            return "Running project text search for \"" + query + "\"...";
        } else if (functionCall.getName().equals("find_declarations_or_usages_of_code")) {
            String path = JsonExtractorService.extractField(functionCall.getArguments(), "path");
            return "Finding Usages for code snippet in \"" + path + "\"...";
        } else if (functionCall.getName().equals("find_implementations_of_code")) {
            String path = JsonExtractorService.extractField(functionCall.getArguments(), "path");
            return "Finding Implementations for code snippet in \"" + path + "\"...";
        } else if (functionCall.getName().equals("find_compile_time_errors_in_code")) {
            String path = JsonExtractorService.extractField(functionCall.getArguments(), "path");
            return "Finding Errors for code snippet in \"" + path + "\"...";
        }
        return "Running function " + functionCall.getName() + "...";
    }
}
