package com.translator.view;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.intellij.openapi.project.Project;
import com.translator.dao.history.CodeModificationHistoryDao;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.service.CodeTranslatorServiceConfig;
import com.translator.service.codactor.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.file.FileOpenerService;
import com.translator.service.codactor.file.FileReaderService;
import com.translator.service.codactor.file.SelectedFileFetcherService;
import com.translator.service.codactor.functions.InquiryChatListFunctionCallCompressorService;
import com.translator.service.codactor.functions.InquiryFunctionCallProcessorService;
import com.translator.service.codactor.functions.search.ProjectSearchService;
import com.translator.service.codactor.inquiry.InquiryService;
import com.translator.service.codactor.modification.CodeModificationService;
import com.translator.service.codactor.modification.FileModificationRestarterService;
import com.translator.service.codactor.modification.FileModificationSuggestionDiffViewerService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.task.BackgroundTaskMapperService;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.console.CodactorConsole;
import com.translator.view.codactor.factory.InquiryViewerFactory;
import com.translator.view.codactor.factory.dialog.*;
import com.translator.view.codactor.viewer.inquiry.InquiryListViewer;
import com.translator.view.codactor.viewer.inquiry.InquiryViewer;
import com.translator.view.codactor.viewer.modification.HistoricalModificationListViewer;
import com.translator.view.codactor.viewer.modification.ModificationQueueViewer;
import com.translator.view.codactor.viewer.modification.ProvisionalModificationViewer;
import com.translator.view.uml.CodactorUmlBuilderApplicationModel;
import com.translator.view.uml.application.CodactorUmlBuilderApplication;
import com.translator.view.uml.application.CodactorUmlBuilderSDIApplication;
import com.translator.view.uml.CodactorUmlBuilderView;
import com.translator.view.uml.factory.CodactorUmlBuilderApplicationModelFactory;
import com.translator.view.uml.factory.CodactorUmlBuilderViewFactory;
import com.translator.view.uml.factory.adapter.CustomMouseAdapterFactory;
import com.translator.view.uml.factory.dialog.PromptNodeDialogFactory;
import com.translator.view.uml.factory.tool.NodeConnectionToolFactory;
import com.translator.view.uml.factory.tool.PromptNodeCreationToolFactory;
import com.translator.view.uml.application.CodactorUmlBuilderOSXApplication;

public class CodeTranslatorViewConfig extends AbstractModule {
    private Project project;

    public CodeTranslatorViewConfig(Project project) {
        this.project = project;
    }

    @Override
    protected void configure() {
        install(new CodeTranslatorServiceConfig(project));
        install(new FactoryModuleBuilder().build(ProvisionalModificationCustomizerDialogFactory.class));
        install(new FactoryModuleBuilder().build(PromptContextBuilderDialogFactory.class));
        install(new FactoryModuleBuilder().build(CodactorUmlBuilderViewFactory.class));
        install(new FactoryModuleBuilder().build(CodactorUmlBuilderApplicationModelFactory.class));
        install(new FactoryModuleBuilder().build(PromptNodeDialogFactory.class));
        install(new FactoryModuleBuilder().build(NodeConnectionToolFactory.class));
        install(new FactoryModuleBuilder().build(CustomMouseAdapterFactory.class));
        install(new FactoryModuleBuilder().build(PromptNodeCreationToolFactory.class));
        install(new FactoryModuleBuilder().build(FileCreateDialogFactory.class));
        install(new FactoryModuleBuilder().build(MultiFileCreateDialogFactory.class));
        install(new FactoryModuleBuilder().build(FileFixDialogFactory.class));
        install(new FactoryModuleBuilder().build(FileModifyDialogFactory.class));
        install(new FactoryModuleBuilder().build(FileTranslateDialogFactory.class));
        install(new FactoryModuleBuilder().build(FileModificationErrorDialogFactory.class));
        install(new FactoryModuleBuilder().build(InquiryViewerFactory.class));
    }

    @Singleton
    @Provides
    public ProvisionalModificationViewer codeSnippetListViewer(CodactorToolWindowService codactorToolWindowService,
                                                               FileModificationTrackerService fileModificationTrackerService,
                                                               FileOpenerService fileOpenerService,
                                                               FileModificationSuggestionDiffViewerService fileModificationSuggestionDiffViewerService,
                                                               ProvisionalModificationCustomizerDialogFactory provisionalModificationCustomizerDialogFactory) {
        return new ProvisionalModificationViewer(codactorToolWindowService, fileModificationTrackerService, fileOpenerService, fileModificationSuggestionDiffViewerService, provisionalModificationCustomizerDialogFactory);
    }

