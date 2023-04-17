package com.translator.view;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;
import com.intellij.openapi.project.Project;
import com.translator.dao.history.CodeModificationHistoryDao;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.service.CodeTranslatorServiceConfig;
import com.translator.service.code.CodeSnippetExtractorService;
import com.translator.service.code.RangeReplaceService;
import com.translator.service.constructor.CodeFileGeneratorService;
import com.translator.service.context.PromptContextService;
import com.translator.service.factory.AutomaticCodeModificationServiceFactory;
import com.translator.service.file.FileOpenerService;
import com.translator.service.file.SelectedFileFetcherService;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.openai.OpenAiApiKeyService;
import com.translator.service.openai.OpenAiModelService;
import com.translator.service.file.FileReaderService;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.view.console.CodactorConsole;
import com.translator.view.factory.PromptContextBuilderFactory;
import com.translator.view.factory.ProvisionalModificationCustomizerFactory;
import com.translator.view.viewer.ModificationQueueViewer;
import com.translator.view.viewer.ProvisionalModificationViewer;
import com.translator.view.viewer.*;
import com.translator.worker.LimitedSwingWorkerExecutor;

import java.util.Map;

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
    }

    @Singleton
    @Provides
    public ProvisionalModificationViewer codeSnippetListViewer(CodactorToolWindowService codactorToolWindowService,
                                                               FileModificationTrackerService fileModificationTrackerService,
                                                               ProvisionalModificationCustomizerFactory provisionalModificationCustomizerFactory) {
        return new ProvisionalModificationViewer(codactorToolWindowService, fileModificationTrackerService, provisionalModificationCustomizerFactory);
    }

    @Singleton
    @Provides
    InquiryViewer inquiryViewer(Project project,
                                OpenAiApiKeyService openAiApiKeyService,
                                OpenAiModelService openAiModelService,
                                CodactorToolWindowService codactorToolWindowService,
                                CodeFileGeneratorService codeFileGeneratorService,
                                InquiryDao inquiryDao) {
        return new InquiryViewer(project, openAiApiKeyService, openAiModelService, codactorToolWindowService, codeFileGeneratorService, inquiryDao);
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
                                                           FileOpenerService fileOpenerService) {
        return new ModificationQueueViewer(project, provisionalModificationViewer, codactorToolWindowService, fileReaderService, fileOpenerService);
    }

    @Singleton
    @Provides
    public CodactorConsole codactorConsole(Project project,
                                           PromptContextService promptContextService,
                                           SelectedFileFetcherService selectedFileFetcherService,
                                           CodeSnippetExtractorService codeSnippetExtractorService,
                                           AutomaticCodeModificationServiceFactory automaticCodeModificationServiceFactory) {
        return new CodactorConsole(project, promptContextService, selectedFileFetcherService, codeSnippetExtractorService, automaticCodeModificationServiceFactory);
    }
}
