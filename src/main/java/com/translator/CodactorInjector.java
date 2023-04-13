package com.translator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.translator.view.CodeTranslatorViewConfig;


public class CodactorInjector {

    private final Project project;
    private final Injector injector;

    public static CodactorInjector getInstance(Project project) {
        return project.getService(CodactorInjector.class);
    }

    public CodactorInjector(Project project) {
        this.project = project;

        // Create Guice injector and configure it with your modules
        injector = Guice.createInjector(new CodeTranslatorViewConfig(project));
    }

    public Injector getInjector() {
        return injector;
    }

    public Project getProject() {
        return project;
    }
}