package com.translator.dao.history;

import com.google.gson.Gson;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.model.codactor.api.translator.history.DesktopCodeModificationHistoryResponseResource;
import com.translator.model.codactor.ai.history.data.HistoricalFileModificationDataHolder;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CodeModificationHistoryDaoImpl implements CodeModificationHistoryDao {
    private FirebaseTokenService firebaseTokenService;
    private final Gson gson;

    @Inject
    public CodeModificationHistoryDaoImpl(FirebaseTokenService firebaseTokenService, Gson gson) {
        this.gson = gson;
        this.firebaseTokenService = firebaseTokenService;
    }

    public DesktopCodeModificationHistoryResponseResource getRecentModifications() {
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/recent");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                return gson.fromJson(response, DesktopCodeModificationHistoryResponseResource.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public HistoricalFileModificationDataHolder getModification(String id) {
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/recent/individual");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(id.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                return gson.fromJson(response, HistoricalFileModificationDataHolder.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
