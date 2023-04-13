package com.translator.dao.firebase;

import com.google.gson.Gson;
import com.translator.model.api.firebase.FirebaseAuthLoginResponseResource;
import com.translator.model.api.firebase.FirebaseToken;
import com.translator.model.api.firebase.UserLoginRequestResource;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FirebaseTokenDaoImpl implements FirebaseTokenDao {
    @Inject
    private final Gson gson;

    public FirebaseTokenDaoImpl(final Gson gson) {
        this.gson = gson;
    }

    @Override
    public FirebaseAuthLoginResponseResource login(String username, String password) {
        URL url = null;
        try {
            url = new URL("https://api.codactor.com/users/login");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            con.setRequestMethod("POST");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setDoOutput(true);
        con.setDoInput(true);
        UserLoginRequestResource userLoginRequestResource = new UserLoginRequestResource(username, password);
        String jsonInputString = gson.toJson(userLoginRequestResource);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream is = con.getInputStream()) {
            String response = IOUtils.toString(is, StandardCharsets.UTF_8);
            FirebaseAuthLoginResponseResource firebaseAuthLoginResponseResource = gson.fromJson(response, FirebaseAuthLoginResponseResource.class);
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
                credentialsFile.createNewFile();
            }
            try (FileWriter writer = new FileWriter(credentialsPath)) {
                if (firebaseAuthLoginResponseResource== null) {
                    return null;
                }
                writer.write(firebaseAuthLoginResponseResource.getRefreshToken());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return firebaseAuthLoginResponseResource;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FirebaseToken getFirebaseToken(String refreshToken) {
        URL url = null;
        try {
            url = new URL("https://api.codactor.com/users/id_token");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            con.setRequestMethod("POST");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        con.setDoOutput(true);
        con.setDoInput(true);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = refreshToken.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream is = con.getInputStream()) {
            String response = IOUtils.toString(is, StandardCharsets.UTF_8);
            FirebaseToken firebaseToken = gson.fromJson(response, FirebaseToken.class);
            firebaseToken.setCreationTimestamp(LocalDateTime.now(ZoneOffset.UTC));
            firebaseToken.setExpirationTimestamp(LocalDateTime.now(ZoneOffset.UTC).plusHours(1));
            return firebaseToken;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
