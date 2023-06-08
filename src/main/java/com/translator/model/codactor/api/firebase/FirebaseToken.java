package com.translator.model.codactor.api.firebase;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FirebaseToken {
    private LocalDateTime creationTimestamp;
    private LocalDateTime expirationTimestamp;
    private String user_id;
    private String id_token;

    public FirebaseToken(String expiresIn, String userId, String idToken) {
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.expirationTimestamp = creationTimestamp.plusSeconds(Long.parseLong(expiresIn));
        this.creationTimestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.expirationTimestamp = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(Long.parseLong(expiresIn));
        this.user_id = userId;
        this.id_token = idToken;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public LocalDateTime getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public void setExpirationTimestamp(LocalDateTime expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public String getIdToken() {
        return id_token;
    }

    public void setIdToken(String idToken) {
        this.id_token = idToken;
    }

    public boolean isExpired() {
        return LocalDateTime.now(ZoneOffset.UTC).isAfter(expirationTimestamp);
    }
}
