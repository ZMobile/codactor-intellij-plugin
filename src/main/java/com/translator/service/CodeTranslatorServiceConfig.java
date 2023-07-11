package com.translator.service;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.intellij.openapi.project.Project;
import com.translator.dao.CodeTranslatorDaoConfig;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.service.codactor.editor.*;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.context.PromptContextServiceImpl;
import com.translator.service.codactor.copy.DirectoryCopierService;
import com.translator.service.codactor.copy.DirectoryCopierServiceImpl;
import com.translator.service.codactor.editor.diff.DiffEditorGeneratorService;
import com.translator.service.codactor.editor.diff.DiffEditorGeneratorServiceImpl;
import com.translator.service.codactor.editor.diff.GitDiffStingGeneratorService;
import com.translator.service.codactor.editor.diff.GitDiffStingGeneratorServiceImpl;
import com.translator.service.codactor.factory.CodeFileGeneratorServiceFactory;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.file.*;
import com.translator.service.codactor.inquiry.functions.*;
import com.translator.service.codactor.inquiry.*;
import com.translator.service.codactor.line.LineCounterService;
import com.translator.service.codactor.line.LineCounterServiceImpl;
import com.translator.service.codactor.modification.*;
import com.translator.service.codactor.modification.multi.MultiFileModificationService;
import com.translator.service.codactor.modification.multi.MultiFileModificationServiceImpl;
import com.translator.service.codactor.modification.tracking.CodeRangeTrackerService;
import com.translator.service.codactor.modification.tracking.CodeRangeTrackerServiceImpl;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerServiceImpl;
import com.translator.service.codactor.modification.tracking.listener.*;
import com.translator.service.codactor.openai.OpenAiApiKeyService;
import com.translator.service.codactor.openai.OpenAiApiKeyServiceImpl;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.openai.OpenAiModelServiceImpl;
import com.translator.service.codactor.task.BackgroundTaskMapperService;
import com.translator.service.codactor.task.BackgroundTaskMapperServiceImpl;
import com.translator.service.codactor.transformer.HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformer;
import com.translator.service.codactor.transformer.HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformerImpl;
import com.translator.service.codactor.transformer.QueuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerService;
import com.translator.service.codactor.transformer.QueuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerServiceImpl;
import com.translator.service.codactor.ui.ModificationQueueListButtonService;
import com.translator.service.codactor.ui.ModificationQueueListButtonServiceImpl;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorServiceImpl;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowServiceImpl;
import com.translator.service.codactor.ui.tool.ToolWindowService;
import com.translator.service.codactor.ui.tool.ToolWindowServiceImpl;
import com.translator.service.uml.node.*;
import com.translator.service.uml.node.query.ConnectionQueryService;
import com.translator.service.uml.node.query.ConnectionQueryServiceImpl;
import com.translator.service.uml.node.query.NodeQueryService;
import com.translator.service.uml.node.query.NodeQueryServiceImpl;
import com.translator.service.uml.node.runner.*;
import com.translator.view.codactor.factory.dialog.FileModificationErrorDialogFactory;
import com.translator.view.codactor.factory.dialog.PromptContextBuilderDialogFactory;

public class CodeTranslatorServiceConfig extends AbstractModule {
    private Project project;

    public CodeTranslatorServiceConfig(Project project) {
        this.project = project;
    }

