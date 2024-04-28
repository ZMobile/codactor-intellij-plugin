package com.translator.model.codactor.ai.chat.function.directive;

import com.translator.model.codactor.ai.chat.function.GptFunction;
import com.translator.model.codactor.ai.chat.function.Parameters;
import com.translator.model.codactor.ai.chat.function.Property;

import java.util.ArrayList;
import java.util.List;

public class CreateAndRunUnitTestDirective {
    private List<GptFunction> phaseOneFunctions; //Activate unit test
    private List<GptFunction> phaseTwoFunctions; //build unit test
    private List<GptFunction> phaseTwoAndThreeFunctions; //Place logs
    // Once unit test is built, we give phase three options
    private List<GptFunction> phaseThreeFunctions; //Run unit test, modify unit test
    // Then we give phase two and phase four options
    private List<GptFunction> phaseFourFunctions; //Terminate test loop and summarize results

    public CreateAndRunUnitTestDirective() {
        this.phaseOneFunctions = new ArrayList<>();
        Parameters createAndRunUnitTestParams = new Parameters("object");
        Property pathProperty = new Property("string", "The file path of the subject code file", null, null);
        createAndRunUnitTestParams.getProperties().put("path", pathProperty);
        createAndRunUnitTestParams.getRequired().add("path");
        Property testDescriptionProperty = new Property("string", "The description of the test being created and what is being tested", null, null);
        createAndRunUnitTestParams.getProperties().put("description", testDescriptionProperty);
        GptFunction createAndRunUnitTest = new GptFunction("create_and_run_unit_test", "Begin the process of creating and running a unit for a code file. It will be deleted at the conclusion of this process", createAndRunUnitTestParams);
        this.phaseOneFunctions.add(createAndRunUnitTest);
        this.phaseTwoFunctions = new ArrayList<>();
        Parameters createUnitTestCodeFileParams = new Parameters("object");
        Property testCodeProperty = new Property("string", "The unit test code", null, null);
        createUnitTestCodeFileParams.getProperties().put("code", testCodeProperty);
        createUnitTestCodeFileParams.getRequired().add("code");
        GptFunction createUnitTestCodeFile = new GptFunction("create_unit_test_code_file", "Create the unit test code file", createUnitTestCodeFileParams);
        this.phaseTwoFunctions.add(createUnitTestCodeFile);
        this.phaseTwoAndThreeFunctions = new ArrayList<>();
        Parameters readSubjectCodeFileParams = new Parameters("object");
        GptFunction readSubjectCodeFile = new GptFunction("read_subject_code_file", "Read the code file that the unit test will be based on", readSubjectCodeFileParams);
        this.phaseTwoAndThreeFunctions.add(readSubjectCodeFile);
        Parameters placeLogsInSubjectCodeFileParams = new Parameters("object");
        List<String> modificationTypes = new ArrayList<>();
        modificationTypes.add("modify");
        modificationTypes.add("fix");
        modificationTypes.add("create");
        Property startSnippetProperty = new Property("string", "A snippet of code from within the file marking the start boundary of the selection. Upon use, the start index of the modification will be at the start of this string. Ideally, the start snippet should include enough to make it unique so it can be used to find its location in the code with precision. If not utilized, it will automatically be set to the beginning of the file.", null, null);
        Property endSnippetProperty = new Property("string", "A snippet of code from within the file marking the end boundary of the selection. Upon use, the end index of the modification will be at the start of this string. Ideally the end snippet should include enough to make it unique so it can be used to find its location in the code with precision. If not utilized, it will automatically be set to the end of the file.", null, null);
        Property codeSnippetProperty = new Property("string", "A snippet of code from within the file marking the start and end of the selection. This property overrides the startSnippet and endSnippet properties--it should not be used start snippet or end snippet are used.", null, null);
        Property modificationTypeProperty = new Property("string", "The type of file modification types to choose from are 'modify' for modifying code in a file, 'fix' for reporting and fixing an error (like modify but for complaining), and 'create' for creating new code where none existed before, so it will ignore the startSnippet, endSnippet, and codeSnippet, placing its created code at index 0.", modificationTypes, null);
        Property descriptionProperty = new Property("string", "The description of the modification to be enacted on this specific code snippet provided within the boundaries", null, null);
        Property replacementCodeProperty = new Property("string", "The code to replace the target code snippet. This code must serve as a full replacement for the target code as it will be directly replacing the code snippet provided if accepted.", null, null);
        placeLogsInSubjectCodeFileParams.getProperties().put("modificationType", modificationTypeProperty);
        placeLogsInSubjectCodeFileParams.getProperties().put("description", descriptionProperty);
        placeLogsInSubjectCodeFileParams.getProperties().put("startBoundary", startSnippetProperty);
        placeLogsInSubjectCodeFileParams.getProperties().put("endBoundary", endSnippetProperty);
        placeLogsInSubjectCodeFileParams.getProperties().put("codeSnippet", codeSnippetProperty);
        placeLogsInSubjectCodeFileParams.getProperties().put("replacementCodeSnippet", replacementCodeProperty);
        placeLogsInSubjectCodeFileParams.getRequired().add("modificationType");
        placeLogsInSubjectCodeFileParams.getRequired().add("description");
        placeLogsInSubjectCodeFileParams.getRequired().add("replacementCodeSnippet");
        GptFunction placeLogsInSubjectCodeFile = new GptFunction("place_logs_in_subject_code_file", "Modify the subject code file to place logs", placeLogsInSubjectCodeFileParams);
        this.phaseTwoAndThreeFunctions.add(placeLogsInSubjectCodeFile);
        this.phaseThreeFunctions = new ArrayList<>();
        Parameters readUnitTestParams = new Parameters("object");
        GptFunction readUnitTest = new GptFunction("read_unit_test", "Read the unit test file", readUnitTestParams);
        this.phaseThreeFunctions.add(readUnitTest);
        Parameters modifyUnitTestParams = new Parameters("object");
        modifyUnitTestParams.getProperties().put("modificationType", modificationTypeProperty);
        modifyUnitTestParams.getProperties().put("description", descriptionProperty);
        modifyUnitTestParams.getProperties().put("startBoundary", startSnippetProperty);
        modifyUnitTestParams.getProperties().put("endBoundary", endSnippetProperty);
        modifyUnitTestParams.getProperties().put("codeSnippet", codeSnippetProperty);
        modifyUnitTestParams.getProperties().put("replacementCodeSnippet", replacementCodeProperty);
        modifyUnitTestParams.getRequired().add("modificationType");
        modifyUnitTestParams.getRequired().add("description");
        modifyUnitTestParams.getRequired().add("replacementCodeSnippet");
        GptFunction modifyUnitTest = new GptFunction("modify_unit_test", "Modify the content of the unit test", modifyUnitTestParams);
        this.phaseThreeFunctions.add(modifyUnitTest);
        Parameters runUnitTestParams = new Parameters("object");
        GptFunction runUnitTest = new GptFunction("run_unit_test", "Run the unit test and get logs", runUnitTestParams);
        this.phaseThreeFunctions.add(runUnitTest);
        this.phaseFourFunctions = new ArrayList<>();
        Parameters terminateTestLoopParams = new Parameters("object");
        GptFunction terminateTestLoop = new GptFunction("terminate_test_loop", "Terminate the test loop", terminateTestLoopParams);
        this.phaseFourFunctions.add(terminateTestLoop);
    }

