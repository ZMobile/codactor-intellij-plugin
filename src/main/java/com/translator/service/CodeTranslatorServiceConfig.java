package com.translator.service;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.intellij.openapi.project.Project;
import com.translator.dao.CodeTranslatorDaoConfig;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.dao.modification.CodeModificationDao;
import com.translator.service.codactor.ai.modification.diff.AiFileModificationSuggestionDiffViewerService;
import com.translator.service.codactor.ai.modification.diff.AiFileModificationSuggestionDiffViewerServiceImpl;
import com.translator.service.codactor.ai.modification.multi.MassAiCodeModificationService;
import com.translator.service.codactor.ai.modification.multi.MassAiCodeModificationServiceImpl;
import com.translator.service.codactor.ai.modification.tracking.*;
import com.translator.service.codactor.ai.modification.tracking.FileModificationManagementService;
import com.translator.service.codactor.ai.modification.tracking.FileModificationManagementServiceImpl;
import com.translator.service.codactor.ai.openai.connection.AzureConnectionService;
import com.translator.service.codactor.ai.openai.connection.AzureConnectionServiceImpl;
import com.translator.service.codactor.ai.openai.connection.CodactorConnectionService;
import com.translator.service.codactor.ai.openai.connection.CodactorConnectionServiceImpl;
import com.translator.service.codactor.ai.chat.context.PromptContextService;
import com.translator.service.codactor.ai.chat.context.PromptContextServiceImpl;
import com.translator.service.codactor.ide.directory.copy.DirectoryCopierService;
import com.translator.service.codactor.ide.directory.copy.DirectoryCopierServiceImpl;
import com.translator.service.codactor.ide.directory.FileDirectoryStructureQueryService;
import com.translator.service.codactor.ide.directory.FileDirectoryStructureQueryServiceImpl;
import com.translator.service.codactor.ide.editor.*;
import com.translator.service.codactor.ide.editor.EditorService;
import com.translator.service.codactor.ide.editor.EditorServiceImpl;
import com.translator.service.codactor.ide.editor.diff.DiffEditorGeneratorService;
import com.translator.service.codactor.ide.editor.diff.DiffEditorGeneratorServiceImpl;
import com.translator.service.codactor.ide.editor.diff.GitDiffStingGeneratorService;
import com.translator.service.codactor.ide.editor.diff.GitDiffStingGeneratorServiceImpl;
import com.translator.service.codactor.ide.editor.psi.*;
import com.translator.service.codactor.factory.CodeFileGeneratorServiceFactory;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.ide.file.*;
import com.translator.service.codactor.ai.chat.functions.*;
import com.translator.service.codactor.ai.chat.functions.search.ProjectSearchService;
import com.translator.service.codactor.ai.chat.functions.search.ProjectSearchServiceImpl;
import com.translator.service.codactor.ai.chat.inquiry.*;
import com.translator.service.codactor.line.LineCounterService;
import com.translator.service.codactor.line.LineCounterServiceImpl;
import com.translator.service.codactor.ai.modification.*;
import com.translator.service.codactor.ai.modification.history.FileModificationHistoryService;
import com.translator.service.codactor.ai.modification.history.FileModificationHistoryServiceImpl;
import com.translator.service.codactor.ai.modification.json.FileModificationDataHolderJsonCompatibilityService;
import com.translator.service.codactor.ai.modification.json.FileModificationDataHolderJsonCompatibilityServiceImpl;
import com.translator.service.codactor.ai.modification.multi.MultiFileAiModificationService;
import com.translator.service.codactor.ai.modification.multi.MultiFileAiModificationServiceImpl;
import com.translator.service.codactor.ide.handler.EditorClickHandlerService;
import com.translator.service.codactor.ide.handler.EditorClickHandlerServiceImpl;
import com.translator.service.codactor.io.TabKeyListenerService;
import com.translator.service.codactor.io.TabKeyListenerServiceImpl;
import com.translator.service.codactor.ai.openai.connection.DefaultConnectionService;
import com.translator.service.codactor.ai.openai.connection.DefaultConnectionServiceImpl;
import com.translator.service.codactor.ai.openai.OpenAiModelService;
import com.translator.service.codactor.ai.openai.OpenAiModelServiceImpl;
import com.translator.service.codactor.ai.runner.CodeRunnerService;
import com.translator.service.codactor.ai.runner.CodeRunnerServiceImpl;
import com.translator.service.codactor.io.BackgroundTaskMapperService;
import com.translator.service.codactor.io.BackgroundTaskMapperServiceImpl;
import com.translator.service.codactor.transformer.FileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService;
import com.translator.service.codactor.transformer.FileModificationObjectHolderToFileModificationDataReferenceHolderTransformerServiceImpl;
import com.translator.service.codactor.transformer.HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformer;
import com.translator.service.codactor.transformer.HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformerImpl;
import com.translator.service.codactor.transformer.modification.*;
import com.translator.service.codactor.ui.ModificationQueueListButtonService;
import com.translator.service.codactor.ui.ModificationQueueListButtonServiceImpl;
import com.translator.service.codactor.ui.ModificationTypeComboBoxService;
import com.translator.service.codactor.ui.ModificationTypeComboBoxServiceImpl;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorServiceImpl;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowServiceImpl;
import com.translator.service.codactor.ui.tool.ToolWindowService;
import com.translator.service.codactor.ui.tool.ToolWindowServiceImpl;
import com.translator.service.uml.node.NodeDialogWindowMapperService;
import com.translator.service.uml.node.NodeDialogWindowMapperServiceImpl;
import com.translator.service.uml.node.PromptHighlighterService;
import com.translator.service.uml.node.PromptHighlighterServiceImpl;
import com.translator.service.uml.node.query.ConnectionQueryService;
import com.translator.service.uml.node.query.ConnectionQueryServiceImpl;
import com.translator.service.uml.node.query.NodeQueryService;
import com.translator.service.uml.node.query.NodeQueryServiceImpl;
import com.translator.service.uml.node.runner.NodeRunnerManagerService;
import com.translator.service.uml.node.runner.NodeRunnerManagerServiceImpl;
import com.translator.service.uml.node.runner.PromptNodeRunnerService;
import com.translator.service.uml.node.runner.PromptNodeRunnerServiceImpl;
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
        bind(MassAiCodeModificationService.class).to(MassAiCodeModificationServiceImpl.class);
        bind(AiCodeModificationService.class).to(AiCodeModificationServiceImpl.class);
        bind(AiCodeModificationRecorderService.class).to(AiCodeModificationRecorderServiceImpl.class);
        bind(DefaultConnectionService.class).to(DefaultConnectionServiceImpl.class).asEagerSingleton();
        bind(OpenAiModelService.class).to(OpenAiModelServiceImpl.class).asEagerSingleton();
        bind(FileCreatorService.class).to(FileCreatorServiceImpl.class);
        bind(FileRemoverService.class).to(FileRemoverServiceImpl.class);
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
        bind(CodeSnippetIndexGetterService.class).to(CodeSnippetIndexGetterServiceImpl.class);
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
        bind(AiFileModificationRestarterService.class).to(AiFileModificationRestarterServiceImpl.class);
        bind(HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformer.class).to(HistoricalContextObjectDataHolderToHistoricalContextObjectHolderTransformerImpl.class);
        bind(DiffEditorGeneratorService.class).to(DiffEditorGeneratorServiceImpl.class);
        bind(AiFileModificationSuggestionDiffViewerService.class).to(AiFileModificationSuggestionDiffViewerServiceImpl.class);
        bind(CodactorFunctionGeneratorService.class).to(CodactorFunctionGeneratorServiceImpl.class);
        bind(CodactorFunctionToLabelMapperService.class).to(CodactorFunctionToLabelMapperServiceImpl.class);
        bind(InquiryChatListFunctionCallCompressorService.class).to(InquiryChatListFunctionCallCompressorServiceImpl.class);
        bind(InquiryFunctionCallProcessorService.class).to(InquiryFunctionCallProcessorServiceImpl.class);
        bind(FileModificationObjectHolderToFileModificationDataReferenceHolderTransformerService.class).to(FileModificationObjectHolderToFileModificationDataReferenceHolderTransformerServiceImpl.class);
        bind(GitDiffStingGeneratorService.class).to(GitDiffStingGeneratorServiceImpl.class);
        bind(InquirySystemMessageGeneratorService.class).to(InquirySystemMessageGeneratorServiceImpl.class);
        bind(HistoricalFileModificationDataHolderToFileModificationDataHolderTransformerService.class).to(HistoricalFileModificationDataHolderToFileModificationDataHolderTransformerServiceImpl.class);
        bind(FileModificationSuggestionRecordToFileModificationTransformerService.class).to(FileModificationSuggestionRecordToFileModificationTransformerServiceImpl.class);
        bind(FileModificationSuggestionModificationRecordToFileModificationSuggestionModificationTransformerService.class).to(FileModificationSuggestionModificationRecordToFileModificationSuggestionModificationTransformerServiceImpl.class);
        bind(FileModificationHistoryService.class).to(FileModificationHistoryServiceImpl.class);
        bind(FileModificationTrackerToFileModificationRangeDataTransformerService.class).to(FileModificationTrackerToFileModificationRangeDataTransformerServiceImpl.class);
        bind(CodeRunnerService.class).to(CodeRunnerServiceImpl.class);
        bind(FileDirectoryStructureQueryService.class).to(FileDirectoryStructureQueryServiceImpl.class);
        bind(FileModificationDataHolderJsonCompatibilityService.class).to(FileModificationDataHolderJsonCompatibilityServiceImpl.class);
        bind(InquiryViewerMapService.class).to(InquiryViewerMapServiceImpl.class).asEagerSingleton();
        bind(CodactorConnectionService.class).to(CodactorConnectionServiceImpl.class).asEagerSingleton();
        bind(AzureConnectionService.class).to(AzureConnectionServiceImpl.class).asEagerSingleton();
        bind(ProjectSearchService.class).to(ProjectSearchServiceImpl.class);
        bind(PsiFileService.class).to(PsiFileServiceImpl.class);
        bind(PsiElementCollectorService.class).to(PsiElementCollectorServiceImpl.class);
        bind(FindErrorService.class).to(FindErrorServiceImpl.class);
        bind(FindUsagesService.class).to(FindUsagesServiceImpl.class);
        bind(FindImplementationsService.class).to(FindImplementationsServiceImpl.class);
        bind(EditorService.class).to(EditorServiceImpl.class);
        bind(ModificationTypeComboBoxService.class).to(ModificationTypeComboBoxServiceImpl.class).asEagerSingleton();
    }

    @Singleton
    @Provides
    public Project project() {
        return project;
    }

    @Singleton
    @Provides
    public FileModificationManagementService getFileModificationTrackerService(Project project,
                                                                               CodeHighlighterService codeHighlighterService,
                                                                               CodeSnippetExtractorService codeSnippetExtractorService,
                                                                               CodeRangeTrackerService codeRangeTrackerService,
                                                                               GuardedBlockService guardedBlockService,
                                                                               RangeReplaceService rangeReplaceService,
                                                                               EditorClickHandlerService editorClickHandlerService,
                                                                               RenameFileService renameFileService,
                                                                               BackgroundTaskMapperService backgroundTaskMapperService,
                                                                               DiffEditorGeneratorService diffEditorGeneratorService,
                                                                               FileCreatorService fileCreatorService,
                                                                               FileRemoverService fileRemoverService) {
        return new FileModificationManagementServiceImpl(project, codeHighlighterService, codeSnippetExtractorService, codeRangeTrackerService, guardedBlockService, rangeReplaceService, editorClickHandlerService, renameFileService, backgroundTaskMapperService, diffEditorGeneratorService, fileCreatorService, fileRemoverService);
    }

    @Singleton
    @Provides
    public MassCodeFileGeneratorService getCodeFileGeneratorService(Project project,
                                                                    InquiryDao inquiryDao,
                                                                    CodeModificationDao codeModificationDao,
                                                                    FileModificationManagementService fileModificationManagementService,
                                                                    DefaultConnectionService defaultConnectionService,
                                                                    OpenAiModelService openAiModelService,
                                                                    FileCreatorService fileCreatorService,
                                                                    InquirySystemMessageGeneratorService inquirySystemMessageGeneratorService,
                                                                    AzureConnectionService azureConnectionService) {
        return new MassCodeFileGeneratorServiceImpl(project, inquiryDao, codeModificationDao, fileModificationManagementService, defaultConnectionService, openAiModelService, fileCreatorService, inquirySystemMessageGeneratorService, azureConnectionService);
    }

    @Singleton
    @Provides
    public MultiFileAiModificationService multiFileModificationService(Project project,
                                                                       InquiryDao inquiryDao,
                                                                       CodeModificationDao codeModificationDao,
                                                                       FileModificationManagementService fileModificationManagementService,
                                                                       AiFileModificationRestarterService aiFileModificationRestarterService,
                                                                       CodeSnippetExtractorService codeSnippetExtractorService,
                                                                       DefaultConnectionService defaultConnectionService,
                                                                       OpenAiModelService openAiModelService,
                                                                       InquirySystemMessageGeneratorService inquirySystemMessageGeneratorService,
                                                                       AzureConnectionService azureConnectionService,
                                                                       FileModificationErrorDialogFactory fileModificationErrorDialogFactory,
                                                                       Gson gson) {
        return new MultiFileAiModificationServiceImpl(project, inquiryDao, codeModificationDao, fileModificationManagementService, aiFileModificationRestarterService, codeSnippetExtractorService, defaultConnectionService, openAiModelService, inquirySystemMessageGeneratorService, azureConnectionService, fileModificationErrorDialogFactory, gson);
    }
}
