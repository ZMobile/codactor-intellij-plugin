package com.translator.service.codactor.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Stack;

public class JsonExtractorService {
    public static String extractJsonObject(String input) {
        if (input == null) {
            return null;
        }
        Stack<Character> stack = new Stack<>();
        int startIndex = -1;
        int endIndex = -1;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (currentChar == '{') {
                if (stack.isEmpty()) {
                    startIndex = i;
                }
                stack.push(currentChar);
            } else if (currentChar == '}') {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
                if (stack.isEmpty()) {
                    endIndex = i;
                    break;
                }
            }
        }

        if (startIndex != -1 && endIndex != -1) {
            return input.substring(startIndex, endIndex + 1);
        }

        return null;
    }

    public static String extractField(String jsonString, String fieldName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonString);
            if (rootNode.has(fieldName)) {
                return rootNode.get(fieldName).asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
