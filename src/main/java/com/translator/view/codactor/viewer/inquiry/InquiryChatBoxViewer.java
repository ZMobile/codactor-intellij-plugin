package com.translator.view.codactor.viewer.inquiry;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.translator.model.codactor.inquiry.Inquiry;
import com.translator.model.codactor.inquiry.InquiryChat;
import com.translator.service.codactor.openai.OpenAiModelService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.Objects;

public class InquiryChatBoxViewer extends JPanel {
    private Inquiry inquiry;
    private InquiryViewer inquiryViewer;
    private JToolBar toolBar;
    private ComboBox<String> modelComboBox;
    private JBTextArea promptInput;
    private JButton askButton;
    private JButton microphoneButton;

    public InquiryChatBoxViewer(InquiryViewer inquiryViewer) {
        this.inquiryViewer = inquiryViewer;
        initComponents();
        addComponents();
    }

    private void initComponents() {
        promptInput = new JBTextArea();
        promptInput.setLineWrap(true);
        promptInput.setWrapStyleWord(true);

        microphoneButton = new JButton();
        microphoneButton.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/microphone_icon.png"))));
        microphoneButton.setMaximumSize(new Dimension(80, 23));
        microphoneButton.setMinimumSize(new Dimension(80, 23));
        microphoneButton.setPreferredSize(new Dimension(80, 23));
        microphoneButton.setSize(new Dimension(80, 23));
        microphoneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Robot robot = null;
                try {
                    robot = new Robot();
                    promptInput.requestFocusInWindow();
                    // Simulate a key press event for the CNTRL key.
                    robot.keyPress(KeyEvent.VK_CONTROL);
                    robot.keyRelease(KeyEvent.VK_CONTROL);

                    // Simulate another key press event for the CNTRL key.
                    robot.keyPress(KeyEvent.VK_CONTROL);
                    robot.keyRelease(KeyEvent.VK_CONTROL);
                } catch (AWTException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        askButton = new JButton();
        askButton.setText("Ask");
        askButton.setToolTipText("");
        askButton.setMaximumSize(new Dimension(80, 23));
        askButton.setMinimumSize(new Dimension(80, 23));
        askButton.setPreferredSize(new Dimension(80, 23));
        askButton.setSize(new Dimension(80, 23));
        askButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (promptInput.getText().isEmpty()) return;
                if (inquiry == null) {
                    inquiryViewer.askNewGeneralInquiryQuestion(promptInput.getText());
                    promptInput.setText("");
                    return;
                }
                if (!inquiry.getChats().isEmpty()){
                    InquiryChat previousInquiryChat = inquiry.getChats().stream()
                            .max(Comparator.comparing(InquiryChat::getCreationTimestamp))
                            .orElseThrow();
                    inquiryViewer.askContinuedQuestion(previousInquiryChat.getId(), promptInput.getText());
                } else if (inquiry.getSubjectRecordId() != null){
                    inquiryViewer.askInquiryQuestion(inquiry.getSubjectRecordId(), inquiry.getSubjectRecordType(), promptInput.getText(), inquiry.getFilePath());
                } else {
                    inquiryViewer.askNewGeneralInquiryQuestion(promptInput.getText());
                }
                promptInput.setText("");
            }
        });
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);

        OpenAiModelService openAiModelService = inquiryViewer.getInquiryChatListViewer().getOpenAiModelService();
        modelComboBox = new ComboBox<>(new String[]{"gpt-3.5-turbo", "gpt-3.5-turbo-16k", "gpt-4", "gpt-4-32k", "gpt-4-0314", "gpt-4-32k-0314", "gpt-3.5-turbo-0613", "gpt-4-0613"});
        modelComboBox.setMaximumSize(new Dimension(200, modelComboBox.getPreferredSize().height));
        String selectedElement = openAiModelService.getSelectedOpenAiModel();
        int selectedIndex = -1;
        for (int i = 0; i < modelComboBox.getItemCount(); i++) {
            if (selectedElement.equals(modelComboBox.getItemAt(i))) {
                selectedIndex = i;
                break;
            }
        }
// Check if the selected element is in the combo box
        if (selectedIndex != -1 && selectedIndex != 0) {
            // Store the element at position 0
            String elementAtZero = modelComboBox.getItemAt(0);

            // Remove the selected element from the combo box
            modelComboBox.removeItemAt(selectedIndex);

            // Insert the selected element at the first position
            modelComboBox.insertItemAt(selectedElement, 0);

            // Remove the element at position 1 (which was previously at position 0)
            modelComboBox.removeItemAt(1);

            // Insert the element that was previously at position 0 to the original position of the selected element
            modelComboBox.insertItemAt(elementAtZero, selectedIndex);

            // Set the selected index to 0
            modelComboBox.setSelectedIndex(0);
        }
        modelComboBox.addActionListener(e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            String model = (String) cb.getSelectedItem();
            if (model != null) {
                openAiModelService.setSelectedOpenAiModel(model);
            }
        });
        toolBar.add(modelComboBox);
        toolBar.addSeparator();

        JButton whatWasChangedButton = new JButton("\"What was changed?\"");
        whatWasChangedButton.setFocusable(false);
        whatWasChangedButton.setHorizontalTextPosition(SwingConstants.CENTER);
        whatWasChangedButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        whatWasChangedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolBar.add(whatWasChangedButton);
        toolBar.addSeparator();

        JButton whatDoesThisDoButton = new JButton("\"What does this do?\"");
        whatDoesThisDoButton.setFocusable(false);
        whatDoesThisDoButton.setHorizontalTextPosition(SwingConstants.CENTER);
        whatDoesThisDoButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        whatDoesThisDoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolBar.add(whatDoesThisDoButton);
        toolBar.setVisible(true);
    }

    private void addComponents() {
        setLayout(new BorderLayout());

        JBScrollPane jBScrollPane = new JBScrollPane(promptInput);

        JPanel promptInputPanel = new JPanel();
        GroupLayout promptInputPanelLayout = new GroupLayout(promptInputPanel);
        promptInputPanel.setLayout(promptInputPanelLayout);
        promptInputPanelLayout.setHorizontalGroup(
                promptInputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(toolBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(promptInputPanelLayout.createSequentialGroup()
                                .addComponent(jBScrollPane, GroupLayout.DEFAULT_SIZE, 1073, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(promptInputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(microphoneButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(askButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, 0))
                );
                promptInputPanelLayout.setVerticalGroup(
                        promptInputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(promptInputPanelLayout.createSequentialGroup()
                                .addComponent(toolBar)
                                .addGroup(promptInputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jBScrollPane, 100, 100, 100)
                                        .addGroup(promptInputPanelLayout.createSequentialGroup()
                                                .addComponent(microphoneButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(askButton, 64, 64, 64))
                                        .addGap(0, 0, 0)))
                );
                add(promptInputPanel, BorderLayout.CENTER);
    }

    public JToolBar getToolBar() {
        return toolBar;
    }

    public JButton getAskButton() {
        return askButton;
    }

    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }
}