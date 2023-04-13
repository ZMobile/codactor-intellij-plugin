package com.translator.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.translator.dao.CodeTranslatorDaoConfig;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.service.constructor.CodeFileGeneratorService;
import com.translator.service.constructor.CodeFileGeneratorServiceImpl;
import com.translator.service.file.*;
import com.translator.service.context.PromptContextService;
import com.translator.service.context.PromptContextServiceImpl;
import com.translator.service.copy.DirectoryCopierService;
import com.translator.service.copy.DirectoryCopierServiceImpl;
import com.translator.service.line.LineCounterService;
import com.translator.service.line.LineCounterServiceImpl;
import com.translator.service.modification.CodeModificationService;
import com.translator.service.modification.CodeModificationServiceImpl;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.modification.tracking.FileModificationTrackerServiceImpl;
import com.translator.service.modification.tracking.highlighter.JBTextAreaHighlighterService;
import com.translator.service.modification.tracking.highlighter.JBTextAreaHighlighterServiceImpl;
import com.translator.service.modification.tracking.listener.*;
import com.translator.service.openai.OpenAiApiKeyService;
import com.translator.service.openai.OpenAiApiKeyServiceImpl;
import com.translator.service.openai.OpenAiModelService;
import com.translator.service.openai.OpenAiModelServiceImpl;
import com.translator.service.search.SearchResultParserService;
import com.translator.service.search.SearchResultParserServiceImpl;
import com.translator.service.ui.*;
import com.translator.service.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.ui.measure.TextAreaHeightCalculatorServiceImpl;
import com.translator.service.ui.tool.CodactorToolWindowService;
import com.translator.service.ui.tool.CodactorToolWindowServiceImpl;
import com.translator.service.ui.tool.ToolWindowService;
import com.translator.service.ui.tool.ToolWindowServiceImpl;
import com.translator.worker.LimitedSwingWorkerExecutor;
import com.intellij.ui.components.JBTextArea;

import java.util.HashMap;
import java.util.Map;

public class CodeTranslatorServiceConfig extends AbstractModule {
    @Override
    protected void configure() {
        install(new CodeTranslatorDaoConfig());
        bind(CodeModificationService.class).to(CodeModificationServiceImpl.class);
        bind(OpenAiApiKeyService.class).to(OpenAiApiKeyServiceImpl.class).asEagerSingleton();
        bind(OpenAiModelService.class).to(OpenAiModelServiceImpl.class).asEagerSingleton();
        bind(FileCreatorService.class).to(FileCreatorServiceImpl.class);
        bind(FileReaderService.class).to(FileReaderServiceImpl.class);
        bind(FileOpenerService.class).to(FileOpenerServiceImpl.class);
        bind(PromptContextService.class).to(PromptContextServiceImpl.class);
        bind(DirectoryCopierService.class).to(DirectoryCopierServiceImpl.class);
        bind(LineCounterService.class).to(LineCounterServiceImpl.class);
        bind(TabKeyListenerService.class).to(TabKeyListenerServiceImpl.class).asEagerSingleton();
        bind(TextAreaHeightCalculatorService.class).to(TextAreaHeightCalculatorServiceImpl.class);
        bind(ModificationQueueListButtonService.class).to(ModificationQueueListButtonServiceImpl.class);
        bind(ToolWindowService.class).to(ToolWindowServiceImpl.class);
        bind(CodactorToolWindowService.class).to(CodactorToolWindowServiceImpl.class).asEagerSingleton();
        bind(SearchResultParserService.class).to(SearchResultParserServiceImpl.class).asEagerSingleton();
        bind(DisplayProjectorService.class).to(DisplayProjectorServiceImpl.class).asEagerSingleton();
    }

    @Singleton
    @Provides
    @Named("displayMap")
    public Map<String, JBTextArea> getDisplayMap() {
        return new HashMap<>();
    }

