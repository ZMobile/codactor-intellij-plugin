package com.translator.dao.firebase;

import com.translator.model.codactor.api.firebase.FirebaseAuthLoginResponseResource;
import com.translator.model.codactor.api.firebase.FirebaseToken;

public interface FirebaseTokenDao {
    FirebaseAuthLoginResponseResource login(String username, String password);

    FirebaseToken getFirebaseToken(String refreshToken);
}
