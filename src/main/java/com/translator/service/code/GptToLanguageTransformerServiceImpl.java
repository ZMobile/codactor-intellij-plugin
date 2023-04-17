package com.translator.service.code;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GptToLanguageTransformerServiceImpl implements GptToLanguageTransformerService {
    private final Map<String, String> languageToExtensionMap;

    public GptToLanguageTransformerServiceImpl() {
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
        languageToExtensionMap.put("unix shell", "sh");
        languageToExtensionMap.put("visual basic", "vb");
        languageToExtensionMap.put("windows batch", "bat");
        languageToExtensionMap.put("xml", "xml");
        languageToExtensionMap.put("yaml", "yaml");
        languageToExtensionMap.put("text", "txt");
    }

    @Override
    public String convert(String text) {
        String[] words = text.split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String doubleWord = null;
            if (i + 1 < words.length) {
                doubleWord = words[i] + " " + words[i + 1];
            }
            if (languageToExtensionMap.containsKey(word.trim().toLowerCase()) || (doubleWord != null && languageToExtensionMap.containsValue(doubleWord.trim().toLowerCase()))) {
                return word.toLowerCase();
            }
        }
        return null;
    }

    @Override
    public String convert(List<String> texts) {
        for (String text : texts) {
            String language = convert(text);
            if (language != null) {
                return language;
            }
        }
        return null;
    }

    @Override
    public String getFromFilePath(String filePath) {
        String extension = filePath.substring(filePath.indexOf(".") + 1);
        for (Map.Entry<String, String> entry : languageToExtensionMap.entrySet()) {
            if (entry.getValue().equals(extension)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public String getExtensionFromLanguage(String language) {
        return languageToExtensionMap.get(language);
    }
}