    @Provides
    @Named("extensionToSyntaxMap")
    public Map<String, String> getExtensionToSyntaxMap() {
        Map<String, String> extensionToSyntaxMap = new HashMap<>();
        /*extensionToSyntaxMap.put("java", SyntaxConstants.SYNTAX_STYLE_JAVA);
        extensionToSyntaxMap.put("c", SyntaxConstants.SYNTAX_STYLE_C);
        extensionToSyntaxMap.put("cpp", SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
        extensionToSyntaxMap.put("cs", SyntaxConstants.SYNTAX_STYLE_CSHARP);
        extensionToSyntaxMap.put("css", SyntaxConstants.SYNTAX_STYLE_CSS);
        extensionToSyntaxMap.put("csv", SyntaxConstants.SYNTAX_STYLE_CSV);
        extensionToSyntaxMap.put("d", SyntaxConstants.SYNTAX_STYLE_D);
        extensionToSyntaxMap.put("dart", SyntaxConstants.SYNTAX_STYLE_DART);
        extensionToSyntaxMap.put("dpr", SyntaxConstants.SYNTAX_STYLE_DELPHI);
        extensionToSyntaxMap.put("dtd", SyntaxConstants.SYNTAX_STYLE_DTD);
        extensionToSyntaxMap.put("for", SyntaxConstants.SYNTAX_STYLE_FORTRAN);
        extensionToSyntaxMap.put("go", SyntaxConstants.SYNTAX_STYLE_GO);
        extensionToSyntaxMap.put("groovy", SyntaxConstants.SYNTAX_STYLE_GROOVY);
        extensionToSyntaxMap.put("html", SyntaxConstants.SYNTAX_STYLE_HTML);
        extensionToSyntaxMap.put("ini", SyntaxConstants.SYNTAX_STYLE_INI);
        extensionToSyntaxMap.put("js", SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        extensionToSyntaxMap.put("json", SyntaxConstants.SYNTAX_STYLE_JSON);
        extensionToSyntaxMap.put("jsp", SyntaxConstants.SYNTAX_STYLE_JSP);
        extensionToSyntaxMap.put("kt", SyntaxConstants.SYNTAX_STYLE_KOTLIN);
        extensionToSyntaxMap.put("tex", SyntaxConstants.SYNTAX_STYLE_LATEX);
        extensionToSyntaxMap.put("less", SyntaxConstants.SYNTAX_STYLE_LESS);
        extensionToSyntaxMap.put("lisp", SyntaxConstants.SYNTAX_STYLE_LISP);
        extensionToSyntaxMap.put("lua", SyntaxConstants.SYNTAX_STYLE_LUA);
        extensionToSyntaxMap.put("makefile", SyntaxConstants.SYNTAX_STYLE_MAKEFILE);
        extensionToSyntaxMap.put("md", SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
        extensionToSyntaxMap.put("mxml", SyntaxConstants.SYNTAX_STYLE_MXML);
        extensionToSyntaxMap.put("nsi", SyntaxConstants.SYNTAX_STYLE_NSIS);
        extensionToSyntaxMap.put("perl", SyntaxConstants.SYNTAX_STYLE_PERL);
        extensionToSyntaxMap.put("php", SyntaxConstants.SYNTAX_STYLE_PHP);
        extensionToSyntaxMap.put("proto", SyntaxConstants.SYNTAX_STYLE_PROTO);
        extensionToSyntaxMap.put("properties", SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
        extensionToSyntaxMap.put("py", SyntaxConstants.SYNTAX_STYLE_PYTHON);
        extensionToSyntaxMap.put("rb", SyntaxConstants.SYNTAX_STYLE_RUBY);
        extensionToSyntaxMap.put("sas", SyntaxConstants.SYNTAX_STYLE_SAS);
        extensionToSyntaxMap.put("scala", SyntaxConstants.SYNTAX_STYLE_SCALA);
        extensionToSyntaxMap.put("sql", SyntaxConstants.SYNTAX_STYLE_SQL);
        extensionToSyntaxMap.put("tcl", SyntaxConstants.SYNTAX_STYLE_TCL);
        extensionToSyntaxMap.put("ts", SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT);
        extensionToSyntaxMap.put("sh", SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL);
        extensionToSyntaxMap.put("vb", SyntaxConstants.SYNTAX_STYLE_VISUAL_BASIC);
        extensionToSyntaxMap.put("bat", SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH);
        extensionToSyntaxMap.put("xml", SyntaxConstants.SYNTAX_STYLE_XML);
        extensionToSyntaxMap.put("yaml", SyntaxConstants.SYNTAX_STYLE_YAML);*/
        return extensionToSyntaxMap;
    }

