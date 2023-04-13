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
import com.translator.service.constructor.CodeFileGeneratorService;
import com.translator.service.file.FileOpenerService;
import com.translator.service.openai.OpenAiApiKeyService;
import com.translator.service.openai.OpenAiModelService;
import com.translator.service.file.FileReaderService;
import com.translator.service.ui.tool.CodactorToolWindowService;
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
        install(new CodeTranslatorServiceConfig());
        install(new FactoryModuleBuilder().build(ProvisionalModificationCustomizerFactory.class));
        install(new FactoryModuleBuilder().build(PromptContextBuilderFactory.class));
    }

    @Singleton
    @Provides
    public Project project() {
        return project;
    }

    @Singleton
    @Provides
    public ProvisionalModificationViewer codeSnippetListViewer(CodactorToolWindowService codactorToolWindowService,
                                                               ProvisionalModificationCustomizerFactory provisionalModificationCustomizerFactory) {
        return new ProvisionalModificationViewer(codactorToolWindowService, provisionalModificationCustomizerFactory);
    }

    @Singleton
    @Provides
    InquiryViewer inquiryViewer(OpenAiApiKeyService openAiApiKeyService,
                                          OpenAiModelService openAiModelService,
                                          CodactorToolWindowService codactorToolWindowService,
                                          CodeFileGeneratorService codeFileGeneratorService,
                                          InquiryDao inquiryDao,
                                          @Named("inquiryTaskExecutor")LimitedSwingWorkerExecutor inquiryTaskExecutor) {
        return new InquiryViewer(openAiApiKeyService, openAiModelService, codactorToolWindowService, codeFileGeneratorService, inquiryDao, inquiryTaskExecutor);
    }

    @Singleton
    @Provides
    public InquiryListViewer inquiryListViewer(InquiryViewer inquiryViewer,
                                               CodactorToolWindowService codactorToolWindowService,
                                               InquiryDao inquiryDao,
                                               @Named("historyFetchingTaskExecutor") LimitedSwingWorkerExecutor historyFetchingTaskExecutor) {
        return new InquiryListViewer(inquiryViewer, codactorToolWindowService, inquiryDao, historyFetchingTaskExecutor);
    }

    @Singleton
    @Provides
    public HistoricalModificationListViewer historicalModificationListViewer(InquiryViewer inquiryViewer,
                                                                             CodactorToolWindowService codactorToolWindowService,
                                                                             CodeModificationHistoryDao codeModificationHistoryDao,
                                                                             @Named("historyFetchingTaskExecutor") LimitedSwingWorkerExecutor historyFetchingTaskExecutor) {
        return new HistoricalModificationListViewer(inquiryViewer, codactorToolWindowService, codeModificationHistoryDao, historyFetchingTaskExecutor);
    }

    @Singleton
    @Provides
    public ModificationQueueViewer ModificationQueueViewerToolWindow(ProvisionalModificationViewer provisionalModificationViewer,
                                                                     CodactorToolWindowService codactorToolWindowService,
                                                                     FileReaderService fileReaderService,
                                                                     FileOpenerService fileOpenerService) {
        return new ModificationQueueViewer(provisionalModificationViewer, codactorToolWindowService, fileReaderService, fileOpenerService);
    }
}
