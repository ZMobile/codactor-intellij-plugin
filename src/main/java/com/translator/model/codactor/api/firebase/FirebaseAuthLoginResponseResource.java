package com.translator.model.codactor.api.firebase;

public class FirebaseAuthLoginResponseResource {
    private String idToken;
    private String email;
    private String refreshToken;
    private String expiresIn;
    private String localId;

    public FirebaseAuthLoginResponseResource(String idToken, String email, String refreshToken, String expiresIn, String localId) {
        this.idToken = idToken;
        this.email = email;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.localId = localId;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }
}
