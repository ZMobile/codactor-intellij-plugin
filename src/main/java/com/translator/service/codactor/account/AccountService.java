package com.translator.service.codactor.account;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

public interface AccountService {
    boolean login(String email, String password);

    void logout();

    String getLoggedInUser();
}
