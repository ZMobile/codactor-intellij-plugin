package com.translator.service.codactor.connection;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.translator.dao.firebase.FirebaseTokenService;

import javax.inject.Inject;


public class DefaultConnectionServiceImpl implements DefaultConnectionService {
    private FirebaseTokenService firebaseTokenService;
    private String openAiApiKey;

    @Inject
    public DefaultConnectionServiceImpl(FirebaseTokenService firebaseTokenService) {
        this.firebaseTokenService = firebaseTokenService;
    }

    @Override
    public String getOpenAiApiKey() {
        if (openAiApiKey != null) {
            return openAiApiKey;
        }
        CredentialAttributes credentialAttributes = new CredentialAttributes("openai_api_key", firebaseTokenService.getLoggedInUser());
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
        CredentialAttributes credentialAttributes = new CredentialAttributes("openai_api_key", firebaseTokenService.getLoggedInUser());
        Credentials credentials = new Credentials("", openAiApiKey);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }

    public String[] getModels() {
        return new String[]{"gpt-3.5-turbo", "gpt-3.5-turbo-16k", "gpt-4", "gpt-4-32k", "gpt-3.5", "gpt-3.5-turbo-0613", "gpt-4-0613", "gpt-4-32k-0613"};
    }
}
