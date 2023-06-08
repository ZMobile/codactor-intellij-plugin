package com.translator.view;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.intellij.openapi.project.Project;
import com.translator.dao.history.CodeModificationHistoryDao;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.service.CodeTranslatorServiceConfig;
import com.translator.service.codactor.code.CodeSnippetExtractorService;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.factory.AutomaticCodeModificationServiceFactory;
import com.translator.service.codactor.task.BackgroundTaskMapperService;
import com.translator.view.uml.factory.CodactorUmlBuilderApplicationModelFactory;
import com.translator.view.uml.factory.CodactorUmlBuilderViewFactory;
import com.translator.view.uml.factory.adapter.CustomMouseAdapterFactory;
import com.translator.view.uml.factory.dialog.PromptNodeDialogFactory;
import com.translator.service.codactor.file.CodeFileGeneratorService;
import com.translator.service.codactor.file.FileOpenerService;
import com.translator.service.codactor.file.FileReaderService;
import com.translator.service.codactor.file.SelectedFileFetcherService;
import com.translator.service.codactor.inquiry.InquiryService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.openai.OpenAiApiKeyService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.console.CodactorConsole;
import com.translator.view.codactor.factory.PromptContextBuilderFactory;
import com.translator.view.codactor.factory.ProvisionalModificationCustomizerFactory;
import com.translator.view.uml.CodactorUmlBuilderView;
import com.translator.view.uml.factory.tool.PromptNodeCreationToolFactory;
import com.translator.view.codactor.viewer.*;

public class CodeTranslatorViewConfig extends AbstractModule {
    private Project project;

    public CodeTranslatorViewConfig(Project project) {
        this.project = project;
    }

    @Override
    protected void configure() {
        install(new CodeTranslatorServiceConfig(project));
        install(new FactoryModuleBuilder().build(ProvisionalModificationCustomizerFactory.class));
        install(new FactoryModuleBuilder().build(PromptContextBuilderFactory.class));
        install(new FactoryModuleBuilder().build(CodactorUmlBuilderViewFactory.class));
        install(new FactoryModuleBuilder().build(CodactorUmlBuilderApplicationModelFactory.class));
        install(new FactoryModuleBuilder().build(PromptNodeDialogFactory.class));
        install(new FactoryModuleBuilder().build(CustomMouseAdapterFactory.class));
        install(new FactoryModuleBuilder().build(PromptNodeCreationToolFactory.class));
    }

    @Singleton
    @Provides
    public ProvisionalModificationViewer codeSnippetListViewer(CodactorToolWindowService codactorToolWindowService,
                                                               FileModificationTrackerService fileModificationTrackerService,
                                                               FileOpenerService fileOpenerService,
                                                               ProvisionalModificationCustomizerFactory provisionalModificationCustomizerFactory) {
        return new ProvisionalModificationViewer(codactorToolWindowService, fileModificationTrackerService, fileOpenerService, provisionalModificationCustomizerFactory);
    }

    @Singleton
    @Provides
    public InquiryViewer inquiryViewer(Project project,
                                CodactorToolWindowService codactorToolWindowService,
                                CodeFileGeneratorService codeFileGeneratorService,
                                InquiryService inquiryService,
                                       OpenAiModelService openAiModelService,
                                       PromptContextBuilderFactory promptContextBuilderFactory) {
        return new InquiryViewer(project, codactorToolWindowService, codeFileGeneratorService, inquiryService, openAiModelService, promptContextBuilderFactory);
    }

    @Singleton
    @Provides
    public InquiryListViewer inquiryListViewer(InquiryViewer inquiryViewer,
                                               CodactorToolWindowService codactorToolWindowService,
                                               InquiryDao inquiryDao) {
        return new InquiryListViewer(inquiryViewer, codactorToolWindowService, inquiryDao);
    }

    @Singleton
    @Provides
    public HistoricalModificationListViewer historicalModificationListViewer(InquiryViewer inquiryViewer,
                                                                             CodactorToolWindowService codactorToolWindowService,
                                                                             CodeModificationHistoryDao codeModificationHistoryDao) {
        return new HistoricalModificationListViewer(inquiryViewer, codactorToolWindowService, codeModificationHistoryDao);
    }

    @Singleton
    @Provides
    public ModificationQueueViewer ModificationQueueViewer(Project project,
                                                           ProvisionalModificationViewer provisionalModificationViewer,
                                                           CodactorToolWindowService codactorToolWindowService,
                                                           FileReaderService fileReaderService,
                                                           FileOpenerService fileOpenerService,
                                                           OpenAiApiKeyService openAiApiKeyService,
                                                           OpenAiModelService openAiModelService,
                                                           FileModificationTrackerService fileModificationTrackerService,
                                                           BackgroundTaskMapperService backgroundTaskMapperService) {
        return new ModificationQueueViewer(project, provisionalModificationViewer, codactorToolWindowService, fileReaderService, fileOpenerService, openAiApiKeyService, openAiModelService, fileModificationTrackerService, backgroundTaskMapperService);
    }

    @Singleton
    @Provides
    public CodactorConsole codactorConsole(Project project,
                                           PromptContextService promptContextService,
                                           CodactorToolWindowService codactorToolWindowService,
                                           SelectedFileFetcherService selectedFileFetcherService,
                                           CodeSnippetExtractorService codeSnippetExtractorService,
                                           InquiryService inquiryService,
                                           CodeFileGeneratorService codeFileGeneratorService,
                                           OpenAiModelService openAiModelService,
                                           AutomaticCodeModificationServiceFactory automaticCodeModificationServiceFactory,
                                           PromptContextBuilderFactory promptContextBuilderFactory,
                                           CodactorUmlBuilderViewFactory codactorUmlBuilderViewFactory,
                                           CodactorUmlBuilderApplicationModelFactory codactorUmlBuilderApplicationModelFactory) {
        return new CodactorConsole(project, promptContextService, codactorToolWindowService, selectedFileFetcherService, codeSnippetExtractorService, inquiryService, codeFileGeneratorService, openAiModelService, automaticCodeModificationServiceFactory, promptContextBuilderFactory, codactorUmlBuilderViewFactory, codactorUmlBuilderApplicationModelFactory);
    }

    @Provides
    public CodactorUmlBuilderView codactorUmlBuilderView(CustomMouseAdapterFactory customMouseAdapterFactory) {
        return new CodactorUmlBuilderView(customMouseAdapterFactory);
    }
}
