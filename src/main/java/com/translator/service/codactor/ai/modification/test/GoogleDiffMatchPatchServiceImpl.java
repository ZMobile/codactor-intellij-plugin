package com.translator.service.codactor.ai.modification.test;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import com.translator.model.codactor.ai.modification.test.OmissionMarker;
import com.translator.model.codactor.ai.modification.test.Range;
import com.translator.model.codactor.ai.modification.test.Token;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation.*;

public class GoogleDiffMatchPatchServiceImpl implements GoogleDiffMatchPatchService {
    private List<Token> tokenize(String code) {
        List<Token> tokens = new ArrayList<>();
        int position = 0;
        Matcher m = Pattern.compile("(\\w+|\\s+|[\\p{Punct}])|(\\R)").matcher(code);
        while (m.find()) {
            String tokenValue = m.group(0); // Get the whole matched token
            if (m.group(1) != null) {
                tokens.add(new Token(tokenValue, position));
            } else if (m.group(2) != null) {
                tokens.add(new Token(tokenValue, position));
            }
            position += tokenValue.length();  // Update position to the next character after the current token
        }
        return tokens;
    }

    public String reconstructCodeWithGoogle(String originalCode, String modifiedCode) {
        DiffMatchPatch dmp = new DiffMatchPatch();

        // Compute the difference.
        LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(originalCode, modifiedCode);

        adjustDiffs(originalCode, findOmissionMarkers(modifiedCode, diffs), diffs);

        return restoreCodeBasedOnDeltas(diffs);
    }

    private String restoreCodeBasedOnDeltas(LinkedList<DiffMatchPatch.Diff> diffs) {
        StringBuilder restoredCode = new StringBuilder();
        int lastPos = 0;

        for (DiffMatchPatch.Diff diff : diffs) {
            if (diff.operation == EQUAL) {
                restoredCode.append(diff.text);
                lastPos += diff.text.length();
            } else if (diff.operation == DELETE) {
                // Check for omissions and decide if this should be restored
                /*if (shouldRestore(currentToken)) {
                    restoredCode.append(diff.text);
                }*/
                lastPos += diff.text.length();
            } else if (diff.operation == INSERT) {
                restoredCode.append(diff.text);
                lastPos += diff.text.length();
            }
        }

        return restoredCode.toString();
    }

    public List<OmissionMarker> findOmissionMarkers(String modifiedCode, LinkedList<DiffMatchPatch.Diff> diffs) {
        List<OmissionMarker> omissionMarkers = new ArrayList<>();
        int modifiedTextPos = 0;

        // First pass: build the complete modified text to easily check lines
        for (int i = 0; i < diffs.size(); i++) {
            DiffMatchPatch.Diff diff = diffs.get(i);
            switch (diff.operation) {
                case EQUAL:
                case INSERT:
                    modifiedTextPos += diff.text.length();
                    if (isOmissionMarker(modifiedCode, modifiedTextPos)) {
                        boolean containedInList = false;
                        int lineNumber = getLineNumberOfText(modifiedCode, modifiedTextPos);
                        for (OmissionMarker marker : omissionMarkers) {
                            if (marker.getLineNumber() == lineNumber) {
                                containedInList = true;
                                break;
                            }
                        }
                        if (!containedInList) {
                            omissionMarkers.add(new OmissionMarker(lineNumber, modifiedTextPos, i));
                        }
                    }
                    break;
                case DELETE:
                    // DELETE does not contribute to the modified text length
                    break;
            }
        }

        return omissionMarkers;
    }

