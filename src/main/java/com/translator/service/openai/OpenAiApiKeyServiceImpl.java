package com.translator.service.openai;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class OpenAiApiKeyServiceImpl implements OpenAiApiKeyService {
    private String openAiApiKey;

    @Override
    public String getOpenAiApiKey() {
        if (openAiApiKey != null) {
            return openAiApiKey;
        }
        String userHome = System.getProperty("user.home");
        String codactorFolder = userHome + "/Codactor";
        File codactorFolderFile = new File(codactorFolder);
        if (!codactorFolderFile.exists()) {
            codactorFolderFile.mkdir();
        }
        String credentialsFolder = userHome + "/Codactor/credentials";
        File credentialsFolderFile = new File(credentialsFolder);
        if (!credentialsFolderFile.exists()) {
            credentialsFolderFile.mkdir();
        }
        String credentialsPath = userHome + "/Codactor/credentials/openaikey.txt";
        File file = new File(credentialsPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            String openAiApiKey = IOUtils.toString(FileUtils.openInputStream(file), StandardCharsets.UTF_8);
            if (openAiApiKey == null || openAiApiKey.isEmpty()) {
                return null;
            }
            this.openAiApiKey = openAiApiKey;
            return openAiApiKey;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setOpenAiApiKey(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
        //Write it in the file
        String userHome = System.getProperty("user.home");
        String codactorFolder = userHome + "/Codactor";
        File codactorFolderFile = new File(codactorFolder);
        if (!codactorFolderFile.exists()) {
            codactorFolderFile.mkdir();
        }
        String credentialsFolder = userHome + "/Codactor/credentials";
        File credentialsFolderFile = new File(credentialsFolder);
        if (!credentialsFolderFile.exists()) {
            credentialsFolderFile.mkdir();
        }
        String credentialsPath = userHome + "/Codactor/credentials/openaikey.txt";
        File file = new File(credentialsPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            //Write it in the file
            try (FileWriter writer = new FileWriter(credentialsPath)) {
                writer.write(openAiApiKey);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
