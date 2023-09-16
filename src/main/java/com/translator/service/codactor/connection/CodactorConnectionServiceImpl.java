package com.translator.service.codactor.connection;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.translator.service.codactor.account.AccountService;

import javax.inject.Inject;

public class CodactorConnectionServiceImpl implements CodactorConnectionService {
    private final AccountService accountService;

    @Inject
    public CodactorConnectionServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public CodactorConnectionType getConnectionType() {
        String user = accountService.getLoggedInUser();
        CredentialAttributes credentialAttributes = new CredentialAttributes("codactor_connection_type", user);
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        String connectionType = credentials != null ? String.valueOf(credentials.getPassword()) : null;
        if (connectionType == null || connectionType.isEmpty()) {
            return null;
        }
        return CodactorConnectionType.valueOf(connectionType);
    }

    @Override
    public CodactorConnectionType setConnectionType(CodactorConnectionType connectionType) {
        String user = accountService.getLoggedInUser();
        CredentialAttributes credentialAttributes = new CredentialAttributes("codactor_connection_type", user);
        Credentials credentials = new Credentials(user, connectionType.name());
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
        return connectionType;
    }
}
