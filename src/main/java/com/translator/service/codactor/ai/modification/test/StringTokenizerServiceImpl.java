package com.translator.service.codactor.ai.modification.test;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.*;
import com.translator.model.codactor.ai.modification.test.Range;
import com.translator.model.codactor.ai.modification.test.Token;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.translator.model.codactor.ai.modification.test.Token;

public class StringTokenizerServiceImpl implements StringTokenizerService {
    // Improved tokenization that captures whitespace and line breaks

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
    /*private List<Token> tokenize(String code) {
        List<Token> tokens = new ArrayList<>();
        int position = 0;
        Matcher m = Pattern.compile("(\\w+|[\\p{Punct}]|\\s+?|\\R)").matcher(code);
        while (m.find()) {
            String tokenValue = m.group();
            tokens.add(new Token(tokenValue, position));
            position += tokenValue.length();  // Update position to the next character after the current token
        }
        return tokens;
    }*/
    /*private List<Token> tokenize(String code) {
        List<Token> tokens = new ArrayList<>();
        int position = 0;
        int currentIndent = 0;
        int lastNewlinePosition = -1; // Track the position of the last newline character

        Matcher m = Pattern.compile("(\\w+|\\s+|[\\p{Punct}])|(\\R)").matcher(code);
        while (m.find()) {
            String tokenValue = m.group(0);
            if (m.group(2) != null) { // If it's a newline
                tokens.add(new Token(tokenValue, position, 0)); // Newlines have no indent
                lastNewlinePosition = position;
                currentIndent = 0; // Reset indentation at the start of a new line
            } else if (m.group(1) != null) {
                if (lastNewlinePosition == -1) {
                    currentIndent = position; // If no newline has been found yet
                } else {
                    currentIndent = position - lastNewlinePosition - 1; // Characters before the token on the same line
                }
                tokens.add(new Token(tokenValue, position, currentIndent));
            }
            position += tokenValue.length();
        }
        return tokens;
    }*/


    private String getWithIndentation(Token token) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(Math.max(0, token.getIntentLevel()))); // Apply indentation
        sb.append(token.getValue());
        return sb.toString();
    }

    public String generateDiffString(String originalCode, String modifiedCode) {
        List<Token> originalTokens = tokenize(originalCode);
        List<Token> modifiedTokens = tokenize(modifiedCode);

        return generateDiffString(originalTokens, modifiedTokens);
    }

    // Generate a diff string with marked changes (for reference or other use cases)
    public String generateDiffString(List<Token> originalTokens, List<Token> modifiedTokens) {
        List<String> originalTokensValues = new ArrayList<>();
        for (Token token : originalTokens) {
            originalTokensValues.add(token.getValue());
        }
        List<String> modifiedTokensValues = new ArrayList<>();
        for (Token token : modifiedTokens) {
            modifiedTokensValues.add(token.getValue());
        }
        Patch<String> patch = DiffUtils.diff(originalTokensValues, modifiedTokensValues);
        StringBuilder diffString = new StringBuilder();

        int indexOrig = 0;
        List<AbstractDelta<String>> deltas = patch.getDeltas();
        for (var delta : deltas) {
            // Append unchanged tokens
            while (indexOrig < delta.getSource().getPosition()) {
                diffString.append(originalTokens.get(indexOrig++));
            }

            switch (delta.getType()) {
                case DELETE:
                    // Append deleted tokens
                    for (String line : delta.getSource().getLines()) {
                        diffString.append("-[-]-").append(line);
                    }
                    break;
                case INSERT:
                    // Append inserted tokens
                    for (String line : delta.getTarget().getLines()) {
                        diffString.append("-[+]-").append(line);
                    }
                    break;
                case CHANGE:
                    // Append changed tokens
                    for (String line : delta.getSource().getLines()) {
                        diffString.append("-[~]-").append(line);
                    }
                    break;
                case EQUAL:
                    // Append unchanged tokens
                    for (String line : delta.getSource().getLines()) {
                        diffString.append("-[=]-").append(line);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected delta type: " + delta.getType());
            }
        }

        // Append remaining unchanged tokens
        while (indexOrig < originalTokens.size()) {
            diffString.append(originalTokens.get(indexOrig++));
        }

        return diffString.toString();
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
        return reconstructModifiedCodeWithDeltas(originalTokens, modifiedTokens, patch.getDeltas());
    }

    // Directly reconstruct the modified code from tokens, handling insertions and deletions
    public String reconstructModifiedCodeWithDeltas(List<Token> originalTokens, List<Token> modifiedTokens, List<AbstractDelta<String>> deltas) {
        StringBuilder modifiedCode = new StringBuilder();
        int indexOrig = 0;

        for (var delta : deltas) {
            // Append unchanged tokens up to the current delta position
            while (indexOrig < delta.getSource().getPosition()) {
                modifiedCode.append(originalTokens.get(indexOrig++));
            }
            //Token modifiedToken = modifiedTokens.get(delta.getTarget().getPosition());
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
    /*public String reconstructModifiedCodeWithDeltas(List<Token> originalTokens, List<Token> modifiedTokens, List<AbstractDelta<String>> deltas) {
        StringBuilder modifiedCode = new StringBuilder();
        int indexOrig = 0;
        int indexMod = 0;  // Ensure to track position in modified tokens as well

        for (var delta : deltas) {
            while (indexOrig < delta.getSource().getPosition()) {
                Token token = originalTokens.get(indexOrig++);
                modifiedCode.append(token.getValue()); // Append value only, ignoring indentation
            }

            for (int i = 0; i < delta.getTarget().getLines().size(); i++) {
                if (indexMod < modifiedTokens.size()) {
                    Token modToken = modifiedTokens.get(indexMod++);
                    modifiedCode.append(modToken.getValue()); // Append modified tokens ignoring indentation
                }
            }

            indexOrig += delta.getSource().getLines().size();  // Skip source lines that were deleted or changed
        }

        while (indexOrig < originalTokens.size()) {
            modifiedCode.append(originalTokens.get(indexOrig++).getValue()); // Append remaining tokens ignoring indentation
        }

        return modifiedCode.toString();
    }*/



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

        return reconstructModifiedCodeWithDeltas(originalTokens, modifiedTokens, deltas);
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
            if (currentDelta.getType() == DeltaType.INSERT || currentDelta.getType() == DeltaType.EQUAL || currentDelta.getType() == DeltaType.CHANGE) {
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
            if (currentDelta.getType() == DeltaType.DELETE/* || currentDelta.getType() == DeltaType.CHANGE*/) {
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
            if (currentDelta.getType() == DeltaType.INSERT || currentDelta.getType() == DeltaType.EQUAL || currentDelta.getType() == DeltaType.CHANGE) {
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
            if (currentDelta.getType() == DeltaType.DELETE/* || currentDelta.getType() == DeltaType.CHANGE*/) {
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
        int start = index;
        int end = index;
        while (start > 0 && code.charAt(start) != '\n') {
            start--;
        }
        while (end < code.length() && code.charAt(end) != '\n') {
            end++;
        }
        return code.substring(start, end);
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

    private void restoreContextAroundOmission(int deltaIndex, List<Token> tokens, List<AbstractDelta<String>> deltas) {
        AbstractDelta<String> delta = deltas.get(deltaIndex);
        int startPos = delta.getSource().getPosition();
        int endPos = startPos + delta.getSource().getLines().size();

        // Restore preceding context
        int prev = startPos - 1;
        while (prev >= 0 && !tokens.get(prev).getValue().equals("-[=]-") && !tokens.get(prev).getValue().startsWith("-[+]-")) {
            if (tokens.get(prev).getValue().startsWith("-[-]-")) {

                tokens.get(prev).setValue(tokens.get(prev).getValue().replace("-[-]-", ""));
            }
            prev--;
        }

        // Restore following context
        int next = endPos;
        while (next < tokens.size() && !tokens.get(next).getValue().equals("-[=]-") && !tokens.get(next).getValue().startsWith("-[+]-")) {
            if (tokens.get(next).getValue().startsWith("-[-]-")) {
                tokens.get(next).setValue(tokens.get(next).getValue().replace("-[-]-", ""));
            }
            next++;
        }

        // Clear the omitted tokens
        for (int i = startPos; i < endPos; i++) {
            tokens.get(i).setValue("");  // Remove the actual omission marker and surrounding context
        }
    }
}