    @Singleton
    @Provides
    public InquiryViewer inquiryViewer(Gson gson,
                                       Project project,
                                       CodactorToolWindowService codactorToolWindowService,
                                       MultiFileCreateDialogFactory multiFileCreateDialogFactory,
                                       InquiryService inquiryService,
                                       PromptContextServiceFactory promptContextServiceFactory,
                                       TextAreaHeightCalculatorService textAreaHeightCalculatorService,
                                       InquiryChatListFunctionCallCompressorService inquiryChatListFunctionCallCompressorService,
                                       InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService) {
        return new InquiryViewer(gson, project, codactorToolWindowService, multiFileCreateDialogFactory, inquiryService, promptContextServiceFactory, textAreaHeightCalculatorService, inquiryChatListFunctionCallCompressorService, inquiryFunctionCallProcessorService);
    }

    @Singleton
    @Provides
    public InquiryListViewer inquiryListViewer(Project project,
                                               CodactorToolWindowService codactorToolWindowService,
                                               InquiryDao inquiryDao,
                                               InquiryViewerFactory inquiryViewer) {
        return new InquiryListViewer(project, codactorToolWindowService, inquiryDao, inquiryViewer);
    }

    @Singleton
    @Provides
    public HistoricalModificationListViewer historicalModificationListViewer(CodactorToolWindowService codactorToolWindowService,
                                                                             CodeModificationHistoryDao codeModificationHistoryDao,
                                                                             InquiryViewerFactory inquiryViewerFactory) {
        return new HistoricalModificationListViewer(codactorToolWindowService, codeModificationHistoryDao, inquiryViewerFactory);
    }

    @Singleton
    @Provides
    public ModificationQueueViewer ModificationQueueViewer(Project project,
                                                           ProvisionalModificationViewer provisionalModificationViewer,
                                                           CodactorToolWindowService codactorToolWindowService,
                                                           FileReaderService fileReaderService,
                                                           FileOpenerService fileOpenerService,
                                                           FileModificationTrackerService fileModificationTrackerService,
                                                           FileModificationRestarterService fileModificationRestarterService,
                                                           FileModificationErrorDialogFactory fileModificationErrorDialogFactory,
                                                           BackgroundTaskMapperService backgroundTaskMapperService) {
        return new ModificationQueueViewer(project, provisionalModificationViewer, codactorToolWindowService, fileReaderService, fileOpenerService, fileModificationTrackerService, fileModificationRestarterService, fileModificationErrorDialogFactory, backgroundTaskMapperService);
    }

    @Singleton
    @Provides
    public CodactorConsole codactorConsole(Project project,
                                           PromptContextServiceFactory promptContextServiceFactory,
                                           CodactorToolWindowService codactorToolWindowService,
                                           SelectedFileFetcherService selectedFileFetcherService,
                                           CodeSnippetExtractorService codeSnippetExtractorService,
                                           InquiryService inquiryService,
                                           OpenAiModelService openAiModelService,
                                           CodeModificationService codeModificationService,
                                           Gson gson,
                                           ProjectSearchService projectSearchService,
                                           CodactorUmlBuilderApplication codactorUmlBuilderApplication,
                                           MultiFileCreateDialogFactory multiFileCreateDialogFactory,
                                           PromptContextBuilderDialogFactory promptContextBuilderDialogFactory,
                                           InquiryViewerFactory inquiryViewerFactory) {
        return new CodactorConsole(project, promptContextServiceFactory, codactorToolWindowService, selectedFileFetcherService, codeSnippetExtractorService, inquiryService, openAiModelService, codeModificationService, gson, projectSearchService, codactorUmlBuilderApplication, multiFileCreateDialogFactory, promptContextBuilderDialogFactory, inquiryViewerFactory);
    }

    @Singleton
    @Provides
    public CodactorUmlBuilderApplication codactorUmlBuilderApplication(CodactorUmlBuilderApplicationModelFactory codactorUmlBuilderApplicationModelFactory, CodactorUmlBuilderViewFactory codactorUmlBuilderViewFactory) {
        CodactorUmlBuilderApplication app;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("mac")) {
            app = new CodactorUmlBuilderOSXApplication();
        } else if (os.startsWith("win")) {
            //app = new MDIApplication();
            app = new CodactorUmlBuilderOSXApplication();
        } else {
            app = new CodactorUmlBuilderSDIApplication();
        }

        CodactorUmlBuilderApplicationModel model = codactorUmlBuilderApplicationModelFactory.create();
        /*model.setName("JHotDraw Draw");
        model.setVersion(getClass().getPackage().getImplementationVersion());
        model.setCopyright("Copyright 2006-2009 (c) by the authors of JHotDraw and all its contributors.\n" +
                "This software is licensed under LGPL or Creative Commons 3.0 Attribution.");*/
        model.setViewFactory(codactorUmlBuilderViewFactory::create);
        app.setModel(model);
        String[] args = new String[0];
        app.launch(args);
        return app;
    }

    @Provides
    public CodactorUmlBuilderView codactorUmlBuilderView(CustomMouseAdapterFactory customMouseAdapterFactory) {
        return new CodactorUmlBuilderView(customMouseAdapterFactory);
    }
}
