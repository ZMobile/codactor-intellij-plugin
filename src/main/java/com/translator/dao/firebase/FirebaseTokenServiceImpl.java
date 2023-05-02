package com.translator.dao.firebase;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.translator.model.api.firebase.FirebaseAuthLoginResponseResource;
import com.translator.model.api.firebase.FirebaseToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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
        CredentialAttributes credentialAttributes = new CredentialAttributes("firebase_refresh_token", "user");
        Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
        String refreshToken = credentials != null ? String.valueOf(credentials.getPassword()) : null;
        if (refreshToken == null || refreshToken.isEmpty()) {
            return null;
        }
        FirebaseToken firebaseToken = firebaseTokenDao.getFirebaseToken(refreshToken);
        return firebaseToken;
    }

    @Override
    public boolean login(String email, String password) {
        FirebaseAuthLoginResponseResource firebaseAuthLoginResponseResource = firebaseTokenDao.login(email, password);
        if (firebaseAuthLoginResponseResource == null) {
            return false;
        }
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
        CredentialAttributes credentialAttributes = new CredentialAttributes("firebase_refresh_token", "user");
        PasswordSafe.getInstance().set(credentialAttributes, null);
    }
}
