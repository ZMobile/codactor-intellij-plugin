package com.translator.service.codactor.account;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.translator.dao.firebase.FirebaseTokenService;

public class AccountServiceImpl implements AccountService {
    private final FirebaseTokenService firebaseTokenService;

    public AccountServiceImpl(FirebaseTokenService firebaseTokenService) {
        this.firebaseTokenService = firebaseTokenService;
    }

    public boolean login(String email, String password) {
        boolean success = firebaseTokenService.login(email, password);
        if (success) {
            CredentialAttributes credentialAttributes = new CredentialAttributes("logged_in_user", "user");
            Credentials credentials = new Credentials("", email);
            PasswordSafe.getInstance().set(credentialAttributes, credentials);
        }
        return success;
    }

    public void logout() {
        CredentialAttributes credentialAttributes = new CredentialAttributes("logged_in_user", "user");
        Credentials credentials = new Credentials("", "");
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
        firebaseTokenService.logout();
    }

    public String getLoggedInUser() {
        CredentialAttributes credentialAttributes = new CredentialAttributes("logged_in_user", "user");
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        String email = credentials != null ? String.valueOf(credentials.getPassword()) : null;
        if (email == null || email.isEmpty()) {
            return null;
        }
        return email;
    }
}
