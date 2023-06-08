package com.translator.view.codactor.dialog;

import com.intellij.ui.components.JBTextArea;
import com.translator.service.codactor.openai.OpenAiApiKeyService;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class OpenAiApiKeyDialog extends JDialog {
    private OpenAiApiKeyService openAiApiKeyService;
    private JBTextArea apiKeyField;

    public OpenAiApiKeyDialog(OpenAiApiKeyService openAiApiKeyService) {
        setTitle("Enter Your OpenAI API Key");
        setLocationRelativeTo(null);
        setModal(true);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);

        JLabel apiKeyLabel = new JLabel("API Key:");
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(apiKeyLabel, c);

        apiKeyField = new JBTextArea();
        apiKeyField.setLineWrap(true);
        apiKeyField.setWrapStyleWord(true);
        apiKeyField.setPreferredSize(new Dimension(240, apiKeyField.getPreferredSize().height));
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(apiKeyField, c);

        JLabel subtitleLabel = new JLabel("<html>Don't have an API key? Follow these steps:<br>1. Log in to your OpenAI account.<br>2. Click on the \"API Keys\" tab.<br>3. Click the \"+ Create new API key\" button.<br>4. Enter a name for your API key and click the \"Create\" button.<br>5. Copy the generated API key and paste it into the field above.</html>");
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(Font.PLAIN, 12f));
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 10, 10, 10);
        panel.add(subtitleLabel, c);

        JButton okButton = new JButton("OK");
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(10, 10, 10, 10);
        panel.add(okButton, c);
        okButton.addActionListener(e -> {
            String apiKey = apiKeyField.getText();
            openAiApiKeyService.setOpenAiApiKey(apiKey);
            dispose();
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null); // centers the dialog
        setResizable(false);

        // Add listener to paste text with CTRL/CMD+V
        apiKeyField.getActionMap().put("paste", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                String data;
                try {
                    data = (String) clipboard.getData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException | IOException ex) {
                    throw new RuntimeException(ex);
                }
                apiKeyField.replaceSelection(data);
            }
        });

        setVisible(true);
    }
}