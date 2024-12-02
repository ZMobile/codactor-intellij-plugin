package com.translator.service.codactor.ai.modification.test;

import com.github.gumtreediff.actions.ChawatheScriptGenerator;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.EditScriptGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.gen.srcml.SrcmlJavaTreeGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class StringCodeOmittedRestorerServiceImpl implements StringCodeOmittedRestorerService {
    private DiffStringService diffStringService;

    public StringCodeOmittedRestorerServiceImpl() {
        this.diffStringService = new DiffStringServiceImpl();
    }

    @Override
    public String restoreOmittedString(String diffCode) {
        List<String> lines = Arrays.asList(diffCode.split("\n"));

        for (int i=0; i<lines.size(); i++) {

            if (lines.get(i).contains("-[+]-") && (((lines.get(i).toLowerCase().contains("omitted") || lines.get(i).toLowerCase().contains("not shown")) && lines.get(i).toLowerCase().contains("brevity")) || (lines.get(i).toLowerCase().contains("// other") || lines.get(i).toLowerCase().contains("# other") || lines.get(i).toLowerCase().contains("// ...")))) {
                int prev = i - 1;
                int next = i + 1;
                while (prev > 0 && (!lines.get(prev).contains("-[=]-") || lines.get(prev).replace("-[=]-", "").trim().isEmpty())) {
                    lines.set(prev, lines.get(prev).replace("-[-]-", ""));
                    prev--;
                }

                while (next < lines.size() && (!lines.get(next).contains("-[=]-") || lines.get(next).replace("-[=]-", "").trim().isEmpty())) {
                    lines.set(next, lines.get(next).replace("-[-]-", ""));
                    next++;
                }

                // remove the -[+]- line
                lines.set(i, "");
            }
        }

        // removing all the remaining lines containing -[-]
        List<String> filteredLines = lines.stream()
                .filter(line -> !line.contains("-[-]-"))
                .collect(Collectors.toList());
        List<String> tagsRemovedLines = new ArrayList<>();

        for (String line : filteredLines) {
            if (line.contains("-[=]-")) {
                line = line.replace("-[=]-", "");
            } else if (line.contains("-[+]-")) {
                line = line.replace("-[+]-", "");
            }
            tagsRemovedLines.add(line);
        }
        diffCode = String.join("\n", tagsRemovedLines);

        return diffCode;
    }

    public String restoreOmittedString(String beforeCode, String afterCode) {
        String result = diffStringService.getDiffString(beforeCode, afterCode);
        result = diffStringService.postProcessDiffString(result);
        return restoreOmittedString(result);
    }

    @Override
    public boolean containsOmittedString(String diffCode) {
        String[] lines = diffCode.split("\n");
        for (String line : lines) {
            if (line.contains("-[+]-") && (((line.toLowerCase().contains("omitted") || line.toLowerCase().contains("not shown")) && line.toLowerCase().contains("brevity")) || (line.toLowerCase().contains("// other") || line.toLowerCase().contains("# other") || line.toLowerCase().contains("// ...")))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsOmittedStringWithoutDiffMarkers(String diffCode) {
        String[] lines = diffCode.split("\n");
        for (String line : lines) {
            if (((line.toLowerCase().contains("omitted") || line.toLowerCase().contains("not shown")) && line.toLowerCase().contains("brevity")) || (line.toLowerCase().contains("// other") || line.toLowerCase().contains("# other") || line.toLowerCase().contains("// ..."))) {
                return true;
            }
        }
        return false;
    }
    /*public String restoreOmittedCode(String originalCode, String modifiedCode) throws IOException {
        Run.initGenerators(); // Initialize all necessary generators

        TreeGenerator generator = new SrcmlJavaTreeGenerator(); // Assuming SrcML generator is set up correctly

        // Generate ASTs from source code
        TreeContext srcContext = generator.generateFrom().string(originalCode);
        TreeContext dstContext = generator.generateFrom().string(modifiedCode);

        // Compute the matchings
        Matcher matcher = Matchers.getInstance().getMatcher();
        MappingStore mappings = matcher.match(srcContext.getRoot(), dstContext.getRoot());

        // Generate edit script using a concrete generator
        ChawatheScriptGenerator editScriptGenerator = new ChawatheScriptGenerator();
        EditScript editScript = editScriptGenerator.computeActions(mappings);


        // Assuming processActions needs to be implemented to use actions from editScript
        return processActions(editScript, srcContext, dstContext);
    }

    private String processActions(EditScript actions, TreeContext srcContext, TreeContext dstContext) {
        StringBuilder result = new StringBuilder();
        for (Action action : actions) {
            if (action instanceof Insert) {
                Tree node = action.getNode();
                if (node.getType().name.equals("Comment") && isOmittedCodeComment(node.getLabel())) {
                    result.append(getSourceCodeFromNode(node));
                }
            }
        }
        return result.toString();
    }*/

    private boolean isOmittedCodeComment(String comment) {
        String lowerCaseComment = comment.toLowerCase();
        return (lowerCaseComment.contains("omitted") && lowerCaseComment.contains("brevity")
                || lowerCaseComment.contains("// other") || lowerCaseComment.contains("# other") || lowerCaseComment.contains("// ..."));
    }

    private String getSourceCodeFromNode(Tree node) {
        // This method needs to be accurately implemented depending on node data structure
        return node.toTreeString();  // Placeholder: adjust according to your method for converting a node to source code
    }
}
