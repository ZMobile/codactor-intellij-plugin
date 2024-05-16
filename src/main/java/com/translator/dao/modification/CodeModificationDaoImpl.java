/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.translator.dao.modification;

import com.google.gson.Gson;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.model.codactor.api.translator.modification.*;
import com.translator.model.codactor.ai.modification.FileModificationSuggestionModificationRecord;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CodeModificationDaoImpl implements CodeModificationDao {
    private FirebaseTokenService firebaseTokenService;
    private final Gson gson;

    @Inject
    public CodeModificationDaoImpl(FirebaseTokenService firebaseTokenService, Gson gson) {
        this.gson = gson;
        this.firebaseTokenService = firebaseTokenService;
    }
    
    public DesktopCodeModificationResponseResource getModifiedCode(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource) {
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/modify");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(desktopCodeModificationRequestResource);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                System.out.println("Response: " + response);
                DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = gson.fromJson(response, DesktopCodeModificationResponseResource.class);
                desktopCodeModificationResponseResource.setResponseCode(responseCode);
                return desktopCodeModificationResponseResource;
            } else {
                return new DesktopCodeModificationResponseResource(responseCode, "Response Code: " + responseCode);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public FileModificationSuggestionModificationRecord getModifiedCodeModification(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource) {
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/modify/suggestions");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(desktopCodeModificationRequestResource);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = gson.fromJson(response, FileModificationSuggestionModificationRecord.class);
                fileModificationSuggestionModificationRecord.setResponseCode(responseCode);
                return fileModificationSuggestionModificationRecord;
            } else {
                return new FileModificationSuggestionModificationRecord(responseCode, "Response Code: " + responseCode);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public DesktopCodeModificationResponseResource getFixedCode(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource) {
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/fix");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(desktopCodeModificationRequestResource);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                DesktopCodeModificationResponseResource desktopCodeModificationResponseResource = gson.fromJson(response, DesktopCodeModificationResponseResource.class);
                desktopCodeModificationResponseResource.setResponseCode(responseCode);
                return desktopCodeModificationResponseResource;
            } else {
                return new DesktopCodeModificationResponseResource(responseCode, "Response Code: " + responseCode);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public FileModificationSuggestionModificationRecord getModifiedCodeFix(DesktopCodeModificationRequestResource desktopCodeModificationRequestResource) {
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/fix/suggestions");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(desktopCodeModificationRequestResource);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = gson.fromJson(response, FileModificationSuggestionModificationRecord.class);
                fileModificationSuggestionModificationRecord.setResponseCode(responseCode);
                return fileModificationSuggestionModificationRecord;
            } else {
                return new FileModificationSuggestionModificationRecord(responseCode, "Response Code: " + responseCode);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    @Override
    public DesktopCodeCreationResponseResource getCreatedCode(DesktopCodeCreationRequestResource desktopCodeCreationRequestResource) {
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/create");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(desktopCodeCreationRequestResource);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = gson.fromJson(response, DesktopCodeCreationResponseResource.class);
                desktopCodeCreationResponseResource.setResponseCode(responseCode);
                return desktopCodeCreationResponseResource;
            } else {
                return new DesktopCodeCreationResponseResource(responseCode, "Response Code: " + responseCode);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public FileModificationSuggestionModificationRecord getModifiedCodeCreation(DesktopCodeCreationRequestResource desktopCodeCreationRequestResource) {
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/suggestions");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(desktopCodeCreationRequestResource);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                FileModificationSuggestionModificationRecord fileModificationSuggestionModificationRecord = gson.fromJson(response, FileModificationSuggestionModificationRecord.class);
                fileModificationSuggestionModificationRecord.setResponseCode(responseCode);
                return fileModificationSuggestionModificationRecord;
            } else {
                return new FileModificationSuggestionModificationRecord(responseCode, "Response Code: " + responseCode);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public DesktopCodeTranslationResponseResource getTranslatedCode(DesktopCodeTranslationRequestResource desktopCodeTranslationRequestResource) {
        try {
        URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/translate");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setDoOutput(true);

                        String requestBody = gson.toJson(desktopCodeTranslationRequestResource);

                        OutputStream os = con.getOutputStream();
                        os.write(requestBody.getBytes());
                        os.flush();
                        os.close();
                        int responseCode = con.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            InputStream inputStream = con.getInputStream();
                            String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                            DesktopCodeTranslationResponseResource desktopCodeTranslationResponseResource = gson.fromJson(response, DesktopCodeTranslationResponseResource.class);
                            desktopCodeTranslationResponseResource.setResponseCode(responseCode);
                            return desktopCodeTranslationResponseResource;
                        } else {
                            return new DesktopCodeTranslationResponseResource(responseCode, "Response Code: " + responseCode);
                        }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