    @Override
    protected void configure() {
        install(new CodeTranslatorDaoConfig());
        install(new FactoryModuleBuilder().build(PromptContextBuilderDialogFactory.class));
        install(new FactoryModuleBuilder().build(PromptContextServiceFactory.class));
        install(new FactoryModuleBuilder().build(CodeFileGeneratorServiceFactory.class));
        bind(PromptContextService.class).to(PromptContextServiceImpl.class).asEagerSingleton();
        bind(AutomaticMassCodeModificationService.class).to(AutomaticMassCodeModificationServiceImpl.class);
        bind(AutomaticCodeModificationService.class).to(AutomaticCodeModificationServiceImpl.class);
        bind(CodeModificationService.class).to(CodeModificationServiceImpl.class);
        bind(OpenAiApiKeyService.class).to(OpenAiApiKeyServiceImpl.class).asEagerSingleton();
        bind(OpenAiModelService.class).to(OpenAiModelServiceImpl.class).asEagerSingleton();
        bind(FileCreatorService.class).to(FileCreatorServiceImpl.class);
        bind(FileReaderService.class).to(FileReaderServiceImpl.class);
        bind(FileOpenerService.class).to(FileOpenerServiceImpl.class);
        bind(SelectedFileFetcherService.class).to(SelectedFileFetcherServiceImpl.class);
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
        bind(GuardedBlockService.class).to(GuardedBlockServiceImpl.class);
        bind(RangeReplaceService.class).to(RangeReplaceServiceImpl.class);
        bind(InquiryService.class).to(InquiryServiceImpl.class);
        bind(RenameFileService.class).to(RenameFileServiceImpl.class);
        bind(BackgroundTaskMapperService.class).to(BackgroundTaskMapperServiceImpl.class).asEagerSingleton();
        bind(NodeDialogWindowMapperService.class).to(NodeDialogWindowMapperServiceImpl.class).asEagerSingleton();
        bind(PromptHighlighterService.class).to(PromptHighlighterServiceImpl.class);
        bind(ConnectionQueryService.class).to(ConnectionQueryServiceImpl.class);
        bind(NodeQueryService.class).to(NodeQueryServiceImpl.class);
        bind(NodeRunnerManagerService.class).to(NodeRunnerManagerServiceImpl.class);
        bind(PromptNodeRunnerService.class).to(PromptNodeRunnerServiceImpl.class);
        bind(FileModificationRestarterService.class).to(FileModificationRestarterServiceImpl.class);
        bind(HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformer.class).to(HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformerImpl.class);
        bind(DiffEditorGeneratorService.class).to(DiffEditorGeneratorServiceImpl.class);
        bind(FileModificationSuggestionDiffViewerService.class).to(FileModificationSuggestionDiffViewerServiceImpl.class);
        bind(CodactorFunctionGeneratorService.class).to(CodactorFunctionGeneratorServiceImpl.class);
        bind(CodactorFunctionToLabelMapperService.class).to(CodactorFunctionToLabelMapperServiceImpl.class);
        bind(InquiryChatListFunctionCallCompressorService.class).to(InquiryChatListFunctionCallCompressorServiceImpl.class);
        bind(InquiryFunctionCallProcessorService.class).to(InquiryFunctionCallProcessorServiceImpl.class);
        bind(QueuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerService.class).to(QueuedFileModificationObjectHolderToQueuedFileModificationObjectReferenceHolderTransformerServiceImpl.class);
        bind(GitDiffStingGeneratorService.class).to(GitDiffStingGeneratorServiceImpl.class);
        bind(InquirySystemMessageGeneratorService.class).to(InquirySystemMessageGeneratorServiceImpl.class);
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
                                                                            BackgroundTaskMapperService backgroundTaskMapperService,
                                                                            DiffEditorGeneratorService diffEditorGeneratorService) {
        return new FileModificationTrackerServiceImpl(project, codeHighlighterService, codeSnippetExtractorService, codeRangeTrackerService, guardedBlockService, rangeReplaceService, editorClickHandlerService, renameFileService, backgroundTaskMapperService, diffEditorGeneratorService);
    }

    @Singleton
    @Provides
    public MassCodeFileGeneratorService getCodeFileGeneratorService(Project project,
                                                                    InquiryDao inquiryDao,
                                                                    CodeModificationService codeModificationService,
                                                                    FileModificationTrackerService fileModificationTrackerService,
                                                                    OpenAiApiKeyService openAiApiKeyService,
                                                                    OpenAiModelService openAiModelService,
                                                                    FileCreatorService fileCreatorService,
                                                                    InquirySystemMessageGeneratorService inquirySystemMessageGeneratorService) {
        return new MassCodeFileGeneratorServiceImpl(project, inquiryDao, codeModificationService, fileModificationTrackerService, openAiApiKeyService, openAiModelService, fileCreatorService, inquirySystemMessageGeneratorService);
    }

    @Singleton
    @Provides
    public MultiFileModificationService multiFileModificationService(Project project,
                                                                     InquiryDao inquiryDao,
                                                                     FileModificationTrackerService fileModificationTrackerService,
                                                                     FileModificationRestarterService fileModificationRestarterService,
                                                                     CodeModificationService codeModificationService,
                                                                     CodeSnippetExtractorService codeSnippetExtractorService,
                                                                     OpenAiApiKeyService openAiApiKeyService,
                                                                     OpenAiModelService openAiModelService,
                                                                     InquirySystemMessageGeneratorService inquirySystemMessageGeneratorService,
                                                                     FileModificationErrorDialogFactory fileModificationErrorDialogFactory,
                                                                     Gson gson) {
        return new MultiFileModificationServiceImpl(project, inquiryDao, fileModificationTrackerService, fileModificationRestarterService, codeModificationService, codeSnippetExtractorService, openAiApiKeyService, openAiModelService, inquirySystemMessageGeneratorService, fileModificationErrorDialogFactory, gson);
    }
}
