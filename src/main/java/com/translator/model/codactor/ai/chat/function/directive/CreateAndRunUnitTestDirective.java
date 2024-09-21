package com.translator.model.codactor.ai.chat.function.directive;

import com.translator.model.codactor.ai.chat.function.GptFunction;
import com.translator.model.codactor.ai.chat.function.Parameters;
import com.translator.model.codactor.ai.chat.function.Property;

import java.util.ArrayList;
import java.util.List;

public class CreateAndRunUnitTestDirective extends Directive {
    private List<GptFunction> phaseOneFunctions; //build unit test
    private List<GptFunction> phaseOneAndTwoFunctions; //Place logs
    // Once unit test is built, we give phase three options
    private List<GptFunction> phaseTwoFunctions; //Run unit test, modify unit test
    // Then we give phase two and phase four options
    private List<GptFunction> phaseThreeFunctions; //Terminate test loop and summarize results
    private CreateAndRunUnitTestDirectiveSession session;

    public CreateAndRunUnitTestDirective() {
        this.session = new CreateAndRunUnitTestDirectiveSession();
        this.phaseOneFunctions = new ArrayList<>();
        Parameters createAndRunUnitTestParams = new Parameters("object");
        Property pathProperty = new Property("string", "The file path of the subject code file", null, null);
        createAndRunUnitTestParams.getProperties().put("path", pathProperty);
        createAndRunUnitTestParams.getRequired().add("path");
        Property testDescriptionProperty = new Property("string", "The description of the test being created and what is being tested and/or the goal of the test", null, null);
        createAndRunUnitTestParams.getProperties().put("description", testDescriptionProperty);
        createAndRunUnitTestParams.getRequired().add("description");
        GptFunction createAndRunUnitTest = new GptFunction("create_and_run_unit_test", "Begin the process of creating and running a unit for a code file. It will be deleted at the conclusion of this process", createAndRunUnitTestParams);
        this.phaseOneFunctions.add(createAndRunUnitTest);
        this.phaseOneFunctions = new ArrayList<>();
        Parameters createUnitTestCodeFileParams = new Parameters("object");
        Property testCodeProperty = new Property("string", "The unit test code", null, null);
        createUnitTestCodeFileParams.getProperties().put("code", testCodeProperty);
        createUnitTestCodeFileParams.getRequired().add("code");
        GptFunction createUnitTestCodeFile = new GptFunction("create_unit_test_code_file", "Create the unit test code file. NOTE: Use packages org.junit for instance org.junit.Test or org.junit.Asset.asserEquals or it wont work. DO NOT USE JUPITER.", createUnitTestCodeFileParams);
        this.phaseOneFunctions.add(createUnitTestCodeFile);
        this.phaseOneAndTwoFunctions = new ArrayList<>();
        Parameters readSubjectCodeFileParams = new Parameters("object");
        GptFunction readSubjectCodeFile = new GptFunction("read_subject_code_file", "Read the code file that the unit test will be based on", readSubjectCodeFileParams);
        this.phaseOneAndTwoFunctions.add(readSubjectCodeFile);
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
        this.phaseOneAndTwoFunctions.add(placeLogsInSubjectCodeFile);
        this.phaseTwoFunctions = new ArrayList<>();
        Parameters readUnitTestParams = new Parameters("object");
        GptFunction readUnitTest = new GptFunction("read_unit_test", "Read the unit test file", readUnitTestParams);
        this.phaseTwoFunctions.add(readUnitTest);
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
        this.phaseTwoFunctions.add(modifyUnitTest);
        Parameters runUnitTestParams = new Parameters("object");
        GptFunction runUnitTest = new GptFunction("run_test", "Run the unit test and get logs", runUnitTestParams);
        this.phaseTwoFunctions.add(runUnitTest);
        this.phaseThreeFunctions = new ArrayList<>();
        Parameters terminateTestLoopParams = new Parameters("object");
        GptFunction endTestAndReport = new GptFunction("end_test_and_report", "Terminate the test loop. Note: this will delete the unit test file. Not to be done before running the test unless you are cancelling the experiment.", terminateTestLoopParams);
        this.phaseThreeFunctions.add(endTestAndReport);
    }

    public List<GptFunction> getPhaseOneFunctions() {
        return phaseOneFunctions;
    }

    public List<GptFunction> getPhaseOneAndTwoFunctions() {
        return phaseOneAndTwoFunctions;
    }

    public List<GptFunction> getPhaseTwoFunctions() {
        return phaseTwoFunctions;
    }

    public List<GptFunction> getPhaseThreeFunctions() {
        return phaseThreeFunctions;
    }

    public void setPhaseOneFunctions(List<GptFunction> phaseOneFunctions) {
        this.phaseOneFunctions = phaseOneFunctions;
    }

    public void setPhaseOneAndTwoFunctions(List<GptFunction> phaseOneAndTwoFunctions) {
        this.phaseOneAndTwoFunctions = phaseOneAndTwoFunctions;
    }

    public void setPhaseTwoFunctions(List<GptFunction> phaseTwoFunctions) {
        this.phaseTwoFunctions = phaseTwoFunctions;
    }

    public void setPhaseThreeFunctions(List<GptFunction> phaseThreeFunctions) {
        this.phaseThreeFunctions = phaseThreeFunctions;
    }

    public CreateAndRunUnitTestDirectiveSession getSession() {
        return session;
    }

    public void setSession(CreateAndRunUnitTestDirectiveSession session) {
        this.session = session;
    }
}
