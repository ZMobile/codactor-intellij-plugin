package com.translator.view.codactor.viewer.inquiry;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.translator.model.TestoObject;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.model.codactor.inquiry.InquiryChatType;
import com.translator.service.codactor.context.PromptContextService;
import com.translator.service.codactor.factory.PromptContextServiceFactory;
import com.translator.service.codactor.inquiry.functions.InquiryChatListFunctionCallCompressorService;
import com.translator.service.codactor.inquiry.functions.InquiryFunctionCallProcessorService;
import com.translator.service.codactor.openai.OpenAiModelService;
import com.translator.service.codactor.openai.OpenAiModelServiceImpl;
import com.translator.service.codactor.ui.measure.TextAreaHeightCalculatorService;
import com.translator.service.codactor.ui.tool.CodactorToolWindowService;
import com.translator.view.codactor.dialog.MultiFileCreateDialog;
import com.translator.view.codactor.factory.dialog.MultiFileCreateDialogFactory;
import com.translator.view.codactor.menu.TextAreaWindow;
import com.translator.view.codactor.panel.FixedHeightPanel;
import com.translator.view.codactor.renderer.InquiryChatRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class InquiryChatListViewer extends JPanel {
    private Gson gson;
    private Inquiry inquiry;
    private InquiryViewer inquiryViewer;
    private JList<InquiryChatViewer> inquiryChatList;
    private JBScrollPane jBScrollPane;
    private ListSelectionListener listSelectionListener;
    private int selectedChat = -1;
    private int lastSelectedChat = -1;
    private JToolBar jToolBar;
    private JBMenuItem editItem;
    private JBMenuItem regenerateItem;
    private JBMenuItem previousChat;
    private JBMenuItem nextChat;
    private JBMenuItem autoGenerate;
    private TextAreaHeightCalculatorService textAreaHeightCalculatorService;
    private OpenAiModelService openAiModelService;
    private InquiryChatListFunctionCallCompressorService inquiryChatListFunctionCallCompressorService;
    private InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService;
    private PromptContextServiceFactory promptContextServiceFactory;
    private MultiFileCreateDialogFactory multiFileCreateDialogFactory;

    public InquiryChatListViewer(Gson gson,
                                 InquiryViewer inquiryViewer,
                                 TextAreaHeightCalculatorService textAreaHeightCalculatorService,
                                 PromptContextServiceFactory promptContextServiceFactory,
                                 CodactorToolWindowService codactorToolWindowService,
                                 InquiryChatListFunctionCallCompressorService inquiryChatListFunctionCallCompressorService,
                                 InquiryFunctionCallProcessorService inquiryFunctionCallProcessorService,
                                 MultiFileCreateDialogFactory multiFileCreateDialogFactory) {
        this.gson = gson;
        this.inquiryViewer = inquiryViewer;
        this.textAreaHeightCalculatorService = textAreaHeightCalculatorService;
        this.openAiModelService = new OpenAiModelServiceImpl(codactorToolWindowService);
        this.inquiryChatListFunctionCallCompressorService = inquiryChatListFunctionCallCompressorService;
        this.promptContextServiceFactory = promptContextServiceFactory;
        this.inquiryFunctionCallProcessorService = inquiryFunctionCallProcessorService;
        this.multiFileCreateDialogFactory = multiFileCreateDialogFactory;
        initComponents();
        //Gson gson = new Gson();
        Inquiry inquiry1 = gson.fromJson("{\"myId\":\"6eddb296-6e70-444b-a8bd-dc4f3752993f\",\"userId\":\"86715441-ccd9-4b77-b2f6-0ec09403f538\",\"creationTimestamp\":{\"date\":{\"year\":2023,\"month\":7,\"day\":5},\"time\":{\"hour\":21,\"minute\":45,\"second\":13,\"nano\":879990000}},\"modifiedTimestamp\":{\"date\":{\"year\":2023,\"month\":7,\"day\":5},\"time\":{\"hour\":21,\"minute\":45,\"second\":13,\"nano\":880119000}},\"initialQuestion\":\"Can you use your functions to see whats inside of package com.translator.listener.EditorListener?\",\"chats\":[{\"myId\":\"9997f001-a938-4df9-9594-1d223a915d70\",\"creationTimestamp\":{\"date\":{\"year\":2023,\"month\":7,\"day\":5},\"time\":{\"hour\":21,\"minute\":45,\"second\":17,\"nano\":762218000}},\"modifiedTimestamp\":{\"date\":{\"year\":2023,\"month\":7,\"day\":5},\"time\":{\"hour\":21,\"minute\":45,\"second\":17,\"nano\":762241000}},\"userId\":\"86715441-ccd9-4b77-b2f6-0ec09403f538\",\"inquiryId\":\"6eddb296-6e70-444b-a8bd-dc4f3752993f\",\"from\":\"User\",\"message\":\"Can you use your functions to see whats inside of package com.translator.listener.EditorListener?\",\"likelyCodeLanguage\":\"txt\",\"functions\":[{\"name\":\"read_file_at_path\",\"description\":\"Read the contents of a code or text file given its path\",\"parameters\":{\"type\":\"object\",\"properties\":{\"path\":{\"type\":\"string\",\"description\":\"The path of the code file eg. /Users/user/IdeaProjects/code_project/src/code.java\"},\"startIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means 0\"},\"endIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means the end of the code file\"}},\"required\":[\"path\"]}},{\"name\":\"read_file_at_package\",\"description\":\"Read the contents of a code or text file given its package in the project directory\",\"parameters\":{\"type\":\"object\",\"properties\":{\"startIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means 0\"},\"package\":{\"type\":\"string\",\"description\":\"The package of the code file e.g. com.translator.view.uml.node.dialog.prompt\"},\"endIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means the end of the code file\"}},\"required\":[\"package\"]}},{\"name\":\"get_queued_modification_ids\",\"description\":\"Get the list of queued modification ids\",\"parameters\":{\"type\":\"object\",\"properties\":{},\"required\":[]}},{\"name\":\"read_modification_in_queue_at_position\",\"description\":\"Read the contents of a queued modification given its position in the queue\",\"parameters\":{\"type\":\"object\",\"properties\":{\"position\":{\"type\":\"integer\",\"description\":\"The position of the file modification in the queue\"}},\"required\":[\"position\"]}},{\"name\":\"read_modification_in_queue\",\"description\":\"Read the contents of a queued modification given its id\",\"parameters\":{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\",\"description\":\"The id of the file modification in the queue\"}},\"required\":[\"id\"]}},{\"name\":\"retry_modification_in_queue\",\"description\":\"Retry a queued modification\",\"parameters\":{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\",\"description\":\"The id of the file modification in the queue\"}},\"required\":[\"id\"]}},{\"name\":\"remove_modification_in_queue\",\"description\":\"Remove a queued modification\",\"parameters\":{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\",\"description\":\"The id of the file modification in the queue\"}},\"required\":[\"id\"]}},{\"name\":\"request_file_modification\",\"description\":\"Request a new file modification to be processed by the file modifier LLM\",\"parameters\":{\"type\":\"object\",\"properties\":{\"path\":{\"type\":\"string\",\"description\":\"The path of the code file eg. /Users/user/IdeaProjects/code_project/src/code.java\"},\"startIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means 0\"},\"endIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means the end of the code file\"},\"modificationType\":{\"type\":\"string\",\"description\":\"The type of file modification, e.g. modify, fix, create\"},\"description\":{\"type\":\"string\",\"description\":\"The description of the requested file modification to be enacted on the file\"}},\"required\":[\"path\",\"modificationType\",\"description\"]}},{\"name\":\"request_file_modification_and_wait_for_response\",\"description\":\"Request a new file modification to be processed and wait for response\",\"parameters\":{\"type\":\"object\",\"properties\":{\"path\":{\"type\":\"string\",\"description\":\"The path of the code file eg. /Users/user/IdeaProjects/code_project/src/code.java\"},\"startIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means 0\"},\"endIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means the end of the code file\"},\"modificationType\":{\"type\":\"string\",\"description\":\"The type of file modification, e.g. modify, fix, create\"},\"description\":{\"type\":\"string\",\"description\":\"The description of the requested file modification to be enacted on the file\"}},\"required\":[\"path\",\"modificationType\",\"description\"]}},{\"name\":\"read_directory_structure_at_path\",\"description\":\"Read the file directory structure at the provided path\",\"parameters\":{\"type\":\"object\",\"properties\":{\"path\":{\"type\":\"string\",\"description\":\"The path of the directory eg. /Users/user/IdeaProjects/code_project/src\"},\"depth\":{\"type\":\"integer\",\"description\":\"The depth of the directory structure returned. If set to 0, it will just return the files and directories immediately inside of the folder at the provided path. If set to 1, it will also return the files and directories immediately inside of its child directories one level deep, and so on.\"}},\"required\":[\"path\",\"depth\"]}},{\"name\":\"run_program\",\"description\":\"Run a program file and read its command line output\",\"parameters\":{\"type\":\"object\",\"properties\":{\"path\":{\"type\":\"string\",\"description\":\"The path of the code file eg. /path/to/python/script.py\"},\"interpreter\":{\"type\":\"string\",\"description\":\"The interpreter language for the selected file eg. python\"}},\"required\":[\"path\",\"interpreter\"]}}]},{\"myId\":\"1ce7cbbb-d984-4ed8-8e7b-9a517b291c29\",\"creationTimestamp\":{\"date\":{\"year\":2023,\"month\":7,\"day\":5},\"time\":{\"hour\":21,\"minute\":45,\"second\":18,\"nano\":173649000}},\"modifiedTimestamp\":{\"date\":{\"year\":2023,\"month\":7,\"day\":5},\"time\":{\"hour\":21,\"minute\":45,\"second\":18,\"nano\":173671000}},\"userId\":\"86715441-ccd9-4b77-b2f6-0ec09403f538\",\"inquiryId\":\"6eddb296-6e70-444b-a8bd-dc4f3752993f\",\"previousInquiryChatId\":\"9997f001-a938-4df9-9594-1d223a915d70\",\"from\":\"Assistant\",\"likelyCodeLanguage\":\"txt\",\"functionCall\":{\"name\":\"read_file_at_package\",\"arguments\":\"{\\n  \\\"package\\\": \\\"com.translator.listener.EditorListener\\\"\\n}\"},\"functions\":[{\"name\":\"read_file_at_path\",\"description\":\"Read the contents of a code or text file given its path\",\"parameters\":{\"type\":\"object\",\"properties\":{\"path\":{\"type\":\"string\",\"description\":\"The path of the code file eg. /Users/user/IdeaProjects/code_project/src/code.java\"},\"startIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means 0\"},\"endIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means the end of the code file\"}},\"required\":[\"path\"]}},{\"name\":\"read_file_at_package\",\"description\":\"Read the contents of a code or text file given its package in the project directory\",\"parameters\":{\"type\":\"object\",\"properties\":{\"startIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means 0\"},\"package\":{\"type\":\"string\",\"description\":\"The package of the code file e.g. com.translator.view.uml.node.dialog.prompt\"},\"endIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means the end of the code file\"}},\"required\":[\"package\"]}},{\"name\":\"get_queued_modification_ids\",\"description\":\"Get the list of queued modification ids\",\"parameters\":{\"type\":\"object\",\"properties\":{},\"required\":[]}},{\"name\":\"read_modification_in_queue_at_position\",\"description\":\"Read the contents of a queued modification given its position in the queue\",\"parameters\":{\"type\":\"object\",\"properties\":{\"position\":{\"type\":\"integer\",\"description\":\"The position of the file modification in the queue\"}},\"required\":[\"position\"]}},{\"name\":\"read_modification_in_queue\",\"description\":\"Read the contents of a queued modification given its id\",\"parameters\":{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\",\"description\":\"The id of the file modification in the queue\"}},\"required\":[\"id\"]}},{\"name\":\"retry_modification_in_queue\",\"description\":\"Retry a queued modification\",\"parameters\":{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\",\"description\":\"The id of the file modification in the queue\"}},\"required\":[\"id\"]}},{\"name\":\"remove_modification_in_queue\",\"description\":\"Remove a queued modification\",\"parameters\":{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"string\",\"description\":\"The id of the file modification in the queue\"}},\"required\":[\"id\"]}},{\"name\":\"request_file_modification\",\"description\":\"Request a new file modification to be processed by the file modifier LLM\",\"parameters\":{\"type\":\"object\",\"properties\":{\"path\":{\"type\":\"string\",\"description\":\"The path of the code file eg. /Users/user/IdeaProjects/code_project/src/code.java\"},\"startIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means 0\"},\"endIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means the end of the code file\"},\"modificationType\":{\"type\":\"string\",\"description\":\"The type of file modification, e.g. modify, fix, create\"},\"description\":{\"type\":\"string\",\"description\":\"The description of the requested file modification to be enacted on the file\"}},\"required\":[\"path\",\"modificationType\",\"description\"]}},{\"name\":\"request_file_modification_and_wait_for_response\",\"description\":\"Request a new file modification to be processed and wait for response\",\"parameters\":{\"type\":\"object\",\"properties\":{\"path\":{\"type\":\"string\",\"description\":\"The path of the code file eg. /Users/user/IdeaProjects/code_project/src/code.java\"},\"startIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means 0\"},\"endIndex\":{\"type\":\"integer\",\"description\":\"The start index of the code to be read in the file. Can be null which means the end of the code file\"},\"modificationType\":{\"type\":\"string\",\"description\":\"The type of file modification, e.g. modify, fix, create\"},\"description\":{\"type\":\"string\",\"description\":\"The description of the requested file modification to be enacted on the file\"}},\"required\":[\"path\",\"modificationType\",\"description\"]}},{\"name\":\"read_directory_structure_at_path\",\"description\":\"Read the file directory structure at the provided path\",\"parameters\":{\"type\":\"object\",\"properties\":{\"path\":{\"type\":\"string\",\"description\":\"The path of the directory eg. /Users/user/IdeaProjects/code_project/src\"},\"depth\":{\"type\":\"integer\",\"description\":\"The depth of the directory structure returned. If set to 0, it will just return the files and directories immediately inside of the folder at the provided path. If set to 1, it will also return the files and directories immediately inside of its child directories one level deep, and so on.\"}},\"required\":[\"path\",\"depth\"]}},{\"name\":\"run_program\",\"description\":\"Run a program file and read its command line output\",\"parameters\":{\"type\":\"object\",\"properties\":{\"path\":{\"type\":\"string\",\"description\":\"The path of the code file eg. /path/to/python/script.py\"},\"interpreter\":{\"type\":\"string\",\"description\":\"The interpreter language for the selected file eg. python\"}},\"required\":[\"path\",\"interpreter\"]}}]}]}\n", Inquiry.class);
        updateInquiryContents(inquiry1);
        //inquiryFunctionCallProcessorService.test();
    }

    private void initComponents() {
        inquiryChatList = new JBList<>();
        inquiryChatList.setModel(new DefaultListModel<>());
        inquiryChatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inquiryChatList.setCellRenderer(new InquiryChatRenderer());

        listSelectionListener = e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = inquiryChatList.getSelectedIndex();
                if (selectedIndex == -1) {
                    return;
                }
                selectedChat = selectedIndex;
                updateSelectionHighlighting();
            }
        };

        inquiryChatList.addListSelectionListener(listSelectionListener);

        inquiryChatList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C && (e.isControlDown() || e.isMetaDown())) {
                    if (selectedChat != -1) {
                        InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
                        JBTextArea jBTextArea = (JBTextArea) inquiryChatViewer.getComponents()[1];
                        StringSelection selection = new StringSelection(jBTextArea.getText());
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, null);
                    }
                }
            }
        });
        JBPopupMenu jBPopupMenu = new JBPopupMenu();
        inquiryChatList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                InquiryChatViewer inquiryChatViewer = null;
                if (e.getButton() == MouseEvent.BUTTON3) {
                    inquiryChatViewer = inquiryChatList.getModel().getElementAt(inquiryChatList.locationToIndex(e.getPoint()));
                    int selectedIndex = inquiryChatList.locationToIndex(e.getPoint());
                    inquiryChatList.setSelectedIndex(selectedIndex);
                    InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
                    if (inquiryChat == null || inquiryChat.getFrom().equalsIgnoreCase("assistant") || inquiryChatList.locationToIndex(e.getPoint()) == 0) {
                        editItem.setEnabled(false);
                        regenerateItem.setEnabled(false);
                        previousChat.setEnabled(false);
                        nextChat.setEnabled(false);
                    } else {
                        if (inquiryChat.getFrom().equalsIgnoreCase("user")) {
                            editItem.setEnabled(true);
                            regenerateItem.setEnabled(true);
                        } else {
                            editItem.setEnabled(false);
                            regenerateItem.setEnabled(false);
                        }
                        if (inquiryChat.getAlternateInquiryChatIds().isEmpty()) {
                            previousChat.setEnabled(false);
                            nextChat.setEnabled(false);
                        } else {
                            previousChat.setEnabled(!Objects.equals(inquiryChat.getId(), inquiryChat.getAlternateInquiryChatIds().get(0)));
                            nextChat.setEnabled(inquiryChat.getId() != null && !Objects.equals(inquiryChat.getId(), inquiryChat.getAlternateInquiryChatIds().get(inquiryChat.getAlternateInquiryChatIds().size() - 1)));
                        }
                    }
                    jBPopupMenu.show(inquiryChatList, e.getX(), e.getY());
                }
                if (selectedChat == -1) {
                    return;
                }
                if (inquiryChatViewer == null) {
                    inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
                }
                if (e.getClickCount() == 2) {
                    //Component component = inquiryChatViewer.getComponentAt(e.getPoint());
                    StringBuilder text = new StringBuilder();
                    boolean firstComponentCopied = false;
                    for (int i = 0; i < inquiryChatViewer.getComponents().length; i++) {
                        Component component1 = inquiryChatViewer.getComponents()[i];
                        if (firstComponentCopied) {
                            text.append("\n");
                            text.append("\n");
                        }
                        if (component1 instanceof JBTextArea) {
                            JBTextArea jBTextArea = (JBTextArea) component1;
                            text.append(jBTextArea.getText());
                            firstComponentCopied = true;
                        } else if (component1 instanceof FixedHeightPanel) {
                            FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component1;
                            Editor editor = fixedHeightPanel.getEditor();
                            if (editor != null) {
                                text.append(editor.getDocument().getText());
                                firstComponentCopied = true;
                            }
                        }
                    }
                    new TextAreaWindow(text.toString());
                } else if (selectedChat == lastSelectedChat) {
                    inquiryChatList.clearSelection();
                    inquiryChatList.setSelectedIndex(-1);
                    selectedChat = -1;
                    updateSelectionHighlighting();
                }
                lastSelectedChat = selectedChat;
            }
        });

        editItem = new JBMenuItem("Edit");
        regenerateItem = new JBMenuItem("Regenerate");
        previousChat = new JBMenuItem("Show Previous Chat");
        nextChat = new JBMenuItem("Show Next Chat");
        autoGenerate = new JBMenuItem("(Experimental) Auto-Generate");

        editItem.addActionListener(e -> {
            InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
            InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
            TextAreaWindow.TextAreaWindowActionListener textAreaWindowActionListener = new TextAreaWindow.TextAreaWindowActionListener() {
                @Override
                public void onOk(String text) {
                    editQuestion(inquiryChat.getId(), text);
                }
            };
            new TextAreaWindow("Edit Message", inquiryChat.getMessage(), true, "Cancel", "Ok", textAreaWindowActionListener);
        });
        regenerateItem.addActionListener(e -> {
            InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
            InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
            editQuestion(inquiryChat.getId(), inquiryChat.getMessage());
        });

        previousChat.addActionListener(e -> {
            if (selectedChat > 0){
                InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
                InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
                int indexOfInquiryChat = inquiryChat.getAlternateInquiryChatIds().indexOf(inquiryChat.getId());
                if (indexOfInquiryChat == -1) {
                    indexOfInquiryChat = inquiryChat.getAlternateInquiryChatIds().size();
                }
                String previousChatId = inquiryChat.getAlternateInquiryChatIds().get(indexOfInquiryChat - 1);
                InquiryChat previousInquiryChat = inquiry.getChats().stream()
                        .filter(inquiryChatQuery -> (inquiryChatQuery.getId() != null && inquiryChatQuery.getId().equals(previousChatId)) || (inquiryChatQuery.getId() == null && previousChatId == null))
                        .findFirst()
                        .orElseThrow();
                InquiryChat newerInquiryChat = findNextInquiryChat(inquiry.getChats(), previousInquiryChat);
                if (newerInquiryChat != null) {
                    while (newerInquiryChat != null) {
                        previousInquiryChat = newerInquiryChat;
                        newerInquiryChat = findNextInquiryChat(inquiry.getChats(), previousInquiryChat);
                    }
                }
                updateInquiryContents(inquiry, previousInquiryChat);
            }
        });
        nextChat.addActionListener(e -> {
            if (selectedChat < inquiryChatList.getModel().getSize() - 1){
                InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
                InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
                int indexOfInquiryChat = inquiryChat.getAlternateInquiryChatIds().indexOf(inquiryChat.getId());
                String nextChatId = inquiryChat.getAlternateInquiryChatIds().get(indexOfInquiryChat + 1);
                InquiryChat nextInquiryChat = inquiry.getChats().stream()
                        .filter(inquiryChatQuery -> (inquiryChatQuery.getId() != null && inquiryChatQuery.getId().equals(nextChatId)) || (inquiryChatQuery.getId() == null && nextChatId == null))
                        .findFirst()
                        .orElseThrow();
                InquiryChat newerInquiryChat = findNextInquiryChat(inquiry.getChats(), nextInquiryChat);
                if (newerInquiryChat != null) {
                    while (newerInquiryChat != null) {
                        nextInquiryChat = newerInquiryChat;
                        newerInquiryChat = findNextInquiryChat(inquiry.getChats(), nextInquiryChat);
                    }
                }
                updateInquiryContents(inquiry, nextInquiryChat);
            }
        });
        autoGenerate.addActionListener(e -> {
            if (selectedChat > 0){
                InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(selectedChat);
                InquiryChat inquiryChat = inquiryChatViewer.getInquiryChat();
                PromptContextService promptContextService = promptContextServiceFactory.create();
                MultiFileCreateDialog multiFileCreateDialog = multiFileCreateDialogFactory.create(null, inquiryChat.getMessage(), promptContextService, openAiModelService);
                multiFileCreateDialog.setVisible(true);
            }
        });

        jBPopupMenu.add(editItem);
        jBPopupMenu.add(regenerateItem);
        jBPopupMenu.addSeparator();
        jBPopupMenu.add(previousChat);
        jBPopupMenu.add(nextChat);
        jBPopupMenu.addSeparator();
        jBPopupMenu.add(autoGenerate);

        jToolBar = new JToolBar();
        jToolBar.setFloatable(false);
        jToolBar.setBorderPainted(false);

        JButton whatWasChangedButton = new JButton("\"What was changed?\"");
        whatWasChangedButton.setFocusable(false);
        whatWasChangedButton.setHorizontalTextPosition(SwingConstants.CENTER);
        whatWasChangedButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        whatWasChangedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar.add(whatWasChangedButton);
        jToolBar.addSeparator();

        JButton whatDoesThisDoButton = new JButton("\"What does this do?\"");
        whatDoesThisDoButton.setFocusable(false);
        whatDoesThisDoButton.setHorizontalTextPosition(SwingConstants.CENTER);
        whatDoesThisDoButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        whatDoesThisDoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        jToolBar.add(whatDoesThisDoButton);
        jToolBar.setVisible(false);
        jBScrollPane = new JBScrollPane(inquiryChatList);
        jBScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(jBScrollPane, BorderLayout.CENTER);
    }

    private void updateSelectionHighlighting() {
        Color highlightColor = Color.decode("#009688");
        for (int i = 0; i < inquiryChatList.getModel().getSize(); i++) {
            InquiryChatViewer inquiryChatViewer = inquiryChatList.getModel().getElementAt(i);
            if (i == selectedChat) {
                for (Component component : inquiryChatViewer.getComponents()) {
                    if (component instanceof JBTextArea) {
                        JBTextArea selectedJBTextArea = (JBTextArea) component;
                        //Highlight the whole text area
                        try {
                            selectedJBTextArea.getHighlighter().addHighlight(0, selectedJBTextArea.getText().length(), new DefaultHighlighter.DefaultHighlightPainter(highlightColor));
                        } catch (BadLocationException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else if (component instanceof FixedHeightPanel) {
                        FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                        Editor editor = fixedHeightPanel.getEditor();
                        if (editor != null) {
                            editor.getMarkupModel().addRangeHighlighter(0, editor.getDocument().getTextLength(), HighlighterLayer.SELECTION - 1, new TextAttributes(null, highlightColor, null, EffectType.BOXED, Font.PLAIN), HighlighterTargetArea.EXACT_RANGE);
                        }
                    }
                }
                continue;
            }
            for (Component component : inquiryChatViewer.getComponents()) {
                if (component instanceof JBTextArea) {
                    JBTextArea jBTextArea = (JBTextArea) component;
                    jBTextArea.getHighlighter().removeAllHighlights();
                } else if (component instanceof FixedHeightPanel) {
                    FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                    Editor editor = fixedHeightPanel.getEditor();
                    if (editor != null) {
                        editor.getMarkupModel().removeAllHighlighters();
                    }
                }
            }
        }
    }

    public void updateInquiryContents(Inquiry inquiry) {
        if (inquiry == null) {
            inquiryChatList.setModel(new DefaultListModel<>());
            return;
        }
        InquiryChat previousInquiryChat = null;
        if (!inquiry.getChats().isEmpty()) {
            previousInquiryChat = inquiry.getChats().stream()
                    .max(Comparator.comparing(InquiryChat::getCreationTimestamp))
                    .orElseThrow();
        }
        updateInquiryContents(inquiry, previousInquiryChat);
    }

    public void updateInquiryContents(Inquiry inquiry, InquiryChat previousInquiryChat) {
        this.inquiry = inquiry;
        this.inquiryViewer.setInquiry(inquiry);
        if (this.inquiryViewer.getInquiryChatBoxViewer() != null) {
            this.inquiryViewer.getInquiryChatBoxViewer().setInquiry(inquiry);
        }
        if (inquiry == null) {
            inquiryChatList.setModel(new DefaultListModel<>());
            return;
        }
        DefaultListModel<InquiryChatViewer> model = new DefaultListModel<>();
        if (inquiry.getBeforeCode() == null && inquiry.getSubjectRecordId() != null) {
            inquiry.setBeforeCode("");
        }
        if (inquiry.getDescription() != null) {
            String text = inquiry.getModificationType() + ": " + inquiry.getDescription().trim();
            InquiryChatViewer descriptionViewer = new InquiryChatViewer.Builder()
                    .withFilePath(inquiry.getFilePath())
                    .withMessage(text)
                    .withHeaderString("User")
                    .withInquiryChatType(InquiryChatType.INSTIGATOR_PROMPT)
                    .build();
            model.addElement(descriptionViewer);
            if (inquiry.getBeforeCode() != null) {
                String beforeCodeText = "```" + inquiry.getBeforeCode().trim() + "```";
                InquiryChatViewer beforeViewer = new InquiryChatViewer.Builder()
                        .withFilePath(inquiry.getFilePath())
                        .withMessage(beforeCodeText)
                        .withHeaderString("Before")
                        .withInquiryChatType(InquiryChatType.CODE_SNIPPET)
                        .build();
                model.addElement(beforeViewer);
            }
            if (inquiry.getAfterCode() != null) {
                String afterCodeText = "```" + inquiry.getAfterCode().trim() + "```";
                InquiryChatViewer afterViewer = new InquiryChatViewer.Builder()
                        .withFilePath(inquiry.getFilePath())
                        .withMessage(afterCodeText)
                        .withHeaderString("After")
                        .withInquiryChatType(InquiryChatType.CODE_SNIPPET)
                        .build();
                model.addElement(afterViewer);
            }
        } else if (inquiry.getSubjectCode() != null) {
            String subjectCodeText = "```" + inquiry.getSubjectCode().trim() + "```";
            InquiryChatViewer subjectCodeViewer = new InquiryChatViewer.Builder()
                    .withFilePath(inquiry.getFilePath())
                    .withMessage(subjectCodeText)
                    .withHeaderString("Code")
                    .withInquiryChatType(InquiryChatType.CODE_SNIPPET)
                    .build();
            model.addElement(subjectCodeViewer);
            String text = inquiry.getInitialQuestion().trim();
            InquiryChatViewer descriptionViewer = new InquiryChatViewer.Builder()
                    .withFilePath(inquiry.getFilePath())
                    .withMessage(text)
                    .withHeaderString("User")
                    .withInquiryChatType(InquiryChatType.INSTIGATOR_PROMPT)
                    .build();
            model.addElement(descriptionViewer);
        }
        List<InquiryChat> finalizedChatList = new ArrayList<>();
        //Find the most recent chat by filtering by creationTimestamp
        List<InquiryChat> chatList = inquiry.getChats();
        if (!chatList.isEmpty()) {
            jToolBar.setVisible(false);
            finalizedChatList.add(previousInquiryChat);
            String previousInquiryChatId = previousInquiryChat.getPreviousInquiryChatId();
            if (previousInquiryChatId != null) {
                while (previousInquiryChat != null) {
                    previousInquiryChat = findPreviousInquiryChat(chatList, previousInquiryChat);
                    if (previousInquiryChat != null) {
                        findAlternatesForInquiryChat(chatList, previousInquiryChat);
                        finalizedChatList.add(previousInquiryChat);
                    }
                }
            }
        } else if (inquiry.getDescription() != null) {
            jToolBar.setVisible(true);
        }
        Collections.reverse(finalizedChatList);
        List<InquiryChatViewer> compressedInquiryChatViewers = inquiryChatListFunctionCallCompressorService.compress(finalizedChatList);
        List<TestoObject> compressedInquiryChats = new ArrayList<>();
        for (InquiryChatViewer inquiryChatListViewer : compressedInquiryChatViewers) {
            compressedInquiryChats.add(new TestoObject(inquiryChatListViewer.getInquiryChat(), inquiryChatListViewer.getFunctionCalls()));
            model.addElement(inquiryChatListViewer);
        }
        System.out.println("Compressed: " + gson.toJson(compressedInquiryChats));
        ComponentListener componentListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                InquiryChatListViewer.this.componentResized(model);
            }
        };
        this.addComponentListener(componentListener);
        ApplicationManager.getApplication().invokeLater(() -> {
            inquiryChatList.setModel(model);
            jBScrollPane.setViewportView(inquiryChatList);
            JScrollBar verticalScrollBar = jBScrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            this.componentResized();
        });
    }

    private void editQuestion(String inquiryChatId, String newQuestion) {
        assert inquiry != null;
        InquiryChat inquiryChat = inquiry.getChats().stream().filter(inquiryChatQuery -> inquiryChatQuery.getId().equals(inquiryChatId)).findFirst().orElse(null);
        assert inquiryChat != null;
        assert inquiryChat.getPreviousInquiryChatId() != null;
        inquiryViewer.askContinuedQuestion(inquiryChat.getPreviousInquiryChatId(), newQuestion);
    }

    private InquiryChat findPreviousInquiryChat(java.util.List<InquiryChat> inquiryChats, InquiryChat inquiryChat) {
        return inquiryChats.stream()
                .filter(inquiryChatQuery ->  (inquiryChatQuery.getId() != null && inquiryChatQuery.getId().equals(inquiryChat.getPreviousInquiryChatId())))
                .findFirst()
                .orElse(null);
    }

    private InquiryChat findNextInquiryChat(java.util.List<InquiryChat> inquiryChats, InquiryChat inquiryChat) {
        return inquiryChats.stream()
                .filter(inquiryChatQuery -> inquiryChatQuery.getPreviousInquiryChatId() != null && inquiryChatQuery.getPreviousInquiryChatId().equals(inquiryChat.getId()))
                .findFirst()
                .orElse(null);
    }

    public void findAlternatesForInquiryChat(java.util.List<InquiryChat> inquiryChats, InquiryChat inquiryChat) {
        java.util.List<InquiryChat> alternateInquiryChats = inquiryChats.stream()
                .filter(inquiryChatQuery ->
                        (inquiryChatQuery.getPreviousInquiryChatId() != null && inquiryChatQuery.getPreviousInquiryChatId().equals(inquiryChat.getPreviousInquiryChatId()))
                                || (inquiryChatQuery.getPreviousInquiryChatId() == null && inquiryChat.getPreviousInquiryChatId() == null))
                .sorted(Comparator.comparing(InquiryChat::getCreationTimestamp))
                .collect(Collectors.toList());
        //Sorted by creationTimestamp
        List<String> alternateInquiryChatIds = alternateInquiryChats.stream()
                .map(InquiryChat::getId)
                .collect(Collectors.toList());
        inquiryChat.setAlternateInquiryChatIds(alternateInquiryChatIds);
    }

    public void componentResized(DefaultListModel<InquiryChatViewer> previousModel) {
        ApplicationManager.getApplication().invokeLater(() -> {
            DefaultListModel<InquiryChatViewer> newModel = new DefaultListModel<>();
            int newTotalHeight = 0;
            for (int i = 0; i < previousModel.size(); i++) {
                InquiryChatViewer chatViewer = previousModel.getElementAt(i);
                for (Component component : chatViewer.getComponents()) {
                    if (component instanceof JBTextArea) {
                        JBTextArea chatDisplay = (JBTextArea) component;
                        int newHeight = 0;
                        int newWidth = getWidth();
                        if (chatViewer.getInquiryChat().getInquiryChatType() == InquiryChatType.CODE_SNIPPET) {
                            newHeight += textAreaHeightCalculatorService.calculateDesiredHeight(chatDisplay, newWidth, false);
                        } else {
                            newHeight += textAreaHeightCalculatorService.calculateDesiredHeight(chatDisplay, newWidth, true);
                        }
                        Dimension preferredSize = new Dimension(newWidth, newHeight);
                        chatDisplay.setPreferredSize(preferredSize);
                        chatDisplay.setMaximumSize(preferredSize);
                        chatDisplay.setSize(preferredSize);
                        newTotalHeight += newHeight + chatViewer.getComponent(0).getHeight();
                    } else if (component instanceof FixedHeightPanel) {
                        FixedHeightPanel fixedHeightPanel = (FixedHeightPanel) component;
                        newTotalHeight += fixedHeightPanel.getHeight();
                    }
                    newTotalHeight += chatViewer.getComponent(0).getHeight();
                }
                newModel.addElement(chatViewer);
            }
            jBScrollPane.setPreferredSize(new Dimension(getWidth(), getHeight()));
            inquiryChatList.setPreferredSize(new Dimension(jBScrollPane.getWidth(), newTotalHeight));
            inquiryChatList.setModel(newModel);
            jBScrollPane.setViewportView(inquiryChatList);
        });
    }

    public void componentResized() {
        componentResized((DefaultListModel<InquiryChatViewer>) inquiryChatList.getModel());
    }

    public JBMenuItem getEditItem() {
        return editItem;
    }

    public JBMenuItem getRegenerateItem() {
        return regenerateItem;
    }

    public JBMenuItem getNextChat() {
        return nextChat;
    }

    public JBMenuItem getPreviousChat() {
        return previousChat;
    }

    public OpenAiModelService getOpenAiModelService() {
        return openAiModelService;
    }
}