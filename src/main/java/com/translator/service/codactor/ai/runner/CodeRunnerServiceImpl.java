package com.translator.service.codactor.ai.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;


public class CodeRunnerServiceImpl implements CodeRunnerService {
    public String runCode(String interpreter, String filePath) {
        List<String> command = Arrays.asList(interpreter, filePath);
        try {
            return runCode(command);
        } catch (IOException | InterruptedException e) {
            return new RuntimeException(e).toString();
        }
    }

    private String runCode(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        // Read the output from the command
        StringBuilder output = new StringBuilder();
        String s;
        while ((s = stdInput.readLine()) != null) {
            output.append(s).append("\n");
        }

        // Read any errors from the attempted command
        StringBuilder error = new StringBuilder();
        while ((s = stdError.readLine()) != null) {
            error.append(s).append("\n");
        }

        process.waitFor();

        if (process.exitValue() != 0) {
            return "Error occurred:\n" + error.toString();
        }

        return output.toString();
    }
}
