package com.translator.dao.firebase;

import com.translator.model.api.firebase.FirebaseAuthLoginResponseResource;
import com.translator.model.api.firebase.FirebaseToken;

public interface FirebaseTokenDao {
    FirebaseAuthLoginResponseResource login(String username, String password);

    FirebaseToken getFirebaseToken(String refreshToken);
}
