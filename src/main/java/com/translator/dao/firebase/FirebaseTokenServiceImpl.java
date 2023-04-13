package com.translator.dao.firebase;

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
        String userHome = System.getProperty("user.home");
        String codactorFolder = userHome + "/Codactor";
        File codactorFolderFile = new File(codactorFolder);
        if (!codactorFolderFile.exists()) {
            codactorFolderFile.mkdir();
        }
        String generatedCodeFolder = userHome + "/Codactor/Generated-Code";
        File generatedCodeFile = new File(generatedCodeFolder);
        if (!generatedCodeFile.exists()) {
            generatedCodeFile.mkdir();
        }
        String credentialsFolder = userHome + "/Codactor/credentials";
        File credentialsFolderFile = new File(credentialsFolder);
        if (!credentialsFolderFile.exists()) {
            credentialsFolderFile.mkdir();
        }
        String credentialsPath = userHome + "/Codactor/credentials/credentials.txt";
        File file = new File(credentialsPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            String refreshToken = IOUtils.toString(FileUtils.openInputStream(file), StandardCharsets.UTF_8);
            if (refreshToken == null || refreshToken.isEmpty()) {
                return null;
            }
            FirebaseToken firebaseToken = firebaseTokenDao.getFirebaseToken(refreshToken);
            return firebaseToken;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
        String credentialsPath = userHome + "/Codactor/credentials/credentials.txt";
        File credentialsFile = new File(credentialsPath);
        if (!credentialsFile.exists()) {
            try {
                credentialsFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            //Clears the contents of the file
            try {
                Files.write(credentialsFile.toPath(), "".getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.exit(0);
    }
}
