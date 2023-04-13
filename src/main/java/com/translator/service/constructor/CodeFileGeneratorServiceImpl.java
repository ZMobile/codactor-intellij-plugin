package com.translator.service.constructor;

import com.translator.dao.inquiry.InquiryDao;
import com.translator.model.api.translator.modification.DesktopCodeCreationRequestResource;
import com.translator.model.api.translator.modification.DesktopCodeCreationResponseResource;
import com.translator.model.history.HistoricalContextInquiryHolder;
import com.translator.model.history.HistoricalContextModificationHolder;
import com.translator.model.history.HistoricalContextObjectHolder;
import com.translator.model.inquiry.Inquiry;
import com.translator.model.inquiry.InquiryChat;
import com.translator.model.modification.ModificationType;
import com.translator.model.modification.RecordType;
import com.translator.service.file.FileCreatorService;
import com.translator.service.modification.CodeModificationService;
import com.translator.service.modification.tracking.FileModificationTrackerService;
import com.translator.service.openai.OpenAiApiKeyService;
import com.translator.service.openai.OpenAiModelService;
import com.translator.view.dialog.OpenAiApiKeyDialog;
import com.translator.worker.LimitedSwingWorker;
import com.translator.worker.LimitedSwingWorkerExecutor;

import javax.inject.Inject;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CodeFileGeneratorServiceImpl implements CodeFileGeneratorService {
    private final InquiryDao inquiryDao;
    private final CodeModificationService codeModificationService;
    private final FileModificationTrackerService fileModificationTrackerService;
    private final OpenAiApiKeyService openAiApiKeyService;
    private final OpenAiModelService openAiModelService;
    private final FileCreatorService fileCreatorService;
    private final LimitedSwingWorkerExecutor inquiryTaskExecutor;
    private final LimitedSwingWorkerExecutor aiTaskExecutor;

    @Inject
    public CodeFileGeneratorServiceImpl(InquiryDao inquiryDao,
                                        CodeModificationService codeModificationService,
                                        FileModificationTrackerService fileModificationTrackerService,
                                        OpenAiApiKeyService openAiApiKeyService,
                                        OpenAiModelService openAiModelService,
                                        FileCreatorService fileCreatorService,
                                        LimitedSwingWorkerExecutor inquiryTaskExecutor,
                                        LimitedSwingWorkerExecutor aiTaskExecutor) {
        this.inquiryDao = inquiryDao;
        this.codeModificationService = codeModificationService;
        this.fileModificationTrackerService = fileModificationTrackerService;
        this.openAiApiKeyService = openAiApiKeyService;
        this.openAiModelService = openAiModelService;
        this.fileCreatorService = fileCreatorService;
        this.inquiryTaskExecutor = inquiryTaskExecutor;
        this.aiTaskExecutor = aiTaskExecutor;
    }

    @Override
    public void generateCodeFiles(Inquiry inquiry, InquiryChat inquiryChat, String language, String fileExtension, String filePath) {
        final String newFileExtension;
        if (fileExtension.startsWith(".")) {
            newFileExtension = fileExtension.substring(1);
        } else {
            newFileExtension = fileExtension;
        }
        String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
        String multiFileModificationId = fileModificationTrackerService.addMultiFileModification(inquiry.getId(), language, fileExtension, filePath);
        LimitedSwingWorker worker = new LimitedSwingWorker(inquiryTaskExecutor) {
            @Override
            protected Void doInBackground() {
                Inquiry newInquiry;
                String question = "What exactly are the names of the ." + newFileExtension + " files that need to be ideally made for this program to work in " + language + "?";
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1/3) Obtaining File Names");
                if (inquiryChat != null) {
                    newInquiry = inquiryDao.continueInquiry(inquiryChat.getId(), question, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                } else {
                    newInquiry = inquiryDao.createInquiry(inquiry.getSubjectRecordId(), inquiry.getSubjectRecordType(), question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), new ArrayList<>());
                }
                if (newInquiry != null) {
                    InquiryChat mostRecentInquiryChat1 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/3) Obtaining Terminal Commands");
                    String question2 = "Can you provide the terminal commands for creating these " + language + " files in " + filePath + "?";
                    newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat1.getId(), question2, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                    if (newInquiry != null) {
                        InquiryChat mostRecentInquiryChat2 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        if (mostRecentInquiryChat2 == null) {
                            newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat1.getId(), question2, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                            mostRecentInquiryChat2 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        }
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files...");
                        List<File> newFiles = fileCreatorService.createFilesFromInput(filePath, mostRecentInquiryChat2.getMessage());
                        for (int i = 0; i < newFiles.size(); i++) {
                            File file = newFiles.get(i);
                            if (file != null) {
                                String modificationId = fileModificationTrackerService.addModification(file.getAbsolutePath(), 0, 0, ModificationType.CREATE);
                                Inquiry finalNewInquiry = newInquiry;
                                int finalI = i;
                                LimitedSwingWorker worker2 = new LimitedSwingWorker(aiTaskExecutor) {
                                    @Override
                                    protected Void doInBackground() {
                                        int fileNumber = finalI + 1;
                                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files... (" + fileNumber + "/" + newFiles.size() + ")");
                                        List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                        HistoricalContextInquiryHolder inquiryContext = new HistoricalContextInquiryHolder(finalNewInquiry.getId(), mostRecentInquiryChat1.getId(), null, false, null);
                                        HistoricalContextObjectHolder priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                                        priorContext.add(priorContextObject);
                                        String description = "The " + language + " code for " + file.getName();
                                        DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(file.getAbsolutePath(), description, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                        DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationService.getCreatedCode(desktopCodeCreationRequestResource);
                                        if (desktopCodeCreationResponseResource.getModificationSuggestions() != null) {
                                            fileModificationTrackerService.implementModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode().trim());
                                            //write the contents to the file with printWriter:
                                            try (PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
                                                out.println(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode().trim());
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                                                OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                                openAiApiKeyDialog.setVisible(true);
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
    public void generateCodeFilesWithConsideration(Inquiry inquiry, InquiryChat inquiryChat, String language, String fileExtension, String filePath) {
        final String newFileExtension;
        if (fileExtension.startsWith(".")) {
            newFileExtension = fileExtension.substring(1);
        } else {
            newFileExtension = fileExtension;
        }
        String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
        String multiFileModificationId = fileModificationTrackerService.addMultiFileModification(inquiry.getId(), language, fileExtension, filePath);
        LimitedSwingWorker worker = new LimitedSwingWorker(inquiryTaskExecutor) {
            @Override
            protected Void doInBackground() {
                Inquiry newInquiry;
                String question = "What exactly are the names of the ." + newFileExtension + " files that need to be ideally made for this program to work in " + language + "?";
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1/3) Obtaining File Names");
                if (inquiryChat != null) {
                    newInquiry = inquiryDao.continueInquiry(inquiryChat.getId(), question, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                } else {
                    newInquiry = inquiryDao.createInquiry(inquiry.getSubjectRecordId(), inquiry.getSubjectRecordType(), question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), new ArrayList<>());
                }
                if (newInquiry != null) {
                    InquiryChat mostRecentInquiryChat1 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1.5/3) Ordering File Names");
                    String question2 = "What would be the ideal order to create these files such that I make the simplest ones first, and then the more complex ones that depend on the previous ones later?";
                    try {
                        Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                    } catch (InterruptedException e) {
                        // Handle the interruption
                        e.printStackTrace();
                    }
                    newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat1.getId(), question2, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                    if (newInquiry != null) {
                        InquiryChat mostRecentInquiryChat2 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/3) Obtaining Terminal Commands");
                        String question3 = "Can you provide the terminal commands for creating those " + language + " files in " + filePath + "?";
                        try {
                            Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                        } catch (InterruptedException e) {
                            // Handle the interruption
                            e.printStackTrace();
                        }
                        newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat2.getId(), question3, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                        if (newInquiry != null) {
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
                                    String modificationId = fileModificationTrackerService.addModification(filePath, 0, 0, ModificationType.CREATE);
                                    int fileNumber = i + 1;
                                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files... (" + fileNumber + "/" + newFiles.size() + ")");
                                    List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                    HistoricalContextInquiryHolder inquiryContext = new HistoricalContextInquiryHolder(newInquiry.getId(), mostRecentInquiryChat1.getId(), null, false, null);
                                    HistoricalContextObjectHolder priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                                    priorContext.add(priorContextObject);
                                    for (String completedSuggestionId : completedSuggestionIds) {
                                        HistoricalContextModificationHolder modificationContext = new HistoricalContextModificationHolder(completedSuggestionId, RecordType.FILE_MODIFICATION_SUGGESTION, false, null);
                                        HistoricalContextObjectHolder priorContextObject2 = new HistoricalContextObjectHolder(modificationContext);
                                        priorContext.add(priorContextObject2);
                                    }
                                    String description = "The " + language + " code for " + file.getName();
                                    DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(file.getAbsolutePath(), description, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                    DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationService.getCreatedCode(desktopCodeCreationRequestResource);
                                    if (desktopCodeCreationResponseResource.getModificationSuggestions() != null) {
                                        fileModificationTrackerService.implementModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode().trim());
                                        //write the contents to the file with printWriter:
                                        try (PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
                                            out.println(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode().trim());
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        completedSuggestionIds.add(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getId());
                                    } else {
                                        if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                                            OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                            openAiApiKeyDialog.setVisible(true);
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

    @Override
    public void generateCodeFiles(String description, String language, String fileExtension, String filePath, List<HistoricalContextObjectHolder> priorContext) {
        final String newFileExtension;
        if (fileExtension.startsWith(".")) {
            newFileExtension = fileExtension.substring(1);
        } else {
            newFileExtension = fileExtension;
        }
        String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
        String multiFileModificationId = fileModificationTrackerService.addMultiFileModification(description, language, fileExtension, filePath);
        LimitedSwingWorker worker = new LimitedSwingWorker(inquiryTaskExecutor) {
            @Override
            protected Void doInBackground() {
                String question = "I need to create a potentially multi-file " + language + " program with the following description: \"" + description + "\".  What exactly are the names of the ." + newFileExtension + " files that need to be ideally made for this program to work in " + language + "?";
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1/3) Obtaining File Names");
                Inquiry newInquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                if (newInquiry != null) {
                    InquiryChat mostRecentInquiryChat1 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/3) Obtaining Terminal Commands");
                    String question2 = "Can you provide the terminal commands for creating those " + language + " files in " + filePath + "?";
                    try {
                        Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                    } catch (InterruptedException e) {
                        // Handle the interruption
                        e.printStackTrace();
                    }
                    newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat1.getId(), question2, openAiApiKey, openAiModelService.getSelectedOpenAiModel());

                    if (newInquiry != null) {
                        InquiryChat mostRecentInquiryChat2 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        if (mostRecentInquiryChat2 == null) {
                            newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat1.getId(), question2, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                            mostRecentInquiryChat2 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        }
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files...");
                        List<File> newFiles = fileCreatorService.createFilesFromInput(filePath, mostRecentInquiryChat2.getMessage());
                        for (int i = 0; i < newFiles.size(); i++) {
                            File file = newFiles.get(i);
                            if (file != null) {
                                String filePath = file.getAbsolutePath();
                                if (!filePath.contains(".")) {
                                    filePath = file.getName();
                                }
                                String modificationId = fileModificationTrackerService.addModification(filePath, 0, 0, ModificationType.CREATE);
                                Inquiry finalNewInquiry = newInquiry;
                                int finalI = i;
                                LimitedSwingWorker worker2 = new LimitedSwingWorker(aiTaskExecutor) {
                                    @Override
                                    protected Void doInBackground() {
                                        int fileNumber = finalI + 1;
                                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files... (" + fileNumber + "/" + newFiles.size() + ")");
                                        List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                        HistoricalContextInquiryHolder inquiryContext = new HistoricalContextInquiryHolder(finalNewInquiry.getId(), mostRecentInquiryChat1.getId(), null, false, null);
                                        HistoricalContextObjectHolder priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                                        priorContext.add(priorContextObject);
                                        String description = "The " + language + " code for " + file.getName();
                                        DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(file.getAbsolutePath(), description, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                        DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationService.getCreatedCode(desktopCodeCreationRequestResource);
                                        if (desktopCodeCreationResponseResource.getModificationSuggestions() != null) {
                                            fileModificationTrackerService.implementModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode().trim());
                                            //write the contents to the file with printWriter:
                                            try (PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
                                                out.println(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode().trim());
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                                                OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                                openAiApiKeyDialog.setVisible(true);
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
        final String newFileExtension;
        if (fileExtension.startsWith(".")) {
            newFileExtension = fileExtension.substring(1);
        } else {
            newFileExtension = fileExtension;
        }
        String openAiApiKey = openAiApiKeyService.getOpenAiApiKey();
        String multiFileModificationId = fileModificationTrackerService.addMultiFileModification(description, language, fileExtension, filePath);
        LimitedSwingWorker worker = new LimitedSwingWorker(inquiryTaskExecutor) {
            @Override
            protected Void doInBackground() {
                String question = "I need to create a potentially multi-file " + language + " program with the following description: \"" + description + "\".  What exactly are the names of the ." + newFileExtension + " files that need to be ideally made for this program to work in " + language + "?";
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1/3) Obtaining File Names");
                Inquiry newInquiry = inquiryDao.createGeneralInquiry(question, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                if (newInquiry != null) {
                    InquiryChat mostRecentInquiryChat1 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(1.5/3) Ordering File Names");
                    String question2 = "What would be the ideal order to create these files such that I make the simplest ones first, and then the more complex ones that depend on the previous ones later?";
                    try {
                        Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                    } catch (InterruptedException e) {
                        // Handle the interruption
                        e.printStackTrace();
                    }
                    newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat1.getId(), question2, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                    if (newInquiry != null) {
                        InquiryChat mostRecentInquiryChat2 = newInquiry.getChats().get(newInquiry.getChats().size() - 1);
                        fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(2/3) Obtaining Terminal Commands");
                        String question3 = "Can you provide the terminal commands for creating those " + language + " files in " + filePath + "?";
                        try {
                            Thread.sleep(1000); // Wait for 1 second (1000 milliseconds)
                        } catch (InterruptedException e) {
                            // Handle the interruption
                            e.printStackTrace();
                        }
                        newInquiry = inquiryDao.continueInquiry(mostRecentInquiryChat2.getId(), question3, openAiApiKey, openAiModelService.getSelectedOpenAiModel());
                        if (newInquiry != null) {
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
                                    String modificationId = fileModificationTrackerService.addModification(filePath, 0, 0, ModificationType.CREATE);
                                    int fileNumber = i + 1;
                                    fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files... (" + fileNumber + "/" + newFiles.size() + ")");
                                    List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                                    HistoricalContextInquiryHolder inquiryContext = new HistoricalContextInquiryHolder(newInquiry.getId(), mostRecentInquiryChat1.getId(), null, true, null);
                                    HistoricalContextObjectHolder priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                                    priorContext.add(priorContextObject);
                                    for (String completedSuggestionId : completedSuggestionIds) {
                                        HistoricalContextModificationHolder modificationContext = new HistoricalContextModificationHolder(completedSuggestionId, RecordType.FILE_MODIFICATION_SUGGESTION, false, null);
                                        HistoricalContextObjectHolder priorContextObject2 = new HistoricalContextObjectHolder(modificationContext);
                                        priorContext.add(priorContextObject2);
                                    }
                                    String description = "The " + language + " code for " + file.getName();
                                    DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(file.getAbsolutePath(), description, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                                    DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationService.getCreatedCode(desktopCodeCreationRequestResource);
                                    if (desktopCodeCreationResponseResource.getModificationSuggestions() != null) {
                                        fileModificationTrackerService.implementModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode().trim());
                                        //write the contents to the file with printWriter:
                                        try (PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
                                            out.println(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode().trim());
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        completedSuggestionIds.add(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getId());
                                    } else {
                                        if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                                            OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                                            openAiApiKeyDialog.setVisible(true);
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

    private void generateFiles(List<File> files, List<HistoricalContextObjectHolder> priorContext) {
        /*for (int i = 0; i < newFiles.size(); i++) {
            File file = newFiles.get(i);
            if (file != null) {
                String filePath = file.getAbsolutePath();
                if (!filePath.contains(".")) {
                    filePath = file.getName();
                }
                String modificationId = fileModificationTrackerService.addModification(filePath, 0, 0, ModificationType.CREATE);
                int fileNumber = i + 1;
                fileModificationTrackerService.setMultiFileModificationStage(multiFileModificationId, "(3/3) Creating Files... (" + fileNumber + "/" + newFiles.size() + ")");
                List<HistoricalContextObjectHolder> priorContext = new ArrayList<>();
                HistoricalContextInquiryHolder inquiryContext = new HistoricalContextInquiryHolder(newInquiry.getId(), mostRecentInquiryChat1.getId(), null, true, null);
                HistoricalContextObjectHolder priorContextObject = new HistoricalContextObjectHolder(inquiryContext);
                priorContext.add(priorContextObject);
                for (String completedSuggestionId : completedSuggestionIds) {
                    HistoricalContextModificationHolder modificationContext = new HistoricalContextModificationHolder(completedSuggestionId, RecordType.FILE_MODIFICATION_SUGGESTION, false, null);
                    HistoricalContextObjectHolder priorContextObject2 = new HistoricalContextObjectHolder(modificationContext);
                    priorContext.add(priorContextObject2);
                }
                String description = "The " + language + " code for " + file.getName();
                DesktopCodeCreationRequestResource desktopCodeCreationRequestResource = new DesktopCodeCreationRequestResource(file.getAbsolutePath(), description, openAiApiKey, openAiModelService.getSelectedOpenAiModel(), priorContext);
                DesktopCodeCreationResponseResource desktopCodeCreationResponseResource = codeModificationService.getCreatedCode(desktopCodeCreationRequestResource);
                if (desktopCodeCreationResponseResource.getModificationSuggestions() != null) {
                    fileModificationTrackerService.implementModificationUpdate(modificationId, desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode().trim());
                    //write the contents to the file with printWriter:
                    try (PrintWriter out = new PrintWriter(file.getAbsolutePath())) {
                        out.println(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getSuggestedCode().trim());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    completedSuggestionIds.add(desktopCodeCreationResponseResource.getModificationSuggestions().get(0).getId());
                } else {
                    if (desktopCodeCreationResponseResource.getError().equals("null: null")) {
                        OpenAiApiKeyDialog openAiApiKeyDialog = new OpenAiApiKeyDialog(openAiApiKeyService);
                        openAiApiKeyDialog.setVisible(true);
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
        }*/
    }
}