    public List<DiffMatchPatch.Diff> adjustDiffs(String modifiedCode, List<OmissionMarker> omissionMarkers, LinkedList<DiffMatchPatch.Diff> diffs) {
        // First pass: build the complete modified text to easily check lines
        int modifiedTextPos = 0;
        List<DiffMatchPatch.Diff> toReplace = new ArrayList<>();

        for (int i = 0; i < diffs.size(); i++) {
            DiffMatchPatch.Diff diff = diffs.get(i);
            switch (diff.operation) {
                case EQUAL:
                case INSERT:
                    modifiedTextPos += diff.text.length();
                    if (isOmissionMarker(i, omissionMarkers)) {
                        OmissionMarker omissionMarker = getOmissionMarker(i, omissionMarkers);
                        adjustPrecedingDeltas(modifiedCode, diffs, omissionMarker, i, modifiedTextPos, toReplace);
                        adjustFollowingDeltas(modifiedCode, diffs, omissionMarker, i, modifiedTextPos, toReplace);
                        toReplace.add(diff);
                    }
                    break;
                case DELETE:
                    // DELETE does not contribute to the modified text length
                    break;
            }
        }

        for (DiffMatchPatch.Diff diff : toReplace) {
            // Convert it from DELETE to EQUAL
            diff.operation = EQUAL;
        }

        return diffs;
    }

    private void adjustPrecedingDeltas(/*List<Range> topStopBeforeRanges, List<Range> topStopModifiedRanges, String beforeCode, */String modifiedCode, List<DiffMatchPatch.Diff> diffs, OmissionMarker omissionMarker, int deltaIndex, int stringIndex, List<DiffMatchPatch.Diff> toReplace) {
        int j = deltaIndex - 1;
        while (j >= 0) {
            DiffMatchPatch.Diff currentDelta = diffs.get(j);
            if (currentDelta.operation == INSERT || currentDelta.operation == EQUAL) {
                if (getLineNumberOfText(modifiedCode, stringIndex) != getLineNumberOfText(modifiedCode, omissionMarker.getStringIndex())) {
                    System.out.println("Going back stopped by delta: " + currentDelta.text);
                    System.out.println("Following delta type: " + currentDelta.operation);
                    //System.out.println("Line at delta: " + getLineOfCodeAtDelta(modifiedCode, currentDelta.));
                    //int modifiedStartIndex = getStartIndexOfLineAtIndex(modifiedCode, modifiedToken.getPosition();
                    //int modifiedEndIndex = getEndIndexOfLineAtIndex(modifiedCode, modifiedToken.getPosition();
                    //Range modifiedRange = new Range(modifiedToken.getPosition(, modifiedToken.getPosition( + modifiedToken.getValue().length());
                    //topStopModifiedRanges.add(modifiedRange);
                    //int beforeStartIndex = getStartIndexOfLineAtIndex(beforeCode, beforeToken.getPosition();
                    //int beforeEndIndex = getEndIndexOfLineAtIndex(beforeCode, beforeToken.getPosition();
                    //Range beforeRange = new Range(beforeToken.getPosition(, beforeToken.getPosition( + beforeToken.getValue().length());
                    //topStopBeforeRanges.add(beforeRange);
                    break;
                } else {
                    if (getLineNumberOfText(modifiedCode, stringIndex) == getLineNumberOfText(modifiedCode, omissionMarker.getStringIndex())) {
                        toReplace.add(currentDelta);
                    }
                    //System.out.println("Both deltas did not cancel because they are on line: " + getLineNumberOfText(modifiedCode, modifiedToken.getPosition());
                }
            }
            if (currentDelta.operation == DELETE) {
                toReplace.add(currentDelta);
            }
            j--;
        }
    }

