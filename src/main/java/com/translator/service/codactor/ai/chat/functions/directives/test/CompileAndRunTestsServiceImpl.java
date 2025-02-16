package com.translator.service.codactor.ai.chat.functions.directives.test;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.junit.JUnitConfiguration;
import com.intellij.execution.junit.JUnitConfigurationType;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.TestSearchScope;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.compiler.CompileStatusNotification;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.translator.service.codactor.io.DynamicClassCompilerService;
import com.translator.service.codactor.io.DynamicClassLoaderService;
import org.junit.runner.Result;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class CompileAndRunTestsServiceImpl implements CompileAndRunTestsService {
    private final Project project;
    private final DynamicClassCompilerService dynamicClassCompilerService;
    private final RunTestAndGetOutputService runTestAndGetOutputService;
    private final FindTestsInDirectoryService findTestsInDirectoryService;
    private final PackageFromFilePathRetrievalService packageFromFilePathRetrievalService;

    @Inject
    public CompileAndRunTestsServiceImpl(Project project,
                                         DynamicClassCompilerService dynamicClassCompilerService,
                                         RunTestAndGetOutputService runTestAndGetOutputService,
                                         FindTestsInDirectoryService findTestsInDirectoryService,
                                         PackageFromFilePathRetrievalService packageFromFilePathRetrievalService) {
        this.project = project;
        this.dynamicClassCompilerService = dynamicClassCompilerService;
        this.runTestAndGetOutputService = runTestAndGetOutputService;
        this.findTestsInDirectoryService = findTestsInDirectoryService;
        this.packageFromFilePathRetrievalService = packageFromFilePathRetrievalService;
    }

    /*@Override
    public List<String> compileAndRunUnitTests(String implementationFilePath, String directoryPath) {
        List<String> testFilePaths = findTestsInDirectoryService.findTestsInDirectory(directoryPath);
        for (String testFilePath : testFilePaths) {
            System.out.println("Found test file: " + testFilePath);
        }
        return compileAndRunUnitTests(implementationFilePath, testFilePaths);
    }*/

    /*@Override
    public List<String> compileAndRunUnitTests(String implementationFilePath, List<String> unitTestFilePaths) {
        //CountDownLatch latch = new CountDownLatch(unitTestFilePaths.size() + 1); // +1 for the implementation file
        CountDownLatch latch = new CountDownLatch(1); // +1 for the implementation file
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
                System.out.println("Compilation completed.");
                latch.countDown(); // Signal that this compilation is complete
            }
        };

        // Save all open documents before starting the compilation
        ApplicationManager.getApplication().invokeAndWait(() -> {
            FileDocumentManager.getInstance().saveAllDocuments();
        }, ModalityState.defaultModalityState());

        // Compile the implementation file
        System.out.println("Implementation file path: " + implementationFilePath);
        dynamicClassCompilerService.dynamicallyCompileClass(implementationFilePath, compileCallback);

        // Compile all unit test files
        for (String testFilePath : unitTestFilePaths) {
            dynamicClassCompilerService.dynamicallyCompileClass(testFilePath, compileCallback);
        }

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
        try {
            // Wait for all compilations to complete
            latch.await();

            if (compilationException.get() != null) {
                throw compilationException.get(); // Throw the compilation exception if any occurred
            }

            System.out.println("All compilations completed successfully. Running tests...");

            // Run all compiled tests
            resultsRef.set(runTestAndGetOutputService.runTestsAndGetOutputs(implementationFilePath, unitTestFilePaths));
            for (String result : resultsRef.get()) {
                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to compile and run unit tests: " + e.getMessage(), e);
        }
        });

        // Return the results of the test runs
        return resultsRef.get();
        //return new ArrayList<>();
    }*/

    /*@Override
    public Map<String, Result> compileAndRunUnitTests(String interfaceFilePath, String implementationFilePath, String directoryPath) {
        List<String> testFilePaths = findTestsInDirectoryService.findTestsInDirectory(directoryPath);
        System.out.println("This gets called 1");
        CompilerManager compilerManager = CompilerManager.getInstance(project);
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        CountDownLatch latch = new CountDownLatch(/*testFilePaths.size()* 1); // +1 for the implementation file +1 for the interface file
        AtomicReference<Map<String, Result>> resultsRef = new AtomicReference<>();
        AtomicReference<Exception> compilationException = new AtomicReference<>();

        ApplicationManager.getApplication().invokeAndWait(
                () -> FileDocumentManager.getInstance().saveAllDocuments(),
                ModalityState.defaultModalityState()
        );

        CompileStatusNotification compileCallback = (aborted, errors, warnings, compileContext) -> {
            try {
                if (aborted) {
                    System.err.println("Compilation aborted.");
                    compilationException.set(new Exception("Compilation aborted."));
                } else if (errors > 0) {
                    System.err.println("Compilation finished with errors.");
                    compilationException.set(new Exception("Compilation finished with errors."));
                } else {
                    System.out.println("Compilation completed successfully with " + warnings + " warnings.");
                }
            } finally {
                try {
                    resultsRef.set(runTestAndGetOutputService.runTestsAndGetOutputs(
                            implementationFilePath, testFilePaths
                    ));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                /*for (String result : resultsRef.get()) {
                    System.out.println(result);
                }**
                System.out.println("Full project rebuild completed successfully.");
                latch.countDown();
            }
        };
        System.out.println("This gets called 2");
        // Perform a FULL REBUILD

        /*CompileStatusNotification compileCallback1 = (aborted, errors, warnings, compileContext) -> {
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
                System.out.println("Compilation completed.");
                latch1.countDown(); // Signal that this compilation is complete
            }
        };
        CompileStatusNotification compileCallback2 = (aborted, errors, warnings, compileContext) -> {
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
                System.out.println("Compilation completed.");
                latch2.countDown(); // Signal that this compilation is complete
            }
        };

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
                System.out.println("Compilation completed.");
                latch.countDown(); // Signal that this compilation is complete
            }
        };

        // Save all open documents before starting the compilation
        ApplicationManager.getApplication().invokeAndWait(() -> {
            FileDocumentManager.getInstance().saveAllDocuments();
        }, ModalityState.defaultModalityState());

        // Compile the implementation file
        System.out.println("Interface file path: " + interfaceFilePath);
        dynamicClassCompilerService.dynamicallyCompileClass(interfaceFilePath, compileCallback1);

        try {
            latch1.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Compile the implementation file
        System.out.println("Implementation file path: " + implementationFilePath);
        dynamicClassCompilerService.dynamicallyCompileClass(implementationFilePath, compileCallback2);

        try {
            latch2.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Compile all unit test files
        for (String testFilePath : testFilePaths) {
            dynamicClassCompilerService.dynamicallyCompileClass(testFilePath, compileCallback);
        }*/


        /*// Call rebuild on the EDT (required by IntelliJ)
        ApplicationManager.getApplication().invokeLater(() -> {
            System.out.println("Starting incremental project compile on EDT...");
            compilerManager.compile(compilerManager.createProjectCompileScope(project), compileCallback);
        });*

        dynamicClassCompilerService.dynamicallyCompileDirectory(directoryPath, compileCallback);
        try {
            // Wait for all compilations to complete
            latch.await();
            ApplicationManager.getApplication().invokeAndWait(() -> {
                System.out.println("Starting FULL project rebuild...");
                compilerManager.rebuild(compileCallback);
            });
            resultsRef.set(runTestAndGetOutputService.runTestsAndGetOutputs(
                    implementationFilePath, testFilePaths
            ));
            return resultsRef.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to compile and run unit tests: " + e.getMessage(), e);
        }
        // Wait for compilation completion on a background thread
        /*try {
            //latch.await();
            if (compilationException.get() != null) {
                throw compilationException.get();
            }
            // Run tests after compilation completes
            resultsRef.set(runTestAndGetOutputService.runTestsAndGetOutputs(
                    implementationFilePath, unitTestFilePaths
            ));
            for (String result : resultsRef.get()) {
                System.out.println(result);
            }
            System.out.println("Full project rebuild completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to rebuild the entire project: " + e.getMessage(), e);
        }*

        return resultsRef.get();*
    }*
        //return new ArrayList<>();
    }*/

    @Override
    public Map<String, Result> compileAndRunUnitTests(String interfaceFilePath, String implementationFilePath, String directoryPath) {
        List<String> testFilePaths = findTestsInDirectoryService.findTestsInDirectory(directoryPath);
        System.out.println("This gets called 1");
        CompilerManager compilerManager = CompilerManager.getInstance(project);
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        CountDownLatch latch = new CountDownLatch(1); // +1 for the implementation file +1 for the interface file
        AtomicReference<Map<String, Result>> resultsRef = new AtomicReference<>();
        AtomicReference<Exception> compilationException = new AtomicReference<>();

        ApplicationManager.getApplication().invokeAndWait(
                () -> FileDocumentManager.getInstance().saveAllDocuments(),
                ModalityState.defaultModalityState()
        );

        CompileStatusNotification compileCallback = (aborted, errors, warnings, compileContext) -> {
            try {
                if (aborted) {
                    System.err.println("Compilation aborted.");
                    compilationException.set(new Exception("Compilation aborted."));
                } else if (errors > 0) {
                    System.err.println("Compilation finished with errors.");
                    compilationException.set(new Exception("Compilation finished with errors."));
                } else {
                    System.out.println("Compilation completed successfully with " + warnings + " warnings.");
                }
            } finally {
                /*try {
                    resultsRef.set(runTestAndGetOutputService.runTestsAndGetOutputs(
                            implementationFilePath, testFilePaths
                    ));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }*/

                System.out.println("Full project rebuild completed successfully.");
                latch.countDown();
            }
        };
        System.out.println("This gets called 2");
        dynamicClassCompilerService.dynamicallyCompileDirectory(directoryPath, compileCallback);
        try {
            // Wait for all compilations to complete
            latch.await();
            ApplicationManager.getApplication().invokeAndWait(() -> {
                System.out.println("Starting FULL project rebuild...");
                compilerManager.rebuild(compileCallback);
            });
            resultsRef.set(runTestAndGetOutputService.runTestsAndGetOutputs(
                    implementationFilePath, testFilePaths
            ));
            return resultsRef.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to compile and run unit tests: " + e.getMessage(), e);
        }
    }

    /*@Override
    public Map<String, Result> compileAndRunUnitTests(String interfaceFilePath, String implementationFilePath, String directoryPath) {
        List<String> testFilePaths = findTestsInDirectoryService.findTestsInDirectory(directoryPath);
        System.out.println("This gets called 1");
        CompilerManager compilerManager = CompilerManager.getInstance(project);
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        CountDownLatch latch = new CountDownLatch(testFilePaths.size()); // +1 for the implementation file +1 for the interface file
        AtomicReference<Map<String, Result>> resultsRef = new AtomicReference<>();
        AtomicReference<Exception> compilationException = new AtomicReference<>();

        ApplicationManager.getApplication().invokeAndWait(
                () -> FileDocumentManager.getInstance().saveAllDocuments(),
                ModalityState.defaultModalityState()
        );

        CompileStatusNotification compileCallback = (aborted, errors, warnings, compileContext) -> {
            try {
                if (aborted) {
                    System.err.println("Compilation aborted.");
                    compilationException.set(new Exception("Compilation aborted."));
                } else if (errors > 0) {
                    System.err.println("Compilation finished with errors.");
                    compilationException.set(new Exception("Compilation finished with errors."));
                } else {
                    System.out.println("Compilation completed successfully with " + warnings + " warnings.");
                }
            } finally {
                try {
                    resultsRef.set(runTestAndGetOutputService.runTestsAndGetOutputs(
                            implementationFilePath, testFilePaths
                    ));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Full project rebuild completed successfully.");
                latch.countDown();
            }
        };
        System.out.println("This gets called 2");
        // Perform a FULL REBUILD

        CompileStatusNotification compileCallback1 = (aborted, errors, warnings, compileContext) -> {
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
                System.out.println("Compilation completed.");
                latch1.countDown(); // Signal that this compilation is complete
            }
        };
        CompileStatusNotification compileCallback2 = (aborted, errors, warnings, compileContext) -> {
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
                System.out.println("Compilation completed.");
                latch2.countDown(); // Signal that this compilation is complete
            }
        };

        // Save all open documents before starting the compilation
        ApplicationManager.getApplication().invokeAndWait(() -> {
            FileDocumentManager.getInstance().saveAllDocuments();
        }, ModalityState.defaultModalityState());

        // Compile the implementation file
        System.out.println("Interface file path: " + interfaceFilePath);
        dynamicClassCompilerService.dynamicallyCompileClass(interfaceFilePath, compileCallback1);

        try {
            latch1.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Compile the implementation file
        System.out.println("Implementation file path: " + implementationFilePath);
        dynamicClassCompilerService.dynamicallyCompileClass(implementationFilePath, compileCallback2);

        try {
            latch2.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Compile all unit test files
        for (String testFilePath : testFilePaths) {
            dynamicClassCompilerService.dynamicallyCompileClass(testFilePath, compileCallback);
        }


        try {
            // Wait for all compilations to complete
            latch.await();
            ApplicationManager.getApplication().invokeAndWait(() -> {
                System.out.println("Starting FULL project rebuild...");
                compilerManager.rebuild(compileCallback);
            });
            resultsRef.set(runTestAndGetOutputService.runTestsAndGetOutputs(
                    implementationFilePath, testFilePaths
            ));
            return resultsRef.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to compile and run unit tests: " + e.getMessage(), e);
        }
    }*/
}