    public List<GptFunction> getPhaseOneFunctions() {
        return phaseOneFunctions;
    }

    public List<GptFunction> getPhaseTwoFunctions() {
        return phaseTwoFunctions;
    }

    public List<GptFunction> getPhaseTwoAndThreeFunctions() {
        return phaseTwoAndThreeFunctions;
    }

    public List<GptFunction> getPhaseThreeFunctions() {
        return phaseThreeFunctions;
    }

    public List<GptFunction> getPhaseFourFunctions() {
        return phaseFourFunctions;
    }

    public void setPhaseOneFunctions(List<GptFunction> phaseOneFunctions) {
        this.phaseOneFunctions = phaseOneFunctions;
    }

    public void setPhaseTwoFunctions(List<GptFunction> phaseTwoFunctions) {
        this.phaseTwoFunctions = phaseTwoFunctions;
    }

    public void setPhaseTwoAndThreeFunctions(List<GptFunction> phaseTwoAndThreeFunctions) {
        this.phaseTwoAndThreeFunctions = phaseTwoAndThreeFunctions;
    }

    public void setPhaseThreeFunctions(List<GptFunction> phaseThreeFunctions) {
        this.phaseThreeFunctions = phaseThreeFunctions;
    }

    public void setPhaseFourFunctions(List<GptFunction> phaseFourFunctions) {
        this.phaseFourFunctions = phaseFourFunctions;
    }
}
