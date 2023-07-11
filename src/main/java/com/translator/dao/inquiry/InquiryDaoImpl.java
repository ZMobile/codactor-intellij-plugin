package com.translator.dao.inquiry;

import com.google.gson.Gson;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.model.codactor.api.translator.inquiry.GeneralInquiryCreationRequestResource;
import com.translator.model.codactor.api.translator.inquiry.InquiryContinuationRequestResource;
import com.translator.model.codactor.api.translator.inquiry.InquiryCreationRequestResource;
import com.translator.model.codactor.api.translator.inquiry.InquiryListResponseResource;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.inquiry.function.ChatGptFunction;
import com.translator.model.codactor.inquiry.function.FunctionCallResponseRequestResource;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.modification.RecordType;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class InquiryDaoImpl implements InquiryDao {
    private FirebaseTokenService firebaseTokenService;
    private final Gson gson;

    @Inject
    public InquiryDaoImpl(FirebaseTokenService firebaseTokenService,
                          Gson gson) {
        this.gson = gson;
        this.firebaseTokenService = firebaseTokenService;
    }

    @Override
    public List<Inquiry> getRecentInquiries() {
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/inquiries/recent");
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
                InquiryListResponseResource inquiryListResponseResource = gson.fromJson(response, InquiryListResponseResource.class);
                return inquiryListResponseResource.getInquiries();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Inquiry getInquiry(String inquiryId) {
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/inquiries/chats");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(inquiryId.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                System.out.println(response);
                return gson.fromJson(response, Inquiry.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Inquiry createInquiry(String subjectRecordId, RecordType recordType, String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext, String systemMessage) {
        InquiryCreationRequestResource inquiryCreationRequestResource = new InquiryCreationRequestResource.Builder()
                .withSubjectRecordId(subjectRecordId)
                .withRecordType(recordType)
                .withQuestion(question)
                .withOpenAiApiKey(openAiApiKey)
                .withModel(model)
                .withPriorContext(priorContext)
                .withSystemMessage(systemMessage)
                .build();
        if (inquiryCreationRequestResource.getPriorContext() == null) {
            inquiryCreationRequestResource.setPriorContext(new ArrayList<>());
        }
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/inquiries/new");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(inquiryCreationRequestResource);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                System.out.println(response);
                return gson.fromJson(response, Inquiry.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Inquiry createInquiry(String subjectRecordId, RecordType recordType, String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext, List<ChatGptFunction> chatGptFunctions, String systemMessage) {
        InquiryCreationRequestResource inquiryCreationRequestResource = new InquiryCreationRequestResource.Builder()
                .withSubjectRecordId(subjectRecordId)
                .withRecordType(recordType)
                .withQuestion(question)
                .withOpenAiApiKey(openAiApiKey)
                .withModel(model)
                .withPriorContext(priorContext)
                .withFunctions(chatGptFunctions)
                .withSystemMessage(systemMessage)
                .build();
        if (inquiryCreationRequestResource.getPriorContext() == null) {
            inquiryCreationRequestResource.setPriorContext(new ArrayList<>());
        }
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/inquiries/new");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(inquiryCreationRequestResource);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                System.out.println(response);
                return gson.fromJson(response, Inquiry.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Inquiry createInquiry(String filePath, String code, String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext, String systemMessage) {
        InquiryCreationRequestResource inquiryCreationRequestResource = new InquiryCreationRequestResource.Builder()
                .withFilePath(filePath)
                .withCode(code)
                .withQuestion(question)
                .withOpenAiApiKey(openAiApiKey)
                .withModel(model)
                .withPriorContext(priorContext)
                .withSystemMessage(systemMessage)
                .build();
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/inquiries/new");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(inquiryCreationRequestResource);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                System.out.println(response);
                return gson.fromJson(response, Inquiry.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Inquiry createInquiry(String filePath, String code, String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext, List<ChatGptFunction> functions, String systemMessage) {
        InquiryCreationRequestResource inquiryCreationRequestResource = new InquiryCreationRequestResource.Builder()
                .withFilePath(filePath)
                .withCode(code)
                .withQuestion(question)
                .withOpenAiApiKey(openAiApiKey)
                .withModel(model)
                .withPriorContext(priorContext)
                .withFunctions(functions)
                .withSystemMessage(systemMessage)
                .build();
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/inquiries/new");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(inquiryCreationRequestResource);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                System.out.println(response);
                return gson.fromJson(response, Inquiry.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Inquiry createGeneralInquiry(String question, String openAiApiKey, String model, String systemMessage) {
        return createGeneralInquiry(question, openAiApiKey, model, new ArrayList<>(), systemMessage);
    }

    @Override
    public Inquiry createGeneralInquiry(String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext, String systemMessage) {
        return createGeneralInquiry(question, openAiApiKey, model, priorContext, null, systemMessage);
    }

    @Override
    public Inquiry createGeneralInquiry(String question, String openAiApiKey, String model, List<HistoricalContextObjectHolder> priorContext, List<ChatGptFunction> functions, String systemMessage) {
        GeneralInquiryCreationRequestResource inquiryCreationRequestResource = new GeneralInquiryCreationRequestResource.Builder()
                .withQuestion(question)
                .withOpenAiApiKey(openAiApiKey)
                .withModel(model)
                .withPriorContext(priorContext)
                .withFunctions(functions)
                .withSystemMessage(systemMessage)
                .build();
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/inquiries/new/general");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(inquiryCreationRequestResource);
            System.out.println(requestBody);
            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                System.out.println(response);
                return gson.fromJson(response, Inquiry.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Inquiry continueInquiry(String previousInquiryChatId, String question, String openAiApiKey, String model) {
        return continueInquiry(previousInquiryChatId, question, openAiApiKey, model, null);
    }

    @Override
    public Inquiry continueInquiry(String previousInquiryChatId, String question, String openAiApiKey, String model, List<ChatGptFunction> functions) {
        InquiryContinuationRequestResource inquiryContinuationRequestResource = new InquiryContinuationRequestResource.Builder()
                .withPreviousInquiryChatId(previousInquiryChatId)
                .withQuestion(question)
                .withOpenAiApiKey(openAiApiKey)
                .withModel(model)
                .withFunctions(functions)
                .build();
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/inquiries/continue");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(inquiryContinuationRequestResource);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                System.out.println(response);
                return gson.fromJson(response, Inquiry.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Inquiry respondToFunctionCall(String previousInquiryChatId, String functionName, String content, String openAiApiKey, String model, List<ChatGptFunction> functions) {
        FunctionCallResponseRequestResource functionCallResponseRequestResource = new FunctionCallResponseRequestResource.Builder()
                .withPreviousInquiryChatId(previousInquiryChatId)
                .withFunctionName(functionName)
                .withContent(content)
                .withOpenAiApiKey(openAiApiKey)
                .withModel(model)
                .withFunctions(functions)
                .build();
        try {
            URL url = new URL("https://api.codactor.com" + /*://localHost:8080*/ "/projects/desktop/inquiries/continue/function");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", firebaseTokenService.getFirebaseToken().getIdToken());
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            String requestBody = gson.toJson(functionCallResponseRequestResource);

            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                String response = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                System.out.println(response);
                return gson.fromJson(response, Inquiry.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
