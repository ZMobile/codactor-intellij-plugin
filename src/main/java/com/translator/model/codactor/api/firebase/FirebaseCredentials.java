package com.translator.model.codactor.api.firebase;

public class FirebaseCredentials {
    private String refreshToken;
    private FirebaseToken firebaseToken;

    public FirebaseCredentials(String refreshToken, FirebaseToken firebaseToken) {
        this.refreshToken = refreshToken;
        this.firebaseToken = firebaseToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public FirebaseToken getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(FirebaseToken firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
