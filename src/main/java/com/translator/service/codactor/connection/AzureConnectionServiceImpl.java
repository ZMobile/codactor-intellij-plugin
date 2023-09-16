package com.translator.service.codactor.connection;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.translator.service.codactor.account.AccountService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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
        if (!isAzureConnected()) {
            return null;
        }
        CredentialAttributes credentialAttributes = new CredentialAttributes("azure_api_resource", accountService.getLoggedInUser());
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        String azureApiDeployment = credentials != null ? String.valueOf(credentials.getPassword()) : null;
        if (azureApiDeployment == null || azureApiDeployment.isEmpty()) {
            return null;
        }
        return azureApiDeployment;
    }

    @Override
    public void setGpt35TurboDeployment(String deployment) {
        CredentialAttributes credentialAttributes = new CredentialAttributes("gpt_35_turbo_deployment", accountService.getLoggedInUser());
        Credentials credentials = new Credentials(accountService.getLoggedInUser(), deployment);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }

    @Override
    public String getGpt35TurboDeployment() {
        CredentialAttributes credentialAttributes = new CredentialAttributes("gpt_35_turbo_deployment", accountService.getLoggedInUser());
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        String gpt35TurboDeployment = credentials != null ? String.valueOf(credentials.getPassword()) : null;
        if (gpt35TurboDeployment == null || gpt35TurboDeployment.isEmpty()) {
            return null;
        }
        return gpt35TurboDeployment;
    }

    @Override
    public void setGpt35Turbo16kDeployment(String deployment) {
        CredentialAttributes credentialAttributes = new CredentialAttributes("gpt_35_turbo_16k_deployment", accountService.getLoggedInUser());
        Credentials credentials = new Credentials(accountService.getLoggedInUser(), deployment);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }

    @Override
    public String getGpt35Turbo16kDeployment() {
        CredentialAttributes credentialAttributes = new CredentialAttributes("gpt_35_turbo_16k_deployment", accountService.getLoggedInUser());
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        String gpt35Turbo16kDeployment = credentials != null ? String.valueOf(credentials.getPassword()) : null;
        if (gpt35Turbo16kDeployment == null || gpt35Turbo16kDeployment.isEmpty()) {
            return null;
        }
        return gpt35Turbo16kDeployment;
    }

    @Override
    public void setGpt4Deployment(String deployment) {
        CredentialAttributes credentialAttributes = new CredentialAttributes("gpt_4_deployment", accountService.getLoggedInUser());
        Credentials credentials = new Credentials(accountService.getLoggedInUser(), deployment);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }

    @Override
    public String getGpt4Deployment() {
        CredentialAttributes credentialAttributes = new CredentialAttributes("gpt_4_deployment", accountService.getLoggedInUser());
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        String gpt4Deployment = credentials != null ? String.valueOf(credentials.getPassword()) : null;
        if (gpt4Deployment == null || gpt4Deployment.isEmpty()) {
            return null;
        }
        return gpt4Deployment;
    }

    @Override
    public void setGpt432kDeployment(String deployment) {
        CredentialAttributes credentialAttributes = new CredentialAttributes("gpt_4_32k_deployment", accountService.getLoggedInUser());
        Credentials credentials = new Credentials(accountService.getLoggedInUser(), deployment);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
    }

    @Override
    public String getGpt432kDeployment() {
        CredentialAttributes credentialAttributes = new CredentialAttributes("gpt_4_32k_deployment", accountService.getLoggedInUser());
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        String gpt432kDeployment = credentials != null ? String.valueOf(credentials.getPassword()) : null;
        if (gpt432kDeployment == null || gpt432kDeployment.isEmpty()) {
            return null;
        }
        return gpt432kDeployment;
    }

    @Override
    public String getDeploymentForModel(String model) {
        if (isAzureConnected()) {
            if (model.equalsIgnoreCase("gpt-3.5-turbo")) {
                return getGpt35TurboDeployment();
            } else if (model.equalsIgnoreCase("gpt-3.5-turbo-16k")) {
                return getGpt35Turbo16kDeployment();
            } else if (model.equalsIgnoreCase("gpt-4")) {
                return getGpt4Deployment();
            } else if (model.equalsIgnoreCase("gpt-4-32k")) {
                return getGpt432kDeployment();
            }
        }
        return null;
    }

    @Override
    public List<String> getActiveModels() {
        List<String> activeModels = new ArrayList<>();
        if (getGpt35TurboDeployment() != null && !getGpt35TurboDeployment().isEmpty()) {
            activeModels.add("gpt-3.5-turbo");
        }
        if (getGpt35Turbo16kDeployment() != null && !getGpt35Turbo16kDeployment().isEmpty()) {
            activeModels.add("gpt-3.5-turbo-16k");
        }
        if (getGpt4Deployment() != null && !getGpt4Deployment().isEmpty()) {
            activeModels.add("gpt-4");
        }
        if (getGpt432kDeployment() != null && !getGpt432kDeployment().isEmpty()) {
            activeModels.add("gpt-4-32k");
        }
        return activeModels;
    }

    public String[] getModels() {
        return new String[]{"gpt-3.5-turbo", "gpt-3.5-turbo-16k", "gpt-4", "gpt-4-32k"};
    }
}
