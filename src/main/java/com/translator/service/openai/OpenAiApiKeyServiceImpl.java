package com.translator.service.openai;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
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
        CredentialAttributes credentialAttributes = new CredentialAttributes("openai_api_key", "user");
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        String openAiApiKey = credentials != null ? String.valueOf(credentials.getPassword()) : null;
        if (openAiApiKey == null || openAiApiKey.isEmpty()) {
            return null;
        }
        this.openAiApiKey = openAiApiKey;
        return openAiApiKey;
    }

    public void setOpenAiApiKey(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
        //Write it in the file
        CredentialAttributes credentialAttributes = new CredentialAttributes("openai_api_key", "user");
        Credentials credentials = new Credentials("", openAiApiKey);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }
}
