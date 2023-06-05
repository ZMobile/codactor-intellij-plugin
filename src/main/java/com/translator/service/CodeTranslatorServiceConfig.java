package com.translator.service;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.intellij.openapi.project.Project;
import com.translator.dao.CodeTranslatorDaoConfig;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.service.code.*;
import com.translator.service.context.PromptContextService;
import com.translator.service.context.PromptContextServiceImpl;
import com.translator.service.copy.DirectoryCopierService;
import com.translator.service.copy.DirectoryCopierServiceImpl;
import com.translator.service.factory.AutomaticCodeModificationServiceFactory;
import com.translator.service.factory.AutomaticMassCodeModificationServiceFactory;
import com.translator.service.file.*;
import com.translator.service.inquiry.InquiryService;
import com.translator.service.inquiry.InquiryServiceImpl;
import com.translator.service.line.LineCounterService;
import com.translator.service.line.LineCounterServiceImpl;
import com.translator.service.modification.CodeModificationService;
import com.translator.service.modification.CodeModificationServiceImpl;
import com.translator.service.modification.multi.MultiFileModificationService;
import com.translator.service.modification.multi.MultiFileModificationServiceImpl;
import com.translator.service.modification.tracking.CodeRangeTrackerService;
import com.translator.service.modification.tracking.CodeRangeTrackerServiceImpl;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.modification.tracking.FileModificationTrackerServiceImpl;
import com.translator.service.modification.tracking.listener.*;
import com.translator.service.openai.OpenAiApiKeyService;
import com.translator.service.openai.OpenAiApiKeyServiceImpl;
import com.translator.service.openai.OpenAiModelService;
import com.translator.service.openai.OpenAiModelServiceImpl;
import com.translator.service.task.BackgroundTaskMapperService;
import com.translator.service.task.BackgroundTaskMapperServiceImpl;
import com.translator.service.ui.ModificationQueueListButtonService;
import com.translator.service.ui.ModificationQueueListButtonServiceImpl;
import com.translator.service.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.ui.measure.TextAreaHeightCalculatorServiceImpl;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.service.ui.tool.CodactorToolWindowServiceImpl;
import com.translator.service.ui.tool.ToolWindowService;
import com.translator.service.ui.tool.ToolWindowServiceImpl;
import com.translator.service.uml.NodeDialogWindowMapperService;
import com.translator.service.uml.NodeDialogWindowMapperServiceImpl;

public class CodeTranslatorServiceConfig extends AbstractModule {
    private Project project;

    public CodeTranslatorServiceConfig(Project project) {
        this.project = project;
    }

