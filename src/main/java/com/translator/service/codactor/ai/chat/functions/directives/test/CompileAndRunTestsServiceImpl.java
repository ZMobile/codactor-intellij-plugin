package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.compiler.CompileStatusNotification;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.translator.model.codactor.ai.chat.function.directive.CreateAndRunUnitTestDirectiveSession;
import com.translator.service.codactor.io.DynamicClassCompilerService;
import com.translator.service.codactor.io.DynamicClassLoaderService;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class CompileAndRunTestsServiceImpl implements CompileAndRunTestsService {
    private final DynamicClassCompilerService dynamicClassCompilerService;
    private final RunTestAndGetOutputService runTestAndGetOutputService;

    @Inject
    public CompileAndRunTestsServiceImpl(DynamicClassCompilerService dynamicClassCompilerService,
                                         RunTestAndGetOutputService runTestAndGetOutputService) {
        this.dynamicClassCompilerService = dynamicClassCompilerService;
        this.runTestAndGetOutputService = runTestAndGetOutputService;
    }

    @Override
    public List<String> compileAndRunUnitTests(String implementationFilePath, List<String> unitTestFilePaths) {
        CountDownLatch latch = new CountDownLatch(unitTestFilePaths.size() + 1); // +1 for the implementation file
        AtomicReference<List<String>> resultsRef = new AtomicReference<>();
        AtomicReference<Exception> compilationException = new AtomicReference<>();

        CompileStatusNotification compileCallback = (aborted, errors, warnings, compileContext) -> {
            try {
                if (aborted) {
                    System.out.println("Compilation aborted.");
                    compilationException.set(new Exception("Compilation aborted."));
                } else if (errors > 0) {
                    System.out.println("Compilation finished with errors.");
                    compilationException.set(new Exception("Compilation finished with errors."));
                } else {
                    System.out.println("Compilation completed successfully with " + warnings + " warnings.");
                }
            } catch (Exception e) {
                compilationException.set(e);
            } finally {
                latch.countDown(); // Signal that this compilation is complete
            }
        };

        // Save all open documents before starting the compilation
        ApplicationManager.getApplication().invokeAndWait(() -> {
            FileDocumentManager.getInstance().saveAllDocuments();
        }, ModalityState.defaultModalityState());

        // Compile the implementation file
        dynamicClassCompilerService.dynamicallyCompileClass(implementationFilePath, compileCallback);

        // Compile all unit test files
        for (String testFilePath : unitTestFilePaths) {
            dynamicClassCompilerService.dynamicallyCompileClass(testFilePath, compileCallback);
        }

        try {
            // Wait for all compilations to complete
            latch.await();

            if (compilationException.get() != null) {
                throw compilationException.get(); // Throw the compilation exception if any occurred
            }

            System.out.println("All compilations completed successfully. Running tests...");

            // Run all compiled tests
            resultsRef.set(runTestAndGetOutputService.runTestsAndGetOutputs(implementationFilePath, unitTestFilePaths));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to compile and run unit tests: " + e.getMessage(), e);
        }

        // Return the results of the test runs
        return resultsRef.get();
    }

}