    @Provides
    @Named("languageToSyntaxMap")
    public Map<String, String> getLanguageToSyntaxMap() {
        Map<String, String> languageToSyntaxMap = new HashMap<>();
        /*languageToSyntaxMap.put("java", SyntaxConstants.SYNTAX_STYLE_JAVA);
        languageToSyntaxMap.put("c", SyntaxConstants.SYNTAX_STYLE_C);
        languageToSyntaxMap.put("c++", SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
        languageToSyntaxMap.put("c#", SyntaxConstants.SYNTAX_STYLE_CSHARP);
        languageToSyntaxMap.put("css", SyntaxConstants.SYNTAX_STYLE_CSS);
        languageToSyntaxMap.put("csv", SyntaxConstants.SYNTAX_STYLE_CSV);
        languageToSyntaxMap.put("d", SyntaxConstants.SYNTAX_STYLE_D);
        languageToSyntaxMap.put("dart", SyntaxConstants.SYNTAX_STYLE_DART);
        languageToSyntaxMap.put("dpr", SyntaxConstants.SYNTAX_STYLE_DELPHI);
        languageToSyntaxMap.put("dtd", SyntaxConstants.SYNTAX_STYLE_DTD);
        languageToSyntaxMap.put("for", SyntaxConstants.SYNTAX_STYLE_FORTRAN);
        languageToSyntaxMap.put("go", SyntaxConstants.SYNTAX_STYLE_GO);
        languageToSyntaxMap.put("groovy", SyntaxConstants.SYNTAX_STYLE_GROOVY);
        languageToSyntaxMap.put("html", SyntaxConstants.SYNTAX_STYLE_HTML);
        languageToSyntaxMap.put("ini", SyntaxConstants.SYNTAX_STYLE_INI);
        languageToSyntaxMap.put("js", SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        languageToSyntaxMap.put("json", SyntaxConstants.SYNTAX_STYLE_JSON);
        languageToSyntaxMap.put("jsp", SyntaxConstants.SYNTAX_STYLE_JSP);
        languageToSyntaxMap.put("kt", SyntaxConstants.SYNTAX_STYLE_KOTLIN);
        languageToSyntaxMap.put("tex", SyntaxConstants.SYNTAX_STYLE_LATEX);
        languageToSyntaxMap.put("less", SyntaxConstants.SYNTAX_STYLE_LESS);
        languageToSyntaxMap.put("lisp", SyntaxConstants.SYNTAX_STYLE_LISP);
        languageToSyntaxMap.put("lua", SyntaxConstants.SYNTAX_STYLE_LUA);
        languageToSyntaxMap.put("makefile", SyntaxConstants.SYNTAX_STYLE_MAKEFILE);
        languageToSyntaxMap.put("md", SyntaxConstants.SYNTAX_STYLE_MARKDOWN);
        languageToSyntaxMap.put("mxml", SyntaxConstants.SYNTAX_STYLE_MXML);
        languageToSyntaxMap.put("nsi", SyntaxConstants.SYNTAX_STYLE_NSIS);
        languageToSyntaxMap.put("perl", SyntaxConstants.SYNTAX_STYLE_PERL);
        languageToSyntaxMap.put("php", SyntaxConstants.SYNTAX_STYLE_PHP);
        languageToSyntaxMap.put("proto", SyntaxConstants.SYNTAX_STYLE_PROTO);
        languageToSyntaxMap.put("properties", SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
        languageToSyntaxMap.put("py", SyntaxConstants.SYNTAX_STYLE_PYTHON);
        languageToSyntaxMap.put("rb", SyntaxConstants.SYNTAX_STYLE_RUBY);
        languageToSyntaxMap.put("sas", SyntaxConstants.SYNTAX_STYLE_SAS);
        languageToSyntaxMap.put("scala", SyntaxConstants.SYNTAX_STYLE_SCALA);
        languageToSyntaxMap.put("sql", SyntaxConstants.SYNTAX_STYLE_SQL);
        languageToSyntaxMap.put("tcl", SyntaxConstants.SYNTAX_STYLE_TCL);
        languageToSyntaxMap.put("ts", SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT);
        languageToSyntaxMap.put("sh", SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL);
        languageToSyntaxMap.put("vb", SyntaxConstants.SYNTAX_STYLE_VISUAL_BASIC);
        languageToSyntaxMap.put("bat", SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH);
        languageToSyntaxMap.put("xml", SyntaxConstants.SYNTAX_STYLE_XML);
        languageToSyntaxMap.put("yaml", SyntaxConstants.SYNTAX_STYLE_YAML);*/
        return languageToSyntaxMap;
    }

