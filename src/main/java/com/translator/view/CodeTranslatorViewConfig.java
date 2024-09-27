package com.translator.view;

import com.github.javaparser.quality.Nullable;
import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.intellij.openapi.project.Project;
import com.translator.dao.history.CodeModificationHistoryDao;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.service.CodeTranslatorServiceConfig;
import com.translator.service.codactor.ai.modification.queued.QueuedFileModificationObjectHolderQueryService;
import com.translator.service.codactor.ai.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.ai.modification.tracking.multi.MultiFileModificationTrackerService;
import com.translator.service.codactor.ai.modification.tracking.suggestion.modification.FileModificationSuggestionModificationTrackerService;
import com.translator.service.codactor.ide.editor.CodeHighlighterService;
import com.translator.service.codactor.ide.editor.CodeSnippetExtractorService;
import com.translator.service.codactor.ide.editor.RangeReplaceService;
import com.translator.service.codactor.ide.editor.diff.DiffEditorGeneratorService;
import com.translator.service.codactor.ide.editor.psi.FindImplementationsService;
import com.translator.service.codactor.ide.editor.psi.FindUsagesService;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.ide.file.FileOpenerService;
import com.translator.service.codactor.ide.file.FileReaderService;
import com.translator.service.codactor.ide.file.SelectedFileFetcherService;
import com.translator.service.codactor.ai.chat.functions.InquiryChatListFunctionCallCompressorService;
import com.translator.service.codactor.ai.chat.functions.InquiryFunctionCallProcessorService;
import com.translator.service.codactor.ai.chat.inquiry.InquiryService;
import com.translator.service.codactor.ai.modification.AiCodeModificationService;
import com.translator.service.codactor.ai.modification.AiFileModificationRestarterService;
import com.translator.service.codactor.ai.modification.diff.AiFileModificationSuggestionDiffViewerService;
import com.translator.service.codactor.ai.openai.OpenAiModelService;
import com.translator.service.codactor.io.BackgroundTaskMapperService;
import com.translator.service.codactor.ui.ModificationTypeComboBoxService;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.console.CodactorConsole;
import com.translator.view.codactor.dialog.modification.ProvisionalModificationCustomizerDialogManager;
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
    public ProvisionalModificationViewer provisionalModificationViewer(CodactorToolWindowService codactorToolWindowService,
                                                                           FileModificationTrackerService fileModificationTrackerService,
                                                                           ProvisionalModificationCustomizerDialogManager provisionalModificationCustomizerDialogManager,
                                                                           FileOpenerService fileOpenerService,
                                                                           AiFileModificationSuggestionDiffViewerService aiFileModificationSuggestionDiffViewerService,
                                                                           DiffEditorGeneratorService diffEditorGeneratorService) {
        return new ProvisionalModificationViewer(codactorToolWindowService, fileModificationTrackerService, provisionalModificationCustomizerDialogManager, fileOpenerService, aiFileModificationSuggestionDiffViewerService, diffEditorGeneratorService);
    }

    @Singleton
    @Provides
    public InquiryViewer inquiryViewer(Gson gson,
                                       Project project,
                                       CodactorToolWindowService codactorToolWindowService,
                                       MultiFileCreateDialogFactory multiFileCreateDialogFactory,
                                       InquiryService inquiryService,
                                       PromptContextServiceFactory promptContextServiceFactory,
                                       OpenAiModelService openAiModelService,
                                       TextAreaHeightCalculatorService textAreaHeightCalculatorService,
                                       InquiryChatListFunctionCallCompressorService inquiryChatListFunctionCallCompressorService,
                                       InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService) {
        return new InquiryViewer(gson, project, codactorToolWindowService, multiFileCreateDialogFactory, inquiryService, promptContextServiceFactory, openAiModelService, textAreaHeightCalculatorService, inquiryChatListFunctionCallCompressorService, inquiryFunctionCallProcessorService);
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
                                                           FileModificationSuggestionModificationTrackerService fileModificationSuggestionModificationTrackerService,
                                                           MultiFileModificationTrackerService multiFileModificationTrackerService,
                                                           QueuedFileModificationObjectHolderQueryService queuedFileModificationObjectHolderQueryService,
                                                           AiFileModificationRestarterService aiFileModificationRestarterService,
                                                           FileModificationErrorDialogFactory fileModificationErrorDialogFactory,
                                                           BackgroundTaskMapperService backgroundTaskMapperService) {
        return new ModificationQueueViewer(project, provisionalModificationViewer, codactorToolWindowService, fileReaderService, fileOpenerService, fileModificationTrackerService, fileModificationSuggestionModificationTrackerService, multiFileModificationTrackerService, queuedFileModificationObjectHolderQueryService, aiFileModificationRestarterService, fileModificationErrorDialogFactory, backgroundTaskMapperService);
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
                                           ModificationTypeComboBoxService modificationTypeComboBoxService,
                                           AiCodeModificationService aiCodeModificationService,
                                           Gson gson,
                                           FindImplementationsService findImplementationsService,
                                           FindUsagesService findUsagesService,
                                           CodeHighlighterService codeHighlighterService,
                                           RangeReplaceService rangeReplaceService,
                                           //CodactorUmlBuilderApplication codactorUmlBuilderApplication,
                                           MultiFileCreateDialogFactory multiFileCreateDialogFactory,
                                           PromptContextBuilderDialogFactory promptContextBuilderDialogFactory,
                                           InquiryViewerFactory inquiryViewerFactory,
                                           InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService,
                                           DiffEditorGeneratorService diffEditorGeneratorService) {
        return new CodactorConsole(project, promptContextServiceFactory, codactorToolWindowService, selectedFileFetcherService, codeSnippetExtractorService, inquiryService, openAiModelService, modificationTypeComboBoxService, aiCodeModificationService, gson, findImplementationsService, findUsagesService, codeHighlighterService, rangeReplaceService, multiFileCreateDialogFactory, promptContextBuilderDialogFactory, inquiryViewerFactory, inquiryFunctionCallProcessorService, diffEditorGeneratorService);
    }

    @Singleton
    @Provides
    public ProvisionalModificationCustomizerDialogManager provisionalModificationCustomizerDialogManager(ProvisionalModificationCustomizerDialogFactory provisionalModificationCustomizerDialogFactory,
                                                                                                         FileModificationTrackerService fileModificationTrackerService) {
        return new ProvisionalModificationCustomizerDialogManager(provisionalModificationCustomizerDialogFactory, fileModificationTrackerService);
    }

    @Singleton
    @Provides
    public CodactorUmlBuilderApplication codactorUmlBuilderApplication(CodactorUmlBuilderApplicationModelFactory codactorUmlBuilderApplicationModelFactory, CodactorUmlBuilderViewFactory codactorUmlBuilderViewFactory) {
        /*CodactorUmlBuilderApplication app;
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
                "This software is licensed under LGPL or Creative Commons 3.0 Attribution.");*
        model.setViewFactory(codactorUmlBuilderViewFactory::create);
        app.setModel(model);
        String[] args = new String[0];
        app.launch(args);*/
        return null;
    }

    @Provides
    public CodactorUmlBuilderView codactorUmlBuilderView(Project project, CustomMouseAdapterFactory customMouseAdapterFactory) {
        return new CodactorUmlBuilderView(project, customMouseAdapterFactory);
    }
}
