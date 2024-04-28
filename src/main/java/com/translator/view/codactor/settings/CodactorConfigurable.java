package com.translator.view.codactor.settings;

import com.google.inject.Injector;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.translator.CodactorInjector;
import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.service.codactor.ai.openai.connection.AzureConnectionService;
import com.translator.service.codactor.ai.openai.connection.CodactorConnectionService;
import com.translator.service.codactor.ai.openai.connection.DefaultConnectionService;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
        if (projectList.length > 0) {
            Project sampleProject = projectList[0];
            Injector injector = CodactorInjector.getInstance().getInjector(sampleProject);
            FirebaseTokenService firebaseTokenService = injector.getInstance(FirebaseTokenService.class);
            CodactorConnectionService codactorConnectionService = injector.getInstance(CodactorConnectionService.class);
            AzureConnectionService azureConnectionService = injector.getInstance(AzureConnectionService.class);
            DefaultConnectionService defaultConnectionService = injector.getInstance(DefaultConnectionService.class);
            settingsPage = new CodactorSettingsPage(firebaseTokenService, codactorConnectionService, defaultConnectionService, azureConnectionService);
            return settingsPage.getRootPanel();
        } else return null;
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