    @Singleton
    @Provides
    @Named("aiTaskExecutor")
    public LimitedSwingWorkerExecutor getAiTaskExecutor() {
        return new LimitedSwingWorkerExecutor();
    }

    @Singleton
    @Provides
    @Named("inquiryTaskExecutor")
    public LimitedSwingWorkerExecutor getInquiryTaskExecutor() {
        return new LimitedSwingWorkerExecutor();
    }

    @Singleton
    @Provides
    @Named("historyFetchingTaskExecutor")
    public LimitedSwingWorkerExecutor getHistoryFetchingTaskExecutor() {
        return new LimitedSwingWorkerExecutor();
    }

    @Singleton
    @Provides
    public FileModificationTrackerService getFileModificationTrackerService(@Named("displayMap") Map<String, JBTextArea> displayMap,
                                                                            @Named("extensionToSyntaxMap") Map<String, String> extensionToSyntaxMap,
                                                                            @Named("languageToSyntaxMap") Map<String, String> languageToSyntaxMap,
                                                                            UneditableSegmentListenerService uneditableSegmentListenerService,
                                                                            DocumentListenerService documentListenerService,
                                                                            JBTextAreaHighlighterService jBTextAreaHighlighterService) {
        return new FileModificationTrackerServiceImpl(displayMap, extensionToSyntaxMap, languageToSyntaxMap, uneditableSegmentListenerService, documentListenerService, jBTextAreaHighlighterService);
    }

    @Singleton
    @Provides
    public CodeFileGeneratorService getCodeFileGeneratorService(InquiryDao inquiryDao,
                                                                CodeModificationService codeModificationService,
                                                                FileModificationTrackerService fileModificationTrackerService,
                                                                OpenAiApiKeyService openAiApiKeyService,
                                                                OpenAiModelService openAiModelService,
                                                                FileCreatorService fileCreatorService,
                                                                @Named("inquiryTaskExecutor") LimitedSwingWorkerExecutor inquiryTaskExecutor,
                                                                @Named("aiTaskExecutor") LimitedSwingWorkerExecutor aiTaskExecutor) {
        return new CodeFileGeneratorServiceImpl(inquiryDao, codeModificationService, fileModificationTrackerService, openAiApiKeyService, openAiModelService, fileCreatorService, inquiryTaskExecutor, aiTaskExecutor);
    }

    @Singleton
    @Provides
    public JBTextAreaHighlighterService getJBTextAreaHighlighterService(@Named("displayMap") Map<String, JBTextArea> displayMap) {
        return new JBTextAreaHighlighterServiceImpl(displayMap);
    }

    @Singleton
    @Provides
    public DocumentListenerService documentListenerService(FileModificationTrackerService fileModificationTrackerService,
                                                           @Named("displayMap") Map<String, JBTextArea> displayMap) {
        return new DocumentListenerServiceImpl(fileModificationTrackerService, displayMap);
    }

    @Singleton
    @Provides
    public UneditableSegmentListenerService uneditableSegmentListenerService(FileModificationTrackerService fileModificationTrackerService,
                                                                             @Named("displayMap") Map<String, JBTextArea> displayMap) {
        return new UneditableSegmentListenerServiceImpl(fileModificationTrackerService, displayMap);
    }
}
