package com.translator.service.codactor.settings;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "CodactorSettings",
        storages = {
                @Storage("codactor.xml")
        }
)
public class CodactorSettings implements PersistentStateComponent<CodactorSettings> {
    private String connectionType;
    private String url;
    private String key;
    private boolean loggedIn;
    private String accountName;

    public CodactorSettings() {
        this.connectionType = "Default";
        this.url = "";
        this.key = "";
        this.loggedIn = false;
        this.accountName = "";
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Nullable
    @Override
    public CodactorSettings getState() {
        try {
            return this;
        } catch (Exception e) {
            System.out.println("Error getting state: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void loadState(@NotNull CodactorSettings state) {
        try {
            XmlSerializerUtil.copyBean(state, this);
        } catch (Exception e) {
            System.out.println("Error loading state: " + e.getMessage());
        }
    }
}

