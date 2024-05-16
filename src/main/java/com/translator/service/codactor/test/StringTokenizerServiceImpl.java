package com.translator.service.codactor.test;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String generateDiffString(String originalCode, String modifiedCode) {
        List<Token> originalTokens = tokenize(originalCode);
        List<Token> modifiedTokens = tokenize(modifiedCode);

        return generateDiffString(originalTokens, modifiedTokens);
    }

    // Generate a diff string with marked changes (for reference or other use cases)
    public String generateDiffString(List<Token> originalTokens, List<Token> modifiedTokens) {
        List<String> originalTokensValues = new ArrayList<>();
        for (Token token : originalTokens) {
            originalTokensValues.add(token.value);
        }
        List<String> modifiedTokensValues = new ArrayList<>();
        for (Token token : modifiedTokens) {
            modifiedTokensValues.add(token.value);
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
            originalTokensValues.add(token.value);
        }
        List<Token> modifiedTokens = tokenize(modifiedCode);
        List<String> modifiedTokensValues = new ArrayList<>();
        for (Token token : modifiedTokens) {
            modifiedTokensValues.add(token.value);
        }
        Patch<String> patch = DiffUtils.diff(originalTokensValues, modifiedTokensValues);
        return reconstructModifiedCodeWithDeltas(originalTokens, patch.getDeltas());
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


    public String reconstructModifiedCodeWithRestoration(String originalCode, String modifiedCode) {
        List<Token> originalTokens = tokenize(originalCode);
        List<Token> modifiedTokens = tokenize(modifiedCode);

        return reconstructModifiedCodeWithRestoration(modifiedCode, originalTokens, modifiedTokens);
    }

    public String reconstructModifiedCodeWithRestoration(String modifiedCode, List<Token> originalTokens, List<Token> modifiedTokens) {
        List<String> originalTokensValues = new ArrayList<>();
        for (Token token : originalTokens) {
            originalTokensValues.add(token.value);
        }
        List<String> modifiedTokensValues = new ArrayList<>();
        for (Token token : modifiedTokens) {
            modifiedTokensValues.add(token.value);
        }
        Patch<String> patch = DiffUtils.diff(originalTokensValues, modifiedTokensValues);
        List<AbstractDelta<String>> deltas = new ArrayList<>(patch.getDeltas());

        adjustDeltasForOmissions(modifiedCode, modifiedTokens, deltas);

        return reconstructModifiedCodeWithDeltas(originalTokens, deltas);
    }




    // Restore omitted tokens in the diff string:
    private void adjustDeltasForOmissions(String modifiedCode, List<Token> modifiedTokens, List<AbstractDelta<String>> deltas) {
        List<AbstractDelta<String>> toRemove = new ArrayList<>();
        deltas.sort(Comparator.comparingInt(delta -> delta.getSource().getPosition()));

        for (int i = 0; i < deltas.size(); i++) {
            AbstractDelta<String> delta = deltas.get(i);
            if (isOmissionMarker(modifiedCode, modifiedTokens, delta)) {
                System.out.println("Omission found: " + delta.getSource().getLines());
                adjustPrecedingDeltas(modifiedCode, modifiedTokens, deltas, delta, i, toRemove);
                adjustFollowingDeltas(modifiedCode, modifiedTokens, deltas, delta, i, toRemove);
            }
        }
        deltas.removeAll(toRemove);
    }

    private void adjustPrecedingDeltas(String modifiedCode, List<Token> modifiedTokens, List<AbstractDelta<String>> deltas, AbstractDelta<String> omissionDelta, int index, List<AbstractDelta<String>> toRemove) {
        Token omissionDeltaToken = modifiedTokens.get(omissionDelta.getTarget().getPosition());
        int j = index - 1;
        while (j >= 0) {
            AbstractDelta<String> currentDelta = deltas.get(j);
            if (currentDelta.getType() == DeltaType.INSERT || currentDelta.getType() == DeltaType.EQUAL) {
                 Token token = modifiedTokens.get(currentDelta.getTarget().getPosition());
                if (getLineNumberOfText(modifiedCode, token.position) != getLineNumberOfText(modifiedCode, omissionDeltaToken.position)) {
                    System.out.println("Going back stopped by delta: " + currentDelta.getSource().getLines());
                    System.out.println("Following delta type: " + currentDelta.getType());
                    System.out.println("Line at delta: " + getLineOfCodeAtDelta(modifiedCode, modifiedTokens, currentDelta));
                    break;
                } else {
                    System.out.println("Both deltas did not cancel because they are on line: " + getLineNumberOfText(modifiedCode, token.position));
                }
            }
            if (currentDelta.getType() == DeltaType.DELETE || currentDelta.getType() == DeltaType.CHANGE) {
                toRemove.add(currentDelta);
            }
            j--;
        }
    }

    private void adjustFollowingDeltas(String modifiedCode, List<Token> modifiedTokens, List<AbstractDelta<String>> deltas, AbstractDelta<String> omissionDelta, int index, List<AbstractDelta<String>> toRemove) {
        Token omissionDeltaToken = modifiedTokens.get(omissionDelta.getTarget().getPosition());
        int j = index + 1;
        while (j < deltas.size()) {
            AbstractDelta<String> currentDelta = deltas.get(j);
            if (currentDelta.getType() == DeltaType.INSERT || currentDelta.getType() == DeltaType.EQUAL) {
               Token token = modifiedTokens.get(currentDelta.getTarget().getPosition());
                if (getLineNumberOfText(modifiedCode, token.position) != getLineNumberOfText(modifiedCode, omissionDeltaToken.position)) {
                    System.out.println("Going forward Stopped by delta: " + currentDelta.getSource().getLines());
                    System.out.println("Following delta type: " + currentDelta.getType());
                    System.out.println("Line at delta: " + getLineOfCodeAtDelta(modifiedCode, modifiedTokens, currentDelta));
                    break;
                } else {
                    System.out.println("Both deltas did not cancel because they are on line: " + getLineNumberOfText(modifiedCode, token.position));
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

    private void restoreContextAroundOmission(int deltaIndex, List<Token> tokens, List<AbstractDelta<String>> deltas) {
        AbstractDelta<String> delta = deltas.get(deltaIndex);
        int startPos = delta.getSource().getPosition();
        int endPos = startPos + delta.getSource().getLines().size();

        // Restore preceding context
        int prev = startPos - 1;
        while (prev >= 0 && !tokens.get(prev).value.equals("-[=]-") && !tokens.get(prev).value.startsWith("-[+]-")) {
            if (tokens.get(prev).value.startsWith("-[-]-")) {

                tokens.get(prev).setValue(tokens.get(prev).getValue().replace("-[-]-", ""));
            }
            prev--;
        }

        // Restore following context
        int next = endPos;
        while (next < tokens.size() && !tokens.get(next).value.equals("-[=]-") && !tokens.get(next).value.startsWith("-[+]-")) {
            if (tokens.get(next).value.startsWith("-[-]-")) {
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