    @Override
    protected void configure() {
        install(new CodeTranslatorDaoConfig());
        install(new FactoryModuleBuilder().build(AutomaticCodeModificationServiceFactory.class));
        install(new FactoryModuleBuilder().build(AutomaticMassCodeModificationServiceFactory.class));
        bind(CodeModificationService.class).to(CodeModificationServiceImpl.class);
        bind(OpenAiApiKeyService.class).to(OpenAiApiKeyServiceImpl.class).asEagerSingleton();
        bind(OpenAiModelService.class).to(OpenAiModelServiceImpl.class).asEagerSingleton();
        bind(FileCreatorService.class).to(FileCreatorServiceImpl.class);
        bind(FileReaderService.class).to(FileReaderServiceImpl.class);
        bind(FileOpenerService.class).to(FileOpenerServiceImpl.class);
        bind(SelectedFileFetcherService.class).to(SelectedFileFetcherServiceImpl.class);
        bind(PromptContextService.class).to(PromptContextServiceImpl.class);
        bind(DirectoryCopierService.class).to(DirectoryCopierServiceImpl.class);
        bind(LineCounterService.class).to(LineCounterServiceImpl.class);
        bind(TabKeyListenerService.class).to(TabKeyListenerServiceImpl.class).asEagerSingleton();
        bind(EditorClickHandlerService.class).to(EditorClickHandlerServiceImpl.class).asEagerSingleton();
        bind(TextAreaHeightCalculatorService.class).to(TextAreaHeightCalculatorServiceImpl.class);
        bind(ModificationQueueListButtonService.class).to(ModificationQueueListButtonServiceImpl.class);
        bind(ToolWindowService.class).to(ToolWindowServiceImpl.class);
        bind(CodactorToolWindowService.class).to(CodactorToolWindowServiceImpl.class).asEagerSingleton();
        bind(EditorExtractorService.class).to(EditorExtractorServiceImpl.class);
        bind(CodeHighlighterService.class).to(CodeHighlighterServiceImpl.class);
        bind(CodeSnippetExtractorService.class).to(CodeSnippetExtractorServiceImpl.class);
        bind(CodeRangeTrackerService.class).to(CodeRangeTrackerServiceImpl.class);
        bind(GptToLanguageTransformerService.class).to(GptToLanguageTransformerServiceImpl.class);
        bind(UneditableSegmentListenerService.class).to(UneditableSegmentListenerServiceImpl.class);
        bind(GuardedBlockService.class).to(GuardedBlockServiceImpl.class);
        bind(RangeReplaceService.class).to(RangeReplaceServiceImpl.class);
        bind(InquiryService.class).to(InquiryServiceImpl.class);
        bind(RenameFileService.class).to(RenameFileServiceImpl.class);
        bind(BackgroundTaskMapperService.class).to(BackgroundTaskMapperServiceImpl.class).asEagerSingleton();
        bind(NodeDialogWindowMapperService.class).to(NodeDialogWindowMapperServiceImpl.class).asEagerSingleton();
    }

    @Singleton
    @Provides
    public Project project() {
        return project;
    }

    @Singleton
    @Provides
    public FileModificationTrackerService getFileModificationTrackerService(Project project,
                                                                            CodeHighlighterService codeHighlighterService,
                                                                            CodeSnippetExtractorService codeSnippetExtractorService,
                                                                            CodeRangeTrackerService codeRangeTrackerService,
                                                                            GuardedBlockService guardedBlockService,
                                                                            RangeReplaceService rangeReplaceService,
                                                                            EditorClickHandlerService editorClickHandlerService,
                                                                            RenameFileService renameFileService,
                                                                            BackgroundTaskMapperService backgroundTaskMapperService) {
        return new FileModificationTrackerServiceImpl(project, codeHighlighterService, codeSnippetExtractorService, codeRangeTrackerService, guardedBlockService, rangeReplaceService, editorClickHandlerService, renameFileService, backgroundTaskMapperService);
    }

    @Singleton
    @Provides
    public CodeFileGeneratorService getCodeFileGeneratorService(Project project,
                                                                InquiryDao inquiryDao,
                                                                CodeModificationService codeModificationService,
                                                                FileModificationTrackerService fileModificationTrackerService,
                                                                OpenAiApiKeyService openAiApiKeyService,
                                                                OpenAiModelService openAiModelService,
                                                                FileCreatorService fileCreatorService) {
        return new CodeFileGeneratorServiceImpl(project, inquiryDao, codeModificationService, fileModificationTrackerService, openAiApiKeyService, openAiModelService, fileCreatorService);
    }

    @Singleton
    @Provides
    public MultiFileModificationService multiFileModificationService(Project project,
                                                                    InquiryDao inquiryDao,
                                                                    FileModificationTrackerService fileModificationTrackerService,
                                                                    CodeModificationService codeModificationService,
                                                                    CodeSnippetExtractorService codeSnippetExtractorService,
                                                                    OpenAiApiKeyService openAiApiKeyService,
                                                                    OpenAiModelService openAiModelService,
                                                                     Gson gson) {
        return new MultiFileModificationServiceImpl(project, inquiryDao, fileModificationTrackerService, codeModificationService, codeSnippetExtractorService, openAiApiKeyService, openAiModelService, gson);
    }
}
