package com.translator.service.code;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CodeToFileTypeTransformerServiceImpl implements CodeToFileTypeTransformerService {
    private final Map<String, String> languageToExtensionMap;

    @Inject
    public CodeToFileTypeTransformerServiceImpl() {
        languageToExtensionMap = new HashMap<>();
        languageToExtensionMap.put("java", "java");
        languageToExtensionMap.put("c", "c");
        languageToExtensionMap.put("c++", "cpp");
        languageToExtensionMap.put("c#", "cs");
        languageToExtensionMap.put("css", "css");
        languageToExtensionMap.put("csv", "csv");
        languageToExtensionMap.put("d", "d");
        languageToExtensionMap.put("dart", "dart");
        languageToExtensionMap.put("delphi", "dpr");
        languageToExtensionMap.put("dtd", "dtd");
        languageToExtensionMap.put("fortran", "for");
        languageToExtensionMap.put("go", "go");
        languageToExtensionMap.put("groovy", "groovy");
        languageToExtensionMap.put("html", "html");
        languageToExtensionMap.put("ini", "ini");
        languageToExtensionMap.put("javascript", "js");
        languageToExtensionMap.put("json", "json");
        languageToExtensionMap.put("jsp", "jsp");
        languageToExtensionMap.put("kotlin", "kt");
        languageToExtensionMap.put("latex", "tex");
        languageToExtensionMap.put("less", "less");
        languageToExtensionMap.put("lisp", "lisp");
        languageToExtensionMap.put("lua", "lua");
        languageToExtensionMap.put("makefile", "makefile");
        languageToExtensionMap.put("markdown", "md");
        languageToExtensionMap.put("mxml", "mxml");
        languageToExtensionMap.put("nsis", "nsi");
        languageToExtensionMap.put("perl", "perl");
        languageToExtensionMap.put("php", "php");
        languageToExtensionMap.put("proto", "proto");
        languageToExtensionMap.put("properties", "properties");
        languageToExtensionMap.put("python", "py");
        languageToExtensionMap.put("ruby", "rb");
        languageToExtensionMap.put("sas", "sas");
        languageToExtensionMap.put("scala", "scala");
        languageToExtensionMap.put("sql", "sql");
        languageToExtensionMap.put("tcl", "tcl");
        languageToExtensionMap.put("typescript", "ts");
        languageToExtensionMap.put("unix_shell", "sh");
        languageToExtensionMap.put("visual_basic", "vb");
        languageToExtensionMap.put("windows_batch", "bat");
        languageToExtensionMap.put("xml", "xml");
        languageToExtensionMap.put("yaml", "yaml");
    }

    @Override
    public String detectLanguage(String code) {
    }


    public FileType convert(String code) {
        String language = detectLanguage(code);

        String extension = languageToExtensionMap.get(language);
        if (extension == null) {
            return null;
        }
        return FileTypeManager.getInstance().getFileTypeByExtension(extension);
    }
}
