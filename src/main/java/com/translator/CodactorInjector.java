package com.translator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.translator.view.CodeTranslatorViewConfig;

import java.util.HashMap;
import java.util.Map;


public class CodactorInjector {
    private final Map<Project, Injector> projectToInjectorMap;

    public static CodactorInjector getInstance() {
        return ServiceManager.getService(CodactorInjector.class);
    }

    public CodactorInjector() {
        this.projectToInjectorMap = new HashMap<>();
    }

    public Injector getInjector(Project project) {
        if (projectToInjectorMap.containsKey(project)) {
            return projectToInjectorMap.get(project);
        } else {
            Injector injector = Guice.createInjector(new CodeTranslatorViewConfig(project));
            projectToInjectorMap.put(project, injector);
            return injector;
        }
    }
}