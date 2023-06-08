package com.translator.dao.history;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ContextQueryDaoImpl implements ContextQueryDao {
    private final FirebaseTokenService firebaseTokenService;
    private final Gson gson;

    @Inject
    public ContextQueryDaoImpl(FirebaseTokenService firebaseTokenService, Gson gson) {
        this.firebaseTokenService = firebaseTokenService;
        this.gson = gson;
    }

    @Override
    public HistoricalContextObjectHolder queryHistoricalContextObject(HistoricalContextObjectHolder historicalContextObjectHolder) {
        try {
            URL url = new URL("https://api.codactor.com/projects/desktop/context");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(historicalContextObjectHolder);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                return gson.fromJson(response, HistoricalContextObjectHolder.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public List<HistoricalContextObjectHolder> queryHistoricalContextObjects(List<HistoricalContextObjectHolder> historicalContextObjectHolderList) {
        if (historicalContextObjectHolderList == null || historicalContextObjectHolderList.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            URL url = new URL("https://api.codactor.com/projects/desktop/context/list");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(historicalContextObjectHolderList);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                Type listType = new TypeToken<List<HistoricalContextObjectHolder>>(){}.getType();
                return gson.fromJson(response, listType);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