    private void adjustFollowingDeltas(/*String beforeCode, */String modifiedCode, List<DiffMatchPatch.Diff> diffs, OmissionMarker omissionMarker, int deltaIndex, int stringIndex, List<DiffMatchPatch.Diff> toReplace) {
        int j = deltaIndex + 1;
        while (j < diffs.size()) {
            DiffMatchPatch.Diff currentDelta = diffs.get(j);
            if (currentDelta.operation == INSERT || currentDelta.operation == EQUAL) {
                if (getLineNumberOfText(modifiedCode, stringIndex) != getLineNumberOfText(modifiedCode, omissionMarker.getStringIndex())) {
                    System.out.println("Going forward stopped by delta: " + currentDelta.text);
                    System.out.println("Following delta type: " + currentDelta.operation);
                    //System.out.println("Line at delta: " + getLineOfCodeAtDelta(modifiedCode, modifiedTokens, currentDelta));
                    /*int modifiedStartIndex = getStartIndexOfLineAtIndex(modifiedCode, modifiedToken.getPosition();
                    int modifiedEndIndex = getEndIndexOfLineAtIndex(modifiedCode, modifiedToken.getPosition();
                    Range modifiedRange = new Range(modifiedToken.getPosition(, modifiedToken.getPosition( + modifiedToken.getValue().length());
                    bottomStopModifiedRanges.add(modifiedRange);
                    int beforeStartIndex = getStartIndexOfLineAtIndex(beforeCode, beforeToken.getPosition();
                    int beforeEndIndex = getEndIndexOfLineAtIndex(beforeCode, beforeToken.getPosition();
                    Range beforeRange = new Range(beforeToken.getPosition(, beforeToken.getPosition( + beforeToken.getValue().length());
                    bottomStopBeforeRanges.add(beforeRange);*/
                    break;
                } else {
                    if (getLineNumberOfText(modifiedCode, stringIndex) == getLineNumberOfText(modifiedCode, omissionMarker.getStringIndex())) {
                        toReplace.add(currentDelta);
                    }
                    //System.out.println("Both deltas did not cancel because they are on line: " + getLineNumberOfText(modifiedCode, modifiedToken.getPosition());
                }
            }
            if (currentDelta.operation == DELETE) {
                toReplace.add(currentDelta);
            }
            j++;
        }
    }

    public boolean isOmissionMarker(int index, List<OmissionMarker> omissionMarkers) {
        for (OmissionMarker marker : omissionMarkers) {
            if (marker.getDiffIndex() == index) {
                return true;
            }
        }
        return false;
    }

    public OmissionMarker getOmissionMarker(int index, List<OmissionMarker> omissionMarkers) {
        for (OmissionMarker marker : omissionMarkers) {
            if (marker.getDiffIndex() == index) {
                return marker;
            }
        }
        return null;
    }

    private boolean shouldRestore(Token token) {
        return token.getValue().contains("OMITTED"); // Change this condition based on how omission flags are defined in your code
    }

    private Token findTokenAtPosition(List<Token> tokens, int position) {
        for (Token token : tokens) {
            if (token.getPosition() == position) {
                return token;
            }
        }
        return null;
    }



    public String reconstructModifiedCode(String originalCode, String modifiedCode) {
        List<Token> originalTokens = tokenize(originalCode);
        List<String> originalTokensValues = new ArrayList<>();
        for (Token token : originalTokens) {
            originalTokensValues.add(token.getValue());
        }
        List<Token> modifiedTokens = tokenize(modifiedCode);
        List<String> modifiedTokensValues = new ArrayList<>();
        for (Token token : modifiedTokens) {
            modifiedTokensValues.add(token.getValue());
        }
        Patch<String> patch = DiffUtils.diff(originalTokensValues, modifiedTokensValues);
        return reconstructModifiedCodeWithDeltas(originalTokens, patch.getDeltas());
    }

    private void appendWithIndentation(StringBuilder sb, Token token) {
        sb.append(" ".repeat(Math.max(0, token.getIntentLevel()))); // Apply indentation
        sb.append(token.getValue());
    }

    // Directly reconstruct the modified code from tokens, handling insertions and deletions
    public String reconstructModifiedCodeWithDeltas(List<Token> originalTokens, List<AbstractDelta<String>> deltas) {
        StringBuilder modifiedCode = new StringBuilder();
        int indexOrig = 0;

        for (var delta : deltas) {
            // Append unchanged tokens up to the current delta position
            while (indexOrig < delta.getSource().getPosition()) {
                modifiedCode.append(originalTokens.get(indexOrig++));
            }

            // Handle different types of deltas
            switch (delta.getType()) {
                case DELETE:
                    // Skip appending anything for DELETE types
                    indexOrig += delta.getSource().getLines().size();
                    break;
                case EQUAL:
                    // Append original tokens for EQUAL types
                    for (String line : delta.getSource().getLines()) {
                        modifiedCode.append(line);
                        indexOrig++;
                    }
                    break;
                case INSERT:
                    // Append modified tokens for INSERT types
                    delta.getTarget().getLines().forEach(modifiedCode::append);
                    break;
                case CHANGE:
                    // Append modified tokens for CHANGE types
                    delta.getTarget().getLines().forEach(modifiedCode::append);
                    indexOrig += delta.getSource().getLines().size();
                    break;
                default:
                    // Optionally handle other cases, or throw an error if unexpected
                    throw new IllegalStateException("Unexpected delta type: " + delta.getType());
            }
        }

        // Append the rest of the unchanged tokens
        while (indexOrig < originalTokens.size()) {
            modifiedCode.append(originalTokens.get(indexOrig++));
        }

        return modifiedCode.toString();
    }


