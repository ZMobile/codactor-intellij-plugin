package com.translator.service.codactor.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;

public class CodactorConfigurable implements Configurable {
    private CodactorSettingsPage settingsPage;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Codactor Settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        CodactorSettings codactorSettings = ServiceManager.getService(CodactorSettings.class);
        settingsPage = new CodactorSettingsPage(codactorSettings);
        return settingsPage.getRootPanel();
    }

    @Override
    public boolean isModified() {
        return settingsPage.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        settingsPage.applySettingsTo();
    }

    @Override
    public void reset() {
        settingsPage.reset();
    }
}
