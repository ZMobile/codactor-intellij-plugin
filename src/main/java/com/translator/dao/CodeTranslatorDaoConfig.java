package com.translator.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.translator.dao.firebase.FirebaseTokenDao;
import com.translator.dao.firebase.FirebaseTokenDaoImpl;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.dao.firebase.FirebaseTokenServiceImpl;
import com.translator.dao.history.CodeModificationHistoryDao;
import com.translator.dao.history.CodeModificationHistoryDaoImpl;
import com.translator.dao.history.ContextQueryDao;
import com.translator.dao.history.ContextQueryDaoImpl;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.dao.inquiry.InquiryDaoImpl;
import com.translator.dao.modification.CodeModificationDao;
import com.translator.dao.modification.CodeModificationDaoImpl;

public class CodeTranslatorDaoConfig extends AbstractModule {
    @Override
    protected void configure() {
        bind(CodeModificationHistoryDao.class).to(CodeModificationHistoryDaoImpl.class).asEagerSingleton();
        bind(ContextQueryDao.class).to(ContextQueryDaoImpl.class).asEagerSingleton();
        bind(CodeModificationDao.class).to(CodeModificationDaoImpl.class).asEagerSingleton();
        bind(InquiryDao.class).to(InquiryDaoImpl.class).asEagerSingleton();
    }

    @Provides
    public Gson gson() {
        return new GsonBuilder().create();
    }

    @Provides
    public FirebaseTokenDao firebaseTokenDao(Gson gson) {
        return new FirebaseTokenDaoImpl(gson);
    }

    @Provides
    public FirebaseTokenService firebaseTokenService(FirebaseTokenDao firebaseTokenDao) {
        return new FirebaseTokenServiceImpl(firebaseTokenDao);
    }
}