    public String reconstructModifiedCodeWithRestoration(List<Range> topStopBeforeRanges, List<Range> topStopModifiedRanges, List<Range> bottomStopBeforeRanges, List<Range> bottomStopModifiedRanges, String originalCode, String modifiedCode) {
        List<Token> originalTokens = tokenize(originalCode);
        List<Token> modifiedTokens = tokenize(modifiedCode);

        return reconstructModifiedCodeWithRestoration(topStopBeforeRanges, topStopModifiedRanges, bottomStopBeforeRanges, bottomStopModifiedRanges, originalCode, modifiedCode, originalTokens, modifiedTokens);
    }

    public String reconstructModifiedCodeWithRestoration(List<Range> topBeforeStopRanges, List<Range> topModifiedStopRanges, List<Range> bottomBeforeStopRanges, List<Range> bottomModifiedStopRanges, String beforeCode, String modifiedCode, List<Token> originalTokens, List<Token> modifiedTokens) {
        List<String> originalTokensValues = new ArrayList<>();
        for (Token token : originalTokens) {
            originalTokensValues.add(token.getValue());
        }
        List<String> modifiedTokensValues = new ArrayList<>();
        for (Token token : modifiedTokens) {
            modifiedTokensValues.add(token.getValue());
        }
        Patch<String> patch = DiffUtils.diff(originalTokensValues, modifiedTokensValues);
        List<AbstractDelta<String>> deltas = new ArrayList<>(patch.getDeltas());

        adjustDeltasForOmissions(topBeforeStopRanges, topModifiedStopRanges, bottomBeforeStopRanges, bottomModifiedStopRanges, beforeCode, modifiedCode, originalTokens, modifiedTokens, deltas);

        return reconstructModifiedCodeWithDeltas(originalTokens, deltas);
    }




    // Restore omitted tokens in the diff string:
    private void adjustDeltasForOmissions(List<Range> topStopBeforeRanges, List<Range> topStopModifiedRanges, List<Range> bottomStopBeforeRanges, List<Range> bottomStopModifiedRanges, String beforeCode, String modifiedCode, List<Token> beforeTokens, List<Token> modifiedTokens, List<AbstractDelta<String>> deltas) {
        List<AbstractDelta<String>> toRemove = new ArrayList<>();
        deltas.sort(Comparator.comparingInt(delta -> delta.getSource().getPosition()));

        for (int i = 0; i < deltas.size(); i++) {
            AbstractDelta<String> delta = deltas.get(i);
            if (isOmissionMarker(modifiedCode, modifiedTokens, delta) && !toRemove.contains(delta)) {
                System.out.println("Omission found: " + delta.getSource().getLines());
                adjustPrecedingDeltas(topStopBeforeRanges, topStopModifiedRanges, beforeCode, modifiedCode, beforeTokens, modifiedTokens, deltas, delta, i, toRemove);
                adjustFollowingDeltas(bottomStopBeforeRanges, bottomStopModifiedRanges, beforeCode, modifiedCode, beforeTokens, modifiedTokens, deltas, delta, i, toRemove);
            }
        }
        deltas.removeAll(toRemove);
    }

