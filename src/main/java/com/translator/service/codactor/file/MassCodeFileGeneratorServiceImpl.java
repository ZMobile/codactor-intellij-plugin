package com.translator.service.codactor.file;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.translator.dao.inquiry.InquiryDao;
import com.translator.dao.modification.CodeModificationDao;
import com.translator.model.codactor.api.translator.modification.DesktopCodeCreationRequestResource;
import com.translator.model.codactor.api.translator.modification.DesktopCodeCreationResponseResource;
import com.translator.model.codactor.history.HistoricalContextFileModificationHolder;
import com.translator.model.codactor.history.HistoricalContextInquiryHolder;
import com.translator.model.codactor.history.HistoricalContextObjectHolder;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.model.codactor.modification.ModificationType;
import com.translator.model.codactor.modification.RecordType;
import com.translator.service.codactor.connection.AzureConnectionService;
import com.translator.service.codactor.inquiry.InquirySystemMessageGeneratorService;
import com.translator.service.codactor.modification.tracking.FileModificationTrackerService;
import com.translator.service.codactor.connection.DefaultConnectionService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.settings.CodactorConfigurable;
import com.translator.worker.LimitedSwingWorker;
import com.translator.worker.LimitedSwingWorkerExecutor;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MassCodeFileGeneratorServiceImpl implements MassCodeFileGeneratorService {

    private final Project project;
    private final InquiryDao inquiryDao;
    private final CodeModificationDao codeModificationDao;
    private final FileModificationTrackerService fileModificationTrackerService;
    private final DefaultConnectionService defaultConnectionService;
    private final OpenAiModelService openAiModelService;
    private final FileCreatorService fileCreatorService;
    private final InquirySystemMessageGeneratorService inquirySystemMessageGeneratorService;
    private final AzureConnectionService azureConnectionService;

    @Inject
    public MassCodeFileGeneratorServiceImpl(Project project,
                                            InquiryDao inquiryDao,
                                            CodeModificationDao codeModificationDao,
                                            FileModificationTrackerService fileModificationTrackerService,
                                            DefaultConnectionService defaultConnectionService,
                                            OpenAiModelService openAiModelService,
                                            FileCreatorService fileCreatorService,
                                            InquirySystemMessageGeneratorService inquirySystemMessageGeneratorService,
                                            AzureConnectionService azureConnectionService) {
        this.project = project;
        this.inquiryDao = inquiryDao;
        this.codeModificationDao = codeModificationDao;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.defaultConnectionService = defaultConnectionService;
        this.openAiModelService = openAiModelService;
        this.fileCreatorService = fileCreatorService;
        this.inquirySystemMessageGeneratorService = inquirySystemMessageGeneratorService;
        this.azureConnectionService = azureConnectionService;
    }

    @Override
    public void generateCodeFiles(Inquiry inquiry, InquiryChat inquiryChat, String language, String fileExtension, String filePath) {
        String model = openAiModelService.getSelectedOpenAiModel();
        final String newFileExtension;
        if (fileExtension.startsWith(".")) {
            newFileExtension = fileExtension.substring(1);
        } else {
            newFileExtension = fileExtension;
        }
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        String multiFileModificationId = fileModificationTrackerService.addMultiFileModification(inquiry.getId(), language, fileExtension, filePath);
        Task.Backgroundable outerTask = new Task.Backgroundable(project, "Outer Task", true) {
            @Override
            public void run(@NotNull ProgressIndicator outerIndicator) {
                Inquiry newInquiry;
                String question = "What exactly are the names of the ." + newFileExtension + " files that need to be ideally made for this program to work in " + language + "?";
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1/3) Obtaining File Names");
                if (inquiryChat != null) {
                    newInquiry = inquiryDao.continueInquiry(inquiryChat.getId(), question, openAiApiKey, model,  azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model));
                } else {
                    newInquiry = inquiryDao.createInquiry(inquiry.getSubjectRecordId(), inquiry.getSubjectRecordType(), question, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), new ArrayList<>(), inquirySystemMessageGeneratorService.generateDefaultSystemMessage());
                }
                if (newInquiry != null) {
                    if (newInquiry.getError() != null) {
                        JOptionPane.showMessageDialog(null, newInquiry.getError(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    InquiryChat mostRecentInquiryChat1 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/3) Obtaining Terminal Commands");
                    String question2 = "Can you provide the terminal commands for creating these " + language + " files in " + filePath + "?";
                    newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat1.getId(), question2, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model));
                    if (newInquiry != null) {
                        if (newInquiry.getError() != null) {
                            JOptionPane.showMessageDialog(null, newInquiry, "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        InquiryChat mostRecentInquiryChat2 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        if (mostRecentInquiryChat2 == null) {
                            newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat1.getId(), question2, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model));
                            if (newInquiry.getError() != null) {
                                JOptionPane.showMessageDialog(null, newInquiry, "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            mostRecentInquiryChat2 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        }
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files...");

                        List<File> newFiles = fileCreatorService.createFilesFromInput(filePath, mostRecentInquiryChat2.getMessage());
                        for (int i = 0; i < newFiles.size(); i++) {
                            File file = newFiles.get(i);
                            if (file != null) {
                                Inquiry finalNewInquiry = newInquiry;
                                int finalI = i;
                                InquiryChat finalMostRecentInquiryChat = mostRecentInquiryChat2;
                                Task.Backgroundable subTask1 = new Task.Backgroundable(project, "Subtask " + i, false) {
                                    @Override
                                    public void run(@NotNull ProgressIndicator indicator) {
                                        int fileNumber = finalI + 1;
                                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files... (" + fileNumber + "/" + newFiles.size() + ")");
                                        List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                        HistoricalContextInquiryHolder inquiryContext = new HistoricalContextInquiryHolder(finalNewInquiry.getId(), mostRecentInquiryChat1.getId(), null, false, null);
                                        HistoricalContextObjectHolder priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                                        priorContext.add(priorContextObject);
                                        String modificationId = fileModificationTrackerService.addModification(file.getAbsolutePath(), finalMostRecentInquiryChat.getMessage(), 0, 0, ModificationType.CREATE, priorContext);
                                        String description = "The complete and comprehensive " + language + " code for " + file.getName();
                                        DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(file.getAbsolutePath(), description, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
                                        DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationDao.getCreatedCode(desktopCodeCreationRequestResource);
                                        if (desktopCodeCreationResponseResource.getModificationSuggestions() != null) {
                                            fileModificationTrackerService.implementModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), true);
                                            //write the contents to the file with printWriter:
                                           // try (PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
                                                //out.println(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode());
                                            //} catch (FileNotFoundException e) {
                                                //e.printStackTrace();
                                            //}
                                        } else {
                                            if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                                                CodactorConfigurable configurable = new CodactorConfigurable();
                                                ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
                                            } else {
                                                JOptionPane.showMessageDialog(null, desktopCodeCreationResponseResource.getError(), "Error",
                                                        JOptionPane.ERROR_MESSAGE);
                                            }
                                            fileModificationTrackerService.removeModification(modificationId);
                                        }
                                    }
                                };
                                ProgressManager.getInstance().runProcessWithProgressAsynchronously(subTask1, outerIndicator);
                                try {
                                    Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                                } catch (InterruptedException e) {
                                    // Handle the interruption
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                fileModificationTrackerService.removeMultiFileModification(multiFileModificationId);
            }
        };

        ProgressManager.getInstance().run(outerTask);
    }

    @Override
    public void generateCodeFilesWithConsideration(Inquiry inquiry, InquiryChat inquiryChat, String language, String fileExtension, String filePath) {
        String model = openAiModelService.getSelectedOpenAiModel();
        final String newFileExtension;
        if (fileExtension.startsWith(".")) {
            newFileExtension = fileExtension.substring(1);
        } else {
            newFileExtension = fileExtension;
        }
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        String multiFileModificationId = fileModificationTrackerService.addMultiFileModification(inquiry.getId(), language, fileExtension, filePath);
        Task.Backgroundable backgroundTask = new Task.Backgroundable(project, "Task", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Inquiry newInquiry;
                String question = "What exactly are the names of the ." + newFileExtension + " files that need to be ideally made for this program to work in " + language + "?";
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1/3) Obtaining File Names");
                if (inquiryChat != null) {
                    newInquiry = inquiryDao.continueInquiry(inquiryChat.getId(), question, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model));
                } else {
                    newInquiry = inquiryDao.createInquiry(inquiry.getSubjectRecordId(), inquiry.getSubjectRecordType(), question, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), new ArrayList<>(), inquirySystemMessageGeneratorService.generateDefaultSystemMessage());
                }
                if (newInquiry != null) {
                    if (newInquiry.getError() != null) {
                        JOptionPane.showMessageDialog(null, newInquiry.getError(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    InquiryChat mostRecentInquiryChat1 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1.5/3) Ordering File Names");
                    String question2 = "What would be the ideal order to create these files such that I make the simplest ones first, and then the more complex ones that depend on the previous ones later?";
                    try {
                        Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                    } catch (InterruptedException e) {
                        // Handle the interruption
                        e.printStackTrace();
                    }
                    newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat1.getId(), question2, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model));
                    if (newInquiry != null) {
                        if (newInquiry.getError() != null) {
                            JOptionPane.showMessageDialog(null, newInquiry, "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        InquiryChat mostRecentInquiryChat2 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/3) Obtaining Terminal Commands");
                        String question3 = "Can you provide the terminal commands for creating those " + language + " files in " + filePath + "? Can you provide them in the order you specified above?";
                        try {
                            Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                        } catch (InterruptedException e) {
                            // Handle the interruption
                            e.printStackTrace();
                        }
                        newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat2.getId(), question3, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model));
                        if (newInquiry != null) {
                            if (newInquiry.getError() != null) {
                                JOptionPane.showMessageDialog(null, newInquiry, "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            InquiryChat mostRecentInquiryChat3 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                            fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files...");
                            List<File> newFiles = fileCreatorService.createFilesFromInput(filePath, mostRecentInquiryChat3.getMessage());
                            List<String> completedSuggestionIds = new ArrayList<>();
                            for (int i = 0; i < newFiles.size(); i++) {
                                File file = newFiles.get(i);
                                if (file != null) {
                                    String filePath = file.getAbsolutePath();
                                    if (!filePath.contains(".")) {
                                        filePath = file.getName();
                                    }
                                    List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                    HistoricalContextInquiryHolder inquiryContext = new HistoricalContextInquiryHolder(newInquiry.getId(), mostRecentInquiryChat1.getId(), null, false, null);
                                    HistoricalContextObjectHolder priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                                    priorContext.add(priorContextObject);
                                    for (String completedSuggestionId : completedSuggestionIds) {
                                        HistoricalContextFileModificationHolder modificationContext = new HistoricalContextFileModificationHolder(completedSuggestionId, RecordType.FILE_MODIFICATION_SUGGESTION, false, null);
                                        HistoricalContextObjectHolder priorContextObject2 = new HistoricalContextObjectHolder(modificationContext);
                                        priorContext.add(priorContextObject2);
                                    }
                                    String modificationId = fileModificationTrackerService.addModification(filePath, mostRecentInquiryChat3.getMessage(), 0, 0, ModificationType.CREATE, priorContext);
                                    int fileNumber = i + 1;
                                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files... (" + fileNumber + "/" + newFiles.size() + ")");
                                    String description = "The complete and comprehensive " + language + " code for " + file.getName();
                                    DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(file.getAbsolutePath(), description, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext);
                                    DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationDao.getCreatedCode(desktopCodeCreationRequestResource);
                                    if (desktopCodeCreationResponseResource.getModificationSuggestions() != null) {
                                        fileModificationTrackerService.implementModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), true);
                                        //write the contents to the file with printWriter:
                                        //try (PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
                                            //out.println(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode());
                                        //} catch (FileNotFoundException e) {
                                            //e.printStackTrace();
                                        //}
                                        completedSuggestionIds.add(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getId());
                                    } else {
                                        if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                                            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
                                        } else {
                                            JOptionPane.showMessageDialog(null, desktopCodeCreationResponseResource.getError(), "Error",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                        fileModificationTrackerService.removeModification(modificationId);
                                    }
                                    try {
                                        Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                                    } catch (InterruptedException e) {
                                        // Handle the interruption
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                fileModificationTrackerService.removeMultiFileModification(multiFileModificationId);
            }
        };

        ProgressManager.getInstance().run(backgroundTask);
    }

    @Override
    public void generateCodeFiles(String description, String language, String fileExtension, String filePath, List<HistoricalContextObjectHolder> priorContext) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (priorContext == null) {
            priorContext = new ArrayList<>();
        }
        final String newFileExtension;
        if (fileExtension.startsWith(".")) {
            newFileExtension = fileExtension.substring(1);
        } else {
            newFileExtension = fileExtension;
        }
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        String multiFileModificationId = fileModificationTrackerService.addMultiFileModification(description, language, fileExtension, filePath);
        List<HistoricalContextObjectHolder> finalPriorContext = priorContext;
        LimitedSwingWorker worker = new LimitedSwingWorker(new LimitedSwingWorkerExecutor()) {
            @Override
            protected Void doInBackground() {
                String question = "I need to create a potentially multi-file " + language + " program with the following description: \"" + description + "\".  What exactly are the names of the ." + newFileExtension + " files that need to be ideally made for this program to work in " + language + "?";
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1/3) Obtaining File Names");
                Inquiry newInquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), finalPriorContext, inquirySystemMessageGeneratorService.generateDefaultSystemMessage());
                if (newInquiry != null) {
                    if (newInquiry.getError() != null) {
                        JOptionPane.showMessageDialog(null, newInquiry.getError(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    InquiryChat mostRecentInquiryChat1 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/3) Obtaining Terminal Commands");
                    String question2 = "Can you provide the terminal commands for creating those " + language + " files in " + filePath + "?";
                    try {
                        Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                    } catch (InterruptedException e) {
                        // Handle the interruption
                        e.printStackTrace();
                    }
                    newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat1.getId(), question2, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model));
                    if (newInquiry != null) {
                        if (newInquiry.getError() != null) {
                            JOptionPane.showMessageDialog(null, newInquiry, "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        InquiryChat mostRecentInquiryChat2 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        if (mostRecentInquiryChat2 == null) {
                            newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat1.getId(), question2, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model));
                            if (newInquiry.getError() != null) {
                                JOptionPane.showMessageDialog(null, newInquiry, "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            mostRecentInquiryChat2 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        }
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files...");

                        List<File> newFiles = fileCreatorService.createFilesFromInput(filePath, mostRecentInquiryChat2.getMessage());
                        for (int i = 0; i < newFiles.size(); i++) {
                            File file = newFiles.get(i);
                            if (file != null) {
                                String newFilePath = file.getAbsolutePath();
                                Inquiry finalNewInquiry = newInquiry;
                                int finalI = i;
                                LimitedSwingWorker worker2 = new LimitedSwingWorker(new LimitedSwingWorkerExecutor()) {
                                    @Override
                                    protected Void doInBackground() {
                                        int fileNumber = finalI + 1;
                                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files... (" + fileNumber + "/" + newFiles.size() + ")");
                                        List<HistoricalContextObjectHolder> newPriorContext = new ArrayList<>();
                                        HistoricalContextInquiryHolder inquiryContext = new HistoricalContextInquiryHolder(finalNewInquiry.getId(), mostRecentInquiryChat1.getId(), null, false, null);
                                        HistoricalContextObjectHolder priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                                        newPriorContext.add(priorContextObject);
                                        String modificationId = fileModificationTrackerService.addModification(newFilePath, description, 0, 0, ModificationType.CREATE, newPriorContext);
                                        String newDescription = "The complete and comprehensive " + language + " code for " + file.getName();
                                        DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(file.getAbsolutePath(), newDescription, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), newPriorContext);
                                        DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationDao.getCreatedCode(desktopCodeCreationRequestResource);
                                        if (desktopCodeCreationResponseResource.getModificationSuggestions() != null) {
                                            fileModificationTrackerService.implementModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), true);
                                            //write the contents to the file with printWriter:
                                            //try (PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
                                                //out.println(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode());
                                            //} catch (FileNotFoundException e) {
                                                //e.printStackTrace();
                                            //}
                                        } else {
                                            if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                                                CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
                                            } else {
                                                JOptionPane.showMessageDialog(null, desktopCodeCreationResponseResource.getError(), "Error",
                                                        JOptionPane.ERROR_MESSAGE);
                                            }
                                            fileModificationTrackerService.removeModification(modificationId);
                                        }
                                        return null;
                                    }
                                };

                                worker2.execute();

                                try {
                                    Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                                } catch (InterruptedException e) {
                                    // Handle the interruption
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                fileModificationTrackerService.removeMultiFileModification(multiFileModificationId);
                return null;
            }
        };
        worker.execute();
    }

    @Override
    public void generateCodeFilesWithConsideration(String description, String language, String fileExtension, String filePath, List<HistoricalContextObjectHolder> priorContext) {
        String model = openAiModelService.getSelectedOpenAiModel();
        if (priorContext == null) {
            priorContext = new ArrayList<>();
        }
        final String newFileExtension;
        if (fileExtension.startsWith(".")) {
            newFileExtension = fileExtension.substring(1);
        } else {
            newFileExtension = fileExtension;
        }
        String openAiApiKey;
            if (azureConnectionService.isAzureConnected()) {
                openAiApiKey = azureConnectionService.getKey();
            } else {
                openAiApiKey = defaultConnectionService.getOpenAiApiKey();
            }
        String multiFileModificationId = fileModificationTrackerService.addMultiFileModification(description, language, fileExtension, filePath);
        List<HistoricalContextObjectHolder> finalPriorContext = priorContext;
        LimitedSwingWorker worker = new LimitedSwingWorker(new LimitedSwingWorkerExecutor()) {
            @Override
            protected Void doInBackground() {
                String question = "I need to create a potentially multi-file " + language + " program with the following description: \"" + description + "\".  What exactly are the names of the ." + newFileExtension + " files that need to be ideally made for this program to work in " + language + "?";
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1/3) Obtaining File Names");
                Inquiry newInquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), finalPriorContext, inquirySystemMessageGeneratorService.generateDefaultSystemMessage());
                if (newInquiry != null) {
                    if (newInquiry.getError() != null) {
                        JOptionPane.showMessageDialog(null, newInquiry.getError(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    InquiryChat mostRecentInquiryChat1 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1.5/3) Ordering File Names");
                    String question2 = "What would be the ideal order to create these files such that I make the simplest ones first, and then the more complex ones that depend on the previous ones later?";
                    try {
                        Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                    } catch (InterruptedException e) {
                        // Handle the interruption
                        e.printStackTrace();
                    }
                    newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat1.getId(), question2, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model));
                    if (newInquiry != null) {
                        if (newInquiry.getError() != null) {
                            JOptionPane.showMessageDialog(null, newInquiry.getError(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        InquiryChat mostRecentInquiryChat2 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/3) Obtaining Terminal Commands");
                        String question3 = "Can you provide the terminal commands for creating those " + language + " files in " + filePath + "? Can you provide them in the order you specified above?";
                        try {
                            Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                        } catch (InterruptedException e) {
                            // Handle the interruption
                            e.printStackTrace();
                        }
                        newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat2.getId(), question3, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model));
                        if (newInquiry != null) {
                            if (newInquiry.getError() != null) {
                                JOptionPane.showMessageDialog(null, newInquiry.getError(), "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            InquiryChat mostRecentInquiryChat3 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                            fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files...");
                            List<File> newFiles = fileCreatorService.createFilesFromInput(filePath, mostRecentInquiryChat3.getMessage());
                            List<String> completedSuggestionIds = new ArrayList<>();
                            for (int i = 0; i < newFiles.size(); i++) {
                                File file = newFiles.get(i);
                                if (file != null) {
                                    String newFilePath = file.getAbsolutePath();
                                    int fileNumber = i + 1;
                                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files... (" + fileNumber + "/" + newFiles.size() + ")");
                                    List<HistoricalContextObjectHolder> priorContext2 = new ArrayList<>();
                                    HistoricalContextInquiryHolder inquiryContext = new HistoricalContextInquiryHolder(newInquiry.getId(), mostRecentInquiryChat1.getId(), null, true, null);
                                    HistoricalContextObjectHolder priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                                    priorContext2.add(priorContextObject);
                                    for (String completedSuggestionId : completedSuggestionIds) {
                                        HistoricalContextFileModificationHolder modificationContext = new HistoricalContextFileModificationHolder(completedSuggestionId, RecordType.FILE_MODIFICATION_SUGGESTION, false, null);
                                        HistoricalContextObjectHolder priorContextObject2 = new HistoricalContextObjectHolder(modificationContext);
                                        priorContext2.add(priorContextObject2);
                                    }
                                    String modificationId = fileModificationTrackerService.addModification(newFilePath, description, 0, 0, ModificationType.CREATE, finalPriorContext);
                                    String description2 = "The complete and comprehensive " + language + " code for " + file.getName();
                                    DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(file.getAbsolutePath(), description2, openAiApiKey, model, azureConnectionService.isAzureConnected(), azureConnectionService.getResource(), azureConnectionService.getDeploymentForModel(model), priorContext2);
                                    DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationDao.getCreatedCode(desktopCodeCreationRequestResource);
                                    if (desktopCodeCreationResponseResource.getModificationSuggestions() != null) {
                                        fileModificationTrackerService.implementModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode(), true);
                                        //write the contents to the file with printWriter:
                                        //Check if the file contents are empty first:
                                        /////
                                        //try (PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
                                            //out.println(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode());
                                            //System.out.println("Testo 9");
                                        //} catch (FileNotFoundException e) {
                                            //e.printStackTrace();
                                        //}
                                        completedSuggestionIds.add(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getId());
                                    } else {
                                        if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                                            CodactorConfigurable configurable = new CodactorConfigurable();
            ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
                                        } else {
                                            JOptionPane.showMessageDialog(null, desktopCodeCreationResponseResource.getError(), "Error",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                        fileModificationTrackerService.removeModification(modificationId);
                                    }
                                    try {
                                        Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                                    } catch (InterruptedException e) {
                                        // Handle the interruption
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                fileModificationTrackerService.removeMultiFileModification(multiFileModificationId);
                return null;
            }
        };
        worker.execute();
    }
}
