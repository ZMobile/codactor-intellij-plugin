package com.translator.service.codactor.ide.editor.diff;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

import java.util.Arrays;
import java.util.List;

public class GitDiffStingGeneratorServiceImpl implements GitDiffStingGeneratorService {
    public String createDiffString(String beforeCode, String afterCode) {
        List<String> beforeLines = Arrays.asList(beforeCode.split("\n"));
        List<String> afterLines = Arrays.asList(afterCode.split("\n"));

        Patch<String> patch = DiffUtils.diff(beforeLines, afterLines);
        List<AbstractDelta<String>> deltas = patch.getDeltas();

        StringBuilder diffText = new StringBuilder();
        int beforePosition = 0;
        for (AbstractDelta<String> delta : deltas) {
            // Copy unchanged lines before this delta
            while (beforePosition < delta.getSource().getPosition()) {
                diffText.append(" ").append(beforeLines.get(beforePosition)).append("\n");
                beforePosition++;
            }

            switch(delta.getType()) {
                case DELETE:
                    for (String line : delta.getSource().getLines()) {
                        diffText.append("-").append(line).append("\n");
                    }
                    beforePosition += delta.getSource().getLines().size();
                    break;
                case INSERT:
                    for (String line : delta.getTarget().getLines()) {
                        diffText.append("+").append(line).append("\n");
                    }
                    break;
                case CHANGE:
                    for (String line : delta.getSource().getLines()) {
                        diffText.append("-").append(line).append("\n");
                    }
                    beforePosition += delta.getSource().getLines().size();
                    for (String line : delta.getTarget().getLines()) {
                        diffText.append("+").append(line).append("\n");
                    }
                    break;
            }
        }

        // Copy remaining unchanged lines
        while (beforePosition < beforeLines.size()) {
            diffText.append(" ").append(beforeLines.get(beforePosition)).append("\n");
            beforePosition++;
        }

        return diffText.toString();
    }
}
