package com.translator.dao.firebase;

import com.translator.model.api.firebase.FirebaseToken;

public interface FirebaseTokenService {
    void refreshFirebaseToken();

    FirebaseToken getFirebaseToken();

    boolean login(String email, String password);

    void logout();
}
