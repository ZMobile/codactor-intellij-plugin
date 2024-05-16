package com.translator.service.codactor.test;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DiffStringServiceImpl implements DiffStringService {
    public String getDiffString(String beforeCode, String afterCode) {
        List<String> beforeLines = Arrays.asList(beforeCode.split("\n"));
        List<String> afterLines = Arrays.asList(afterCode.split("\n"));

        Patch<String> patch = DiffUtils.diff(beforeLines, afterLines);
        List<AbstractDelta<String>> deltas = patch.getDeltas();

        StringBuilder merged = new StringBuilder();
        List<Pair<Integer, String>> changes = new ArrayList<>();
        int beforePosition = 0;
        int afterPosition = 0;

        for (AbstractDelta<String> delta : deltas) {

            while (beforePosition < delta.getSource().getPosition()) {
                merged.append("-[=]-" + beforeLines.get(beforePosition)).append("\n");
                beforePosition++;
                afterPosition++;
            }

            switch (delta.getType()) {
                case DELETE:
                    for (String line : delta.getSource().getLines()) {
                        merged.append("-[-]-" + line).append("\n");
                        changes.add(new Pair<>(afterPosition, "-[-]-"));
                        afterPosition++;
                    }
                    beforePosition += delta.getSource().getLines().size();
                    break;
                case INSERT:
                    for (String line : delta.getTarget().getLines()) {
                        merged.append("-[+]-" + line).append("\n");
                        changes.add(new Pair<>(afterPosition, "-[+]-"));
                        afterPosition++;
                    }
                    break;
                case CHANGE:
                    for (String line : delta.getSource().getLines()) {
                        merged.append("-[-]-" + line).append("\n");
                        changes.add(new Pair<>(afterPosition, "-[-]-"));
                        afterPosition++;
                    }
                    beforePosition += delta.getSource().getLines().size();
                    for (String line : delta.getTarget().getLines()) {
                        merged.append("-[+]-" + line).append("\n");
                        changes.add(new Pair<>(afterPosition, "-[+]-"));
                        afterPosition++;
                    }
                    break;
            }
        }

        while (beforePosition < beforeLines.size()) {
            merged.append("-[=]-" + beforeLines.get(beforePosition)).append("\n");
            beforePosition++;
            afterPosition++;
        }
        return merged.toString();
    }

    public String postProcessDiffString(String diffString) {
        System.out.println("This gets called 1");
        String[] lines = diffString.split("\n");

        for (int i = lines.length - 1; i >= 0; i--) {
            String trimmedLine = lines[i].trim();

            if (trimmedLine.startsWith("-[=]-") && (trimmedLine.endsWith("}") || trimmedLine.endsWith("};") || trimmedLine.endsWith("});"))) {
                System.out.println("This gets called");
                String closingBracketLine = lines[i];
                String matchCheckerLine = closingBracketLine.replace("-[=]-", "-[-]-");
                String closingBracketContent = collectAllPositiveTextAboveLine(i, lines);
                Integer replacementLineIndex = null;
                System.out.println("Closing bracket content: " + closingBracketContent);
                for (int j = i + 1; j < lines.length; j++) {
                    System.out.println("Match checker line: " + matchCheckerLine);
                    System.out.println("This line: " + lines[j]);
                    String thisTrimmedLine = lines[j].trim();
                    String trimmedLineWithoutTags = thisTrimmedLine.replace("-[=]-", "").replace("-[-]-", "").replace("-[+]-", "").trim();
                    if (lines[j].startsWith("-[=]-") && !trimmedLineWithoutTags.isEmpty()) {
                        System.out.println("BREAKING for line: " + j);
                        if (replacementLineIndex != null) {
                            lines[replacementLineIndex] = closingBracketContent;
                            lines[i] = lines[i].replace("-[=]-", "-[-]-");
                            lines = removeAllPositiveTextAboveLine(i, lines);
                        }
                        break;
                    }
                    if (thisTrimmedLine.equals(matchCheckerLine)) {
                        System.out.println("This gets called wohoo!");
                        System.out.println("For : " + closingBracketContent);
                        replacementLineIndex = j;
                    }
                }
            }
        }

        return String.join("\n", lines);
    }

    private String collectAllPositiveTextAboveLine(int lineIndex, String[] lines) {
        Stack<String> replacementContentLines = new Stack<>();
        replacementContentLines.add(lines[lineIndex]);
        for (int i = lineIndex - 1; i >= 0; i--) {
            String trimmedLineAbove = lines[i].trim();
            if (!trimmedLineAbove.startsWith("-[+]-")) {
                System.out.println("This gets called 2: + line: " + lines[i]);
                break;
            }
            replacementContentLines.add(lines[i]);
        }
        StringBuilder replacementContentBuilder = new StringBuilder();
        while (!replacementContentLines.isEmpty()) {
            replacementContentBuilder.append(replacementContentLines.pop()).append("\n");
        }
        return replacementContentBuilder.toString();
    }

    private String[] removeAllPositiveTextAboveLine(int lineIndex, String[] lines) {
        List<String> list = new ArrayList<>(Arrays.asList(lines));

        for (int i = lineIndex - 1; i >= 0; i--) {
            String trimmedLineAbove = list.get(i).trim();
            if (!trimmedLineAbove.startsWith("-[+]-")) {
                break;
            }
            list.remove(i);
        }
        // Convert list back to array.

        // Your 'Done' comment.

        return list.toArray(new String[list.size()]);
    }

    public String replaceConsideringDiffMarkers(String original, String oldSequence, String newSequence, DiffType diffType) {
        System.out.println("&&New sequence:" + newSequence);
        String codeWithoutDiff = original.replace("-[-]-", "")
                .replace("-[=]-", "")
                .replace("-[+]-", "");

        int startIndex = codeWithoutDiff.indexOf(oldSequence);
        int endIndex = startIndex + oldSequence.length();
        int lineOfStartIndex = getLineOfIndex(codeWithoutDiff, startIndex);
        int lineOfEndIndex = getLineOfIndex(codeWithoutDiff, endIndex);

        String[] newSequenceLines = newSequence.split("\n");
        String[] lines = original.split("\n");
        List<String> linesAsList = new ArrayList<>(Arrays.asList(lines));
        //Replace lines from line of start index to line of end index, make sure to add diff markers to the beginning of the lines depending on DiffType
        for (int i = lineOfEndIndex; i >= lineOfStartIndex; i--) {
            linesAsList.remove(i);
        }
        for (int i = lineOfStartIndex; i < lineOfStartIndex + newSequenceLines.length; i++) {
            String line = newSequenceLines[i - lineOfStartIndex];
            String diffTypeString;
            if (diffType == DiffType.ADD) {
                diffTypeString = "-[+]-";
            } else if (diffType == DiffType.REMOVE) {
                diffTypeString = "-[-]-";
            } else {
                diffTypeString = "-[=]-";
            }
            linesAsList.add(i, diffTypeString + line);
            System.out.println("&&New code addition: " + diffTypeString + line);
        }
        System.out.println("&&New code: " + String.join("\n", linesAsList));
        return String.join("\n", linesAsList);
    }

    public String addMethodToEndOfClass(String diffString, String methodString, DiffType diffType) {
        System.out.println("&&Method added to class: " + methodString);
        int lastBraceIndex = diffString.lastIndexOf("}");
        if (lastBraceIndex == -1) {
            throw new IllegalArgumentException("Invalid class string: No closing brace found.");
        }
        int lineOfLastBrace = getLineOfIndex(diffString, lastBraceIndex);
        String[] lines = diffString.split("\n");
        List<String> linesAsList = new ArrayList<>(Arrays.asList(lines));
        String[] methodLines = methodString.split("\n");
        List<String> methodLinesAsList = new ArrayList<>(Arrays.asList(methodLines));
        methodLinesAsList.add(0, "");
        for (int i = 0; i < methodLinesAsList.size(); i++) {
            String line = methodLinesAsList.get(i);
            String diffTypeString;
            if (diffType == DiffType.ADD) {
                diffTypeString = "-[+]-";
            } else if (diffType == DiffType.REMOVE) {
                diffTypeString = "-[-]-";
            } else {
                diffTypeString = "-[=]-";
            }
            linesAsList.add(lineOfLastBrace + i, diffTypeString + line);
        }
        return String.join("\n", linesAsList);
    }

    private int getLineOfIndex(String code, int index) {
        String[] lines = code.split("\n");
        int lineCount = 0;
        int charCount = 0;
        for (String line : lines) {
            if (charCount + line.length() >= index) {
                return lineCount;
            }
            charCount += line.length() + 1;
            lineCount++;
        }
        return -1;
    }
}
