package com.translator.service.codactor.settings;

import com.google.inject.Injector;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.translator.CodactorInjector;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.service.codactor.connection.AzureConnectionService;
import com.translator.service.codactor.connection.CodactorConnectionService;
import com.translator.service.codactor.connection.DefaultConnectionService;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;
import java.util.List;

public class CodactorConfigurable implements Configurable {
    private CodactorSettingsPage settingsPage;

    public CodactorConfigurable() {
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Codactor Settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        Project[] projectList = ProjectManager.getInstance().getOpenProjects();
        assert projectList.length > 0;
        Project sampleProject = projectList[0];
        Injector injector = CodactorInjector.getInstance().getInjector(sampleProject);
        FirebaseTokenService firebaseTokenService = injector.getInstance(FirebaseTokenService.class);
        CodactorConnectionService codactorConnectionService = injector.getInstance(CodactorConnectionService.class);
        AzureConnectionService azureConnectionService = injector.getInstance(AzureConnectionService.class);
        DefaultConnectionService defaultConnectionService = injector.getInstance(DefaultConnectionService.class);
        settingsPage = new CodactorSettingsPage(firebaseTokenService, codactorConnectionService, defaultConnectionService, azureConnectionService);
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
