package com.translator.service.codactor.connection;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.translator.service.codactor.account.AccountService;

import javax.inject.Inject;

public class AzureConnectionServiceImpl implements AzureConnectionService {
    private AccountService accountService;
    private CodactorConnectionService codactorConnectionService;

    @Inject
    public AzureConnectionServiceImpl(AccountService accountService,
                                      CodactorConnectionService codactorConnectionService) {
        this.accountService = accountService;
        this.codactorConnectionService = codactorConnectionService;
    }

    @Override
    public boolean isAzureConnected() {
        CodactorConnectionType codactorConnectionType = codactorConnectionService.getConnectionType();
        if (codactorConnectionType == null) {
            return false;
        }
        return codactorConnectionType == CodactorConnectionType.AZURE || codactorConnectionType == CodactorConnectionType.ENTERPRISE;
    }

    @Override
    public String getKey() {
        CredentialAttributes credentialAttributes = new CredentialAttributes("azure_api_key", accountService.getLoggedInUser());
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        String azureApiKey = credentials != null ? String.valueOf(credentials.getPassword()) : null;
        if (azureApiKey == null || azureApiKey.isEmpty()) {
            return null;
        }
        return azureApiKey;
    }

    @Override
    public void setKey(String key) {
        CredentialAttributes credentialAttributes = new CredentialAttributes("azure_api_key", accountService.getLoggedInUser());
        Credentials credentials = new Credentials(accountService.getLoggedInUser(), key);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }


    @Override
    public void setResource(String resource) {
        CredentialAttributes credentialAttributes = new CredentialAttributes("azure_api_resource", accountService.getLoggedInUser());
        Credentials credentials = new Credentials(accountService.getLoggedInUser(), resource);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }

    @Override
    public String getResource() {
        CredentialAttributes credentialAttributes = new CredentialAttributes("azure_api_resource", accountService.getLoggedInUser());
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        String azureApiDeployment = credentials != null ? String.valueOf(credentials.getPassword()) : null;
        if (azureApiDeployment == null || azureApiDeployment.isEmpty()) {
            return null;
        }

        return azureApiDeployment;
    }


}