    private void adjustPrecedingDeltas(List<Range> topStopBeforeRanges, List<Range> topStopModifiedRanges, String beforeCode, String modifiedCode, List<Token> beforeTokens, List<Token> modifiedTokens, List<AbstractDelta<String>> deltas, AbstractDelta<String> omissionDelta, int index, List<AbstractDelta<String>> toRemove) {
        Token omissionDeltaToken = modifiedTokens.get(omissionDelta.getTarget().getPosition());
        int j = index - 1;
        while (j >= 0) {
            AbstractDelta<String> currentDelta = deltas.get(j);
            if (currentDelta.getType() == DeltaType.INSERT || currentDelta.getType() == DeltaType.EQUAL) {
                Token beforeToken = beforeTokens.get(currentDelta.getSource().getPosition());
                Token modifiedToken = modifiedTokens.get(currentDelta.getTarget().getPosition());
                if (getLineNumberOfText(modifiedCode, modifiedToken.getPosition()) != getLineNumberOfText(modifiedCode, omissionDeltaToken.getPosition())) {
                    System.out.println("Going back stopped by delta: " + currentDelta.getSource().getLines());
                    System.out.println("Following delta type: " + currentDelta.getType());
                    System.out.println("Line at delta: " + getLineOfCodeAtDelta(modifiedCode, modifiedTokens, currentDelta));
                    int modifiedStartIndex = getStartIndexOfLineAtIndex(modifiedCode, modifiedToken.getPosition());
                    int modifiedEndIndex = getEndIndexOfLineAtIndex(modifiedCode, modifiedToken.getPosition());
                    Range modifiedRange = new Range(modifiedToken.getPosition(), modifiedToken.getPosition() + modifiedToken.getValue().length());
                    topStopModifiedRanges.add(modifiedRange);
                    int beforeStartIndex = getStartIndexOfLineAtIndex(beforeCode, beforeToken.getPosition());
                    int beforeEndIndex = getEndIndexOfLineAtIndex(beforeCode, beforeToken.getPosition());
                    Range beforeRange = new Range(beforeToken.getPosition(), beforeToken.getPosition() + beforeToken.getValue().length());
                    topStopBeforeRanges.add(beforeRange);
                    break;
                } else {
                    if (getLineNumberOfText(modifiedCode, modifiedToken.getPosition()) == getLineNumberOfText(modifiedCode, omissionDeltaToken.getPosition())) {
                        toRemove.add(currentDelta);
                    }
                    System.out.println("Both deltas did not cancel because they are on line: " + getLineNumberOfText(modifiedCode, modifiedToken.getPosition()));
                }
            }
            if (currentDelta.getType() == DeltaType.DELETE || currentDelta.getType() == DeltaType.CHANGE) {
                toRemove.add(currentDelta);
            }
            j--;
        }
    }

    private void adjustFollowingDeltas(List<Range> bottomStopBeforeRanges, List<Range> bottomStopModifiedRanges, String beforeCode, String modifiedCode, List<Token> beforeTokens, List<Token> modifiedTokens, List<AbstractDelta<String>> deltas, AbstractDelta<String> omissionDelta, int index, List<AbstractDelta<String>> toRemove) {
        Token omissionDeltaToken = modifiedTokens.get(omissionDelta.getTarget().getPosition());
        int j = index + 1;
        while (j < deltas.size()) {
            AbstractDelta<String> currentDelta = deltas.get(j);
            if (currentDelta.getType() == DeltaType.INSERT || currentDelta.getType() == DeltaType.EQUAL) {
                Token beforeToken = beforeTokens.get(currentDelta.getSource().getPosition());
                Token modifiedToken = modifiedTokens.get(currentDelta.getTarget().getPosition());
                if (getLineNumberOfText(modifiedCode, modifiedToken.getPosition()) != getLineNumberOfText(modifiedCode, omissionDeltaToken.getPosition())) {
                    System.out.println("Going forward Stopped by delta: " + currentDelta.getSource().getLines());
                    System.out.println("Following delta type: " + currentDelta.getType());
                    System.out.println("Line at delta: " + getLineOfCodeAtDelta(modifiedCode, modifiedTokens, currentDelta));
                    int modifiedStartIndex = getStartIndexOfLineAtIndex(modifiedCode, modifiedToken.getPosition());
                    int modifiedEndIndex = getEndIndexOfLineAtIndex(modifiedCode, modifiedToken.getPosition());
                    Range modifiedRange = new Range(modifiedToken.getPosition(), modifiedToken.getPosition() + modifiedToken.getValue().length());
                    bottomStopModifiedRanges.add(modifiedRange);
                    int beforeStartIndex = getStartIndexOfLineAtIndex(beforeCode, beforeToken.getPosition());
                    int beforeEndIndex = getEndIndexOfLineAtIndex(beforeCode, beforeToken.getPosition());
                    Range beforeRange = new Range(beforeToken.getPosition(), beforeToken.getPosition() + beforeToken.getValue().length());
                    bottomStopBeforeRanges.add(beforeRange);
                    break;
                } else {
                    if (getLineNumberOfText(modifiedCode, modifiedToken.getPosition()) == getLineNumberOfText(modifiedCode, omissionDeltaToken.getPosition())) {
                        toRemove.add(currentDelta);
                    }
                    System.out.println("Both deltas did not cancel because they are on line: " + getLineNumberOfText(modifiedCode, modifiedToken.getPosition()));
                }
            }
            if (currentDelta.getType() == DeltaType.DELETE || currentDelta.getType() == DeltaType.CHANGE) {
                toRemove.add(currentDelta);
            }
            j++;
        }
    }

