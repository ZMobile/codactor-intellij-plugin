package com.translator.service.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileCreatorServiceImpl implements FileCreatorService {

    public List<File> createFilesFromInput(String directoryPath, String input) {
        List<File> files = new ArrayList<>();
        List<String> commands = extractCommands(input);


        commands.forEach(command -> {
            List<String> fileNames = extractFileNames(command);

            if (directoryPath != null && !fileNames.isEmpty()) {
                fileNames.forEach(fileName -> {
                    try {
                        files.add(createFile(directoryPath, fileName));
                    } catch (IOException e) {
                        System.err.println("Error creating file: " + fileName);
                        e.printStackTrace();
                    }
                });
            }
        });
        return files;
    }

    private List<String> extractCommands(String input) {
        Pattern commandPattern = Pattern.compile("```([^`]+)```");
        Matcher matcher = commandPattern.matcher(input);

        List<String> commands = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.group(1).contains("touch") && matcher.group(1).contains(".")) {
                commands.add(matcher.group(1));
            }
        }

        return commands;
    }

    private List<String> extractFileNames(String command) {
        Pattern fileNamePattern = Pattern.compile("touch ((?:[^\\s]+\\s*)+)");
        Matcher matcher = fileNamePattern.matcher(command);
        List<String> fileNames = new ArrayList<>();

        if (matcher.find()) {
            String fileNamesGroup = matcher.group(1);
            List<String> fileNamesList = Arrays.asList(fileNamesGroup.trim().split("\\s+"));
            for (String fileName : fileNamesList) {
                if (fileName.contains(".") && !fileNames.contains(fileName)) {
                    File file = new File(fileName);
                    fileNames.add(file.getName());
                }
            }
        }

        return fileNames;
    }

    private File createFile(String directoryPath, String fileName) throws IOException {
        Files.createDirectories(Paths.get(directoryPath));
        File file = new File(directoryPath, fileName);
        //Create the directory if it doesn't exist
        System.out.println("Creating file: " + file.getAbsolutePath());
        if (file.getParentFile().mkdirs()) {
            System.out.println("Directory created: " + file.getParentFile().getAbsolutePath());
        } else {
            System.out.println("Directory already exists: " + file.getParentFile().getAbsolutePath());
        }
        if (file.createNewFile()) {
            System.out.println("File created: " + file.getAbsolutePath());
        } else {
            System.out.println("File already exists: " + file.getAbsolutePath());
        }
        return file;
    }
}