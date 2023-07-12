package com.translator.service.codactor.directory;

import com.google.gson.Gson;
import com.google.inject.Inject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileDirectoryStructureQueryServiceImpl implements FileDirectoryStructureQueryService {
    private final Gson gson;

    @Inject
    public FileDirectoryStructureQueryServiceImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String getDirectoryStructureAsJson(String filePath, int depth) {
        File directory = new File(filePath);
        Map<String, Object> structure = generateDirectoryStructure(directory, depth);
        return gson.toJson(structure);
    }

    private Map<String, Object> generateDirectoryStructure(File file, int depth) {
        Map<String, Object> structure = new HashMap<>();
        if (file.isFile()) {
            structure.put("type", "file");
            structure.put("name", file.getName());
        } else if (file.isDirectory() && depth >= 0) {
            structure.put("type", "directory");
            structure.put("name", file.getName());
            File[] children = file.listFiles();
            if (children != null) {
                ArrayList<Map<String, Object>> childStructures = new ArrayList<>();
                for (File child : children) {
                    childStructures.add(generateDirectoryStructure(child, depth - 1));
                }
                structure.put("children", childStructures);
            }
        }
        return structure;
    }
}
