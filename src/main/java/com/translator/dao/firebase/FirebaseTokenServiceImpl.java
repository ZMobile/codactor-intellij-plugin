package com.translator.dao.firebase;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.translator.model.codactor.api.firebase.FirebaseAuthLoginResponseResource;
import com.translator.model.codactor.api.firebase.FirebaseToken;

import javax.inject.Inject;

public class FirebaseTokenServiceImpl implements FirebaseTokenService {
    private FirebaseTokenDao firebaseTokenDao;
    private FirebaseToken firebaseToken;

    @Inject
    public FirebaseTokenServiceImpl(FirebaseTokenDao firebaseTokenDao) {
        this.firebaseTokenDao = firebaseTokenDao;
        this.firebaseToken = null;
    }

    @Override
    public FirebaseToken getFirebaseToken() {
        if (firebaseToken != null && !firebaseToken.isExpired()) {
            return firebaseToken;
        }
        CredentialAttributes credentialAttributes = new CredentialAttributes("firebase_refresh_token", getLoggedInUser());
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        String refreshToken = credentials != null ? String.valueOf(credentials.getPassword()) : null;
        if (refreshToken == null || refreshToken.isEmpty()) {
            return null;
        }
        return firebaseTokenDao.getFirebaseToken(refreshToken);
    }

    @Override
    public boolean login(String email, String password) {
        FirebaseAuthLoginResponseResource firebaseAuthLoginResponseResource = firebaseTokenDao.login(email, password);
        if (firebaseAuthLoginResponseResource == null || firebaseAuthLoginResponseResource.getError() != null) {
            return false;
        }
        CredentialAttributes credentialAttributes = new CredentialAttributes("logged_in_user", "user");
        Credentials credentials = new Credentials("user", email);
        PasswordSafe.getInstance().set(credentialAttributes, credentials);
        CredentialAttributes refreshTokenCredentialAttributes = new CredentialAttributes("firebase_refresh_token", email);
        credentials = new Credentials(email, firebaseAuthLoginResponseResource.getRefreshToken());
        PasswordSafe.getInstance().set(refreshTokenCredentialAttributes, credentials);
        this.firebaseToken = firebaseTokenDao.getFirebaseToken(firebaseAuthLoginResponseResource.getRefreshToken());
        return true;
    }

    @Override
    public void refreshFirebaseToken() {
        FirebaseToken newFirebaseToken = getFirebaseToken();
        if (newFirebaseToken == null) {
            return;
        }
        this.firebaseToken = newFirebaseToken;
    }

    public void logout() {
        CredentialAttributes refreshTokenCredentialAttributes = new CredentialAttributes("firebase_refresh_token", getLoggedInUser());
        PasswordSafe.getInstance().set(refreshTokenCredentialAttributes, null);
        CredentialAttributes loggedInUserEmailCredentialAttributes = new CredentialAttributes("logged_in_user", "user");
        Credentials credentials = new Credentials("", "");
        PasswordSafe.getInstance().set(loggedInUserEmailCredentialAttributes, credentials);
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

    public boolean isLoggedIn() {
        return getLoggedInUser() != null;
    }
}
