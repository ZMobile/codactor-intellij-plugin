package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.intellij.openapi.application.ApplicationManager;
import com.translator.model.codactor.ai.chat.function.directive.test.ReplacedClassInfoResource;
import com.translator.model.codactor.ai.chat.function.directive.test.ResultsResource;
import com.translator.service.codactor.ide.editor.RangeReplaceService;
import org.junit.runner.notification.Failure;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class TestRunnerLoopServiceImpl implements TestRunnerLoopService {
    private final CompileAndRunTestsService compileAndRunTestsService;
    private final ImplementationFixerService implementationFixerService;
    private final RangeReplaceService rangeReplaceService;

    @Inject
    public TestRunnerLoopServiceImpl(CompileAndRunTestsService compileAndRunTestsService, ImplementationFixerService implementationFixerService, RangeReplaceService rangeReplaceService) {
        this.compileAndRunTestsService = compileAndRunTestsService;
        this.implementationFixerService = implementationFixerService;
        this.rangeReplaceService = rangeReplaceService;
    }

    @Override
    public void runUnitTestsAndGetFeedback(String directoryPath, String implementationFilePath, String interfaceFilePath, ReplacedClassInfoResource replacedClassInfoResource) {
        //this.unitTestStatusLabel.setText("Compiling and running...");
        String finalImplementationFilePath = implementationFilePath;
        String finalInterfaceFilePath = interfaceFilePath;
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<ResultsResource> results = compileAndRunTestsService.compileAndRunUnitTests(finalInterfaceFilePath, finalImplementationFilePath, directoryPath);
            List<ResultsResource> failedResults = new ArrayList<>();
            List<ResultsResource> passedResults = new ArrayList<>();
            for (ResultsResource resultsResource : results) {
                if (resultsResource.getResult() == null || !resultsResource.getResult().wasSuccessful()) {
                    failedResults.add(resultsResource);
                } else {
                    passedResults.add(resultsResource);
                }
            }
            //this.unitTestStatusLabel.setText("Unit Test Status: (" + passedResults.size() + "/" + results.size() + ") passed");
            StringBuilder failedUnitTestsText = new StringBuilder("Failed Unit Tests: \n\n");
            for (ResultsResource resultsResource : failedResults) {
                if (resultsResource.getResult() == null) {
                    failedUnitTestsText.append(resultsResource.getError());
                    continue;
                }
                for (Failure failure : resultsResource.getResult().getFailures()) {
                    failedUnitTestsText.append(failure.toString()).append("\n");
                }
                failedUnitTestsText.append("\n");
            }
            //this.failedUnitTestsTextArea.setText(failedUnitTestsText.toString());

            if (failedResults.isEmpty()) {
                //this.failedUnitTestsTextArea.setText("All unit tests passed!");
            } else {
                //this.failedUnitTestsTextArea.setText(failedUnitTestsText.toString());
                boolean areNewResultsBetter = areNewResultsBetter(replacedClassInfoResource, results);
                System.out.println("Are new results better? " + areNewResultsBetter);

                if (areNewResultsBetter) {
                    ReplacedClassInfoResource newReplacedClassInfoResource = implementationFixerService.startFixing(finalImplementationFilePath, interfaceFilePath, results);

                    runUnitTestsAndGetFeedback(directoryPath, implementationFilePath, interfaceFilePath, newReplacedClassInfoResource);
                } else {
                    System.out.println("The new results are not better than the old results");
                    rangeReplaceService.replaceRange(replacedClassInfoResource.getFilePath(), 0, replacedClassInfoResource.getNewCode().length(), replacedClassInfoResource.getOldCode(), true);

                    List<ResultsResource> formerResults = replacedClassInfoResource.getFormerResults();

                    if (!formerResults.isEmpty()) {
                        ResultsResource firstResultsResource = formerResults.remove(0);
                        formerResults.add(firstResultsResource);
                    }

                    runUnitTestsAndGetFeedback(directoryPath, implementationFilePath, interfaceFilePath, replacedClassInfoResource);
                }
            }
            /*StringBuilder passedUnitTestsText = new StringBuilder("Passed Unit Tests: \n\n");
            for (Result result : passedResults) {
                passedUnitTestsText.append(result).append(" tests passed\n");
            }
            this.passedUnitTestsTextArea.setText(passedUnitTestsText.toString());*/
        });
    }

    private boolean areNewResultsBetter(ReplacedClassInfoResource replacedClassInfoResource, List<ResultsResource> newResults) {
        if (replacedClassInfoResource == null) {
            return true;
        }
        List<ResultsResource> oldResults = replacedClassInfoResource.getFormerResults();
        if (oldResults == null || oldResults.isEmpty()) {
            return true;
        }
        //Measurement: less failures OR, more total tests passed
        int oldFailures = 0;
        int newFailures = 0;
        int oldCompErrors = 0;
        int newCompErrors = 0;
        for (ResultsResource resultsResource : oldResults) {
            if (resultsResource.getResult() == null || !resultsResource.getResult().wasSuccessful()) {
                oldFailures++;
                if (resultsResource.getResult() == null) {
                    oldCompErrors++;
                }
            }
        }
        for (ResultsResource resultsResource : newResults) {
            if (resultsResource.getResult() == null || !resultsResource.getResult().wasSuccessful()) {
                newFailures++;
                if (resultsResource.getResult() == null) {
                    newCompErrors++;
                }
            }
        }
        if (newFailures == oldFailures) {
            if (oldCompErrors == newCompErrors) {
                return newResults.size() > oldResults.size();
            }
            return newCompErrors < oldCompErrors;
        }
        return newFailures < oldFailures;
    }
}