    private String getLineOfCodeAtDelta(String modifiedCode, List<Token> modifiedTokens, AbstractDelta<String> delta) {
        Token token = modifiedTokens.get(delta.getTarget().getPosition());
        return getLineOfCodeAtIndex(modifiedCode, token.getPosition());
    }

    private boolean isOmissionMarker(String modifiedCode, List<Token> modifiedTokens, AbstractDelta<String> delta) {
        if ((delta.getType() == DeltaType.INSERT || delta.getType() == DeltaType.CHANGE)) {
            String line = getLineOfCodeAtDelta(modifiedCode, modifiedTokens, delta);
            if (((line.toLowerCase().contains("omitted")
                    || line.toLowerCase().contains("not shown"))
                    && line.toLowerCase().contains("brevity"))
                    || (line.toLowerCase().contains("// other")
                    || line.toLowerCase().contains("# other")
                    || line.toLowerCase().contains("// ..."))) {
                System.out.println("Omission line found: " + line);
                System.out.println("Delta type: " + delta.getType());
                System.out.println("Delta: " + delta.getTarget().getLines());
                return true;
            }
        }
        return false;
    }

    private boolean isOmissionMarker(String modifiedCode, int modifiedTextPos) {
        String line = getLineOfCodeAtIndex(modifiedCode, modifiedTextPos);
        return ((line.toLowerCase().contains("omitted")
                || line.toLowerCase().contains("not shown"))
                && line.toLowerCase().contains("brevity"))
                || (line.toLowerCase().contains("// other")
                || line.toLowerCase().contains("# other")
                || line.toLowerCase().contains("// ..."));
    }


    public int getLineNumberOfText(String code, int index) {
        int line = 1;
        for (int i = 0; i < index; i++) {
            if (code.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

    public String getLineOfCodeAtIndex(String code, int index) {
        // Ensure the index is within the valid range of the code string.
        if (index >= code.length()) {
            return "";  // Return an empty string or handle this case as needed.
        }

        int start = index;
        int end = index;

        // Adjust the start index to find the beginning of the line.
        while (start > 0 && code.charAt(start - 1) != '\n') {
            start--;
        }

        // Adjust the end index to find the end of the line.
        while (end < code.length() - 1 && code.charAt(end + 1) != '\n') {
            end++;
        }

        // Return the substring from start to end (inclusive of end).
        return code.substring(start, end + 1);
    }

    public int getStartIndexOfLineAtIndex(String code, int index) {
        int start = index;
        while (start > 0 && code.charAt(start) != '\n') {
            start--;
        }
        return start;
    }

    public int getEndIndexOfLineAtIndex(String code, int index) {
        int end = index;
        while (end < code.length() && code.charAt(end) != '\n') {
            end++;
        }
        return end;
    }

}
