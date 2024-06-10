package com.translator.view.codactor.settings;

import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.service.codactor.ai.openai.connection.AzureConnectionService;
import com.translator.service.codactor.ai.openai.connection.CodactorConnectionService;
import com.translator.service.codactor.ai.openai.connection.CodactorConnectionType;
import com.translator.service.codactor.ai.openai.connection.DefaultConnectionService;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CodactorSettingsPage {
    private final FirebaseTokenService firebaseTokenService;
    private final CodactorConnectionService codactorConnectionService;
    private final DefaultConnectionService defaultConnectionService;
    private final AzureConnectionService azureConnectionService;
    private boolean modified;
    private JPanel rootPanel;
    private JLabel codactorConnectionLabel;
    private JRadioButton defaultConnectionButton;
    private JRadioButton azureRadioButton;
    private JRadioButton enterpriseRadioButton;
    private JTextField textField1;
    private JButton logOutButton;
    private JButton loginButton;
    private JLabel urlLabel;
    private JLabel keyLabel;
    private JLabel accountNameLabel;
    private JLabel accountLabel;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JTextField textField7;
    private JPasswordField keyField;
    private JPanel urlPanel;
    private JLabel httpLabel;
    private JLabel urlCompletionLabel;
    private JPanel gpt35TurboDeploymentNamePanel;
    private JPanel gpt35Turbo16kDeploymentNamePanel;
    private JPanel gpt4DeploymentNamePanel;
    private JPanel gpt432kDeploymentNamePanel;
    private JPanel keyPanel;
    private JScrollBar scrollBar1;
    private JTextField emailTextField;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JLabel emailLabel;
    private JPanel codactorConnectionTypePanel;
    private JPanel accountButtonsAndInfoPanel;
    private JLabel errorLabel;
    private JPanel accountPanel;
    private JPanel accountEmailPanel;
    private JPanel accountPasswordPanel;
    private JPanel codactorConnectionInfoPanel;
    private JPanel localApiPanel;
    private JLabel apiSettingLabel;
    private JLabel portLabel;
    private JTextField textField2;
    private JButton enableButton;
    private JLabel localApiServerStatusLabel;
    private JPanel gpt4oDeploymentNamePanel;


    DocumentListener textChangedListener = new DocumentListener() {
        public void changedUpdate(DocumentEvent e) {
            modified = true;
        }
        public void removeUpdate(DocumentEvent e) {
            modified = true;
        }
        public void insertUpdate(DocumentEvent e) {
            modified = true;
        }
    };

    ActionListener buttonChangedListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            modified = true;
        }
    };

    public CodactorSettingsPage(FirebaseTokenService firebaseTokenService, CodactorConnectionService codactorConnectionService, DefaultConnectionService defaultConnectionService, AzureConnectionService azureConnectionService) {
        this.firebaseTokenService = firebaseTokenService;
        this.codactorConnectionService = codactorConnectionService;
        this.defaultConnectionService = defaultConnectionService;
        this.azureConnectionService = azureConnectionService;
        keyField.getDocument().addDocumentListener(textChangedListener);
        textField1.getDocument().addDocumentListener(textChangedListener);
        textField3.getDocument().addDocumentListener(textChangedListener);
        textField4.getDocument().addDocumentListener(textChangedListener);
        textField5.getDocument().addDocumentListener(textChangedListener);
        textField6.getDocument().addDocumentListener(textChangedListener);
        textField7.getDocument().addDocumentListener(textChangedListener);

        logOutButton.addActionListener(buttonChangedListener);
        loginButton.addActionListener(buttonChangedListener);
        defaultConnectionButton.addActionListener(buttonChangedListener);
        azureRadioButton.addActionListener(buttonChangedListener);
        enterpriseRadioButton.addActionListener(buttonChangedListener);

        logOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // perform logic here
                codactorConnectionTypePanel.setVisible(false);
                codactorConnectionInfoPanel.setVisible(false);
                firebaseTokenService.logout();
                accountPanel.setVisible(true);
                loginButton.setEnabled(true);
                loginButton.setVisible(true);
                logOutButton.setVisible(false);
                accountEmailPanel.setVisible(false);
                accountPasswordPanel.setVisible(false);
                errorLabel.setVisible(false);
                accountNameLabel.setText("");
                accountNameLabel.setVisible(false);
                reset();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!accountEmailPanel.isVisible()) {
                    accountEmailPanel.setVisible(true);
                    accountPasswordPanel.setVisible(true);
                    errorLabel.setVisible(false);
                } else {
                    // perform logic here
                    loginButton.setEnabled(false);
                    errorLabel.setVisible(false);
                    boolean loginStatus = firebaseTokenService.login(emailTextField.getText(), passwordField.getText());
                    if (!loginStatus) {
                        errorLabel.setVisible(true);
                        errorLabel.setText("Login Failed");
                        accountEmailPanel.setVisible(true);
                        accountPasswordPanel.setVisible(true);
                    } else {
                        accountNameLabel.setText(firebaseTokenService.getLoggedInUser());
                        accountEmailPanel.setVisible(false);
                        accountPasswordPanel.setVisible(false);
                        emailTextField.setText("");
                        passwordField.setText("");
                        logOutButton.setVisible(true);
                        loginButton.setVisible(false);
                        codactorConnectionTypePanel.setVisible(true);
                        codactorConnectionInfoPanel.setVisible(true);
                        if (codactorConnectionService.getConnectionType() == null) {
                            codactorConnectionService.setConnectionType(CodactorConnectionType.DEFAULT);
                        }
                        if (codactorConnectionService.getConnectionType() == CodactorConnectionType.DEFAULT) {
                            keyPanel.setVisible(true);
                            keyLabel.setText("OpenAI API Key");
                            keyField.setText(defaultConnectionService.getOpenAiApiKey());
                            defaultConnectionButton.setSelected(true);
                            azureRadioButton.setSelected(false);
                            enterpriseRadioButton.setSelected(false);
                            urlPanel.setVisible(false);
                            gpt35TurboDeploymentNamePanel.setVisible(false);
                            gpt35Turbo16kDeploymentNamePanel.setVisible(false);
                            gpt4DeploymentNamePanel.setVisible(false);
                            gpt432kDeploymentNamePanel.setVisible(false);
                        } else if (codactorConnectionService.getConnectionType() == CodactorConnectionType.AZURE) {
                            keyPanel.setVisible(true);
                            keyLabel.setText("Azure OpenAI API Key");
                            defaultConnectionButton.setSelected(false);
                            enterpriseRadioButton.setSelected(false);
                            urlPanel.setVisible(true);
                            urlLabel.setText("Azure Resource");
                            httpLabel.setVisible(true);
                            urlCompletionLabel.setVisible(true);
                            gpt35TurboDeploymentNamePanel.setVisible(true);
                            gpt35Turbo16kDeploymentNamePanel.setVisible(true);
                            gpt4DeploymentNamePanel.setVisible(true);
                            gpt432kDeploymentNamePanel.setVisible(true);
                            keyField.setText(azureConnectionService.getKey());
                            textField1.setText(azureConnectionService.getResource());
                            textField3.setText(azureConnectionService.getGpt35TurboDeployment());
                            textField4.setText(azureConnectionService.getGpt35Turbo16kDeployment());
                            textField5.setText(azureConnectionService.getGpt4Deployment());
                            textField6.setText(azureConnectionService.getGpt432kDeployment());
                            textField7.setText(azureConnectionService.getGpt4oDeployment());
                        } else {
                            keyPanel.setVisible(false);
                            defaultConnectionButton.setSelected(false);
                            azureRadioButton.setSelected(false);
                            urlPanel.setVisible(true);
                            urlLabel.setText("Backend URL");
                            httpLabel.setVisible(false);
                            urlCompletionLabel.setVisible(false);
                            gpt35TurboDeploymentNamePanel.setVisible(false);
                            gpt35Turbo16kDeploymentNamePanel.setVisible(false);
                            gpt4DeploymentNamePanel.setVisible(false);
                            gpt432kDeploymentNamePanel.setVisible(false);
                            textField1.setText("");
                        }
                    }
                    loginButton.setEnabled(true);
                }
            }
        });

        defaultConnectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyPanel.setVisible(true);
                keyLabel.setText("OpenAI API Key");
                azureRadioButton.setSelected(false);
                enterpriseRadioButton.setSelected(false);
                urlPanel.setVisible(false);
                gpt35TurboDeploymentNamePanel.setVisible(false);
                gpt35Turbo16kDeploymentNamePanel.setVisible(false);
                gpt4DeploymentNamePanel.setVisible(false);
                gpt432kDeploymentNamePanel.setVisible(false);
                keyField.setText(defaultConnectionService.getOpenAiApiKey());
            }
        });

        azureRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyPanel.setVisible(true);
                keyLabel.setText("Azure OpenAI API Key");
                defaultConnectionButton.setSelected(false);
                enterpriseRadioButton.setSelected(false);
                urlPanel.setVisible(true);
                urlLabel.setText("Azure Resource");
                httpLabel.setVisible(true);
                urlCompletionLabel.setVisible(true);
                gpt35TurboDeploymentNamePanel.setVisible(true);
                gpt35Turbo16kDeploymentNamePanel.setVisible(true);
                gpt4DeploymentNamePanel.setVisible(true);
                gpt432kDeploymentNamePanel.setVisible(true);
                keyField.setText(azureConnectionService.getKey());
                textField1.setText(azureConnectionService.getResource());
                textField3.setText(azureConnectionService.getGpt35TurboDeployment());
                textField4.setText(azureConnectionService.getGpt35Turbo16kDeployment());
                textField5.setText(azureConnectionService.getGpt4Deployment());
                textField6.setText(azureConnectionService.getGpt432kDeployment());
                textField7.setText(azureConnectionService.getGpt4oDeployment());
            }
        });

        enterpriseRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyPanel.setVisible(false);
                defaultConnectionButton.setSelected(false);
                azureRadioButton.setSelected(false);
                urlPanel.setVisible(true);
                urlLabel.setText("Backend URL");
                httpLabel.setVisible(false);
                urlCompletionLabel.setVisible(false);
                gpt35TurboDeploymentNamePanel.setVisible(false);
                gpt35Turbo16kDeploymentNamePanel.setVisible(false);
                gpt4DeploymentNamePanel.setVisible(false);
                gpt432kDeploymentNamePanel.setVisible(false);
                textField1.setText("");
            }
        });
        if (codactorConnectionService.getConnectionType() == CodactorConnectionType.DEFAULT) {
            keyPanel.setVisible(true);
            keyLabel.setText("OpenAI API Key");
            keyField.setText(defaultConnectionService.getOpenAiApiKey());
            defaultConnectionButton.setSelected(true);
            azureRadioButton.setSelected(false);
            enterpriseRadioButton.setSelected(false);
            urlPanel.setVisible(false);
            gpt35TurboDeploymentNamePanel.setVisible(false);
            gpt35Turbo16kDeploymentNamePanel.setVisible(false);
            gpt4DeploymentNamePanel.setVisible(false);
            gpt432kDeploymentNamePanel.setVisible(false);
        } else if (codactorConnectionService.getConnectionType() == CodactorConnectionType.AZURE) {
            keyPanel.setVisible(true);
            keyLabel.setText("Azure OpenAI API Key");
            defaultConnectionButton.setSelected(false);
            enterpriseRadioButton.setSelected(false);
            urlPanel.setVisible(true);
            urlLabel.setText("Azure Resource");
            httpLabel.setVisible(true);
            urlCompletionLabel.setVisible(true);
            gpt35TurboDeploymentNamePanel.setVisible(true);
            gpt35Turbo16kDeploymentNamePanel.setVisible(true);
            gpt4DeploymentNamePanel.setVisible(true);
            gpt432kDeploymentNamePanel.setVisible(true);
            keyField.setText(azureConnectionService.getKey());
            textField1.setText(azureConnectionService.getResource());
            textField3.setText(azureConnectionService.getGpt35TurboDeployment());
            textField4.setText(azureConnectionService.getGpt35Turbo16kDeployment());
            textField5.setText(azureConnectionService.getGpt4Deployment());
            textField6.setText(azureConnectionService.getGpt432kDeployment());
            textField7.setText(azureConnectionService.getGpt4oDeployment());
        } else {
            keyPanel.setVisible(false);
            defaultConnectionButton.setSelected(false);
            azureRadioButton.setSelected(false);
            urlPanel.setVisible(true);
            urlLabel.setText("Backend URL");
            httpLabel.setVisible(false);
            urlCompletionLabel.setVisible(false);
            gpt35TurboDeploymentNamePanel.setVisible(false);
            gpt35Turbo16kDeploymentNamePanel.setVisible(false);
            gpt4DeploymentNamePanel.setVisible(false);
            gpt432kDeploymentNamePanel.setVisible(false);
            textField1.setText("");
        }

        if (firebaseTokenService.isLoggedIn()) {
            accountEmailPanel.setVisible(false);
            accountPasswordPanel.setVisible(false);
            accountNameLabel.setVisible(true);
            accountNameLabel.setText(firebaseTokenService.getLoggedInUser());
            loginButton.setVisible(false);
            logOutButton.setVisible(true);
            codactorConnectionTypePanel.setVisible(true);
            codactorConnectionInfoPanel.setVisible(true);
            if (codactorConnectionService.getConnectionType() == null) {
                codactorConnectionService.setConnectionType(CodactorConnectionType.DEFAULT);
            }
            if (codactorConnectionService.getConnectionType() == CodactorConnectionType.DEFAULT) {
                keyPanel.setVisible(true);
                keyLabel.setText("OpenAI API Key");
                keyField.setText(defaultConnectionService.getOpenAiApiKey());
                defaultConnectionButton.setSelected(true);
                azureRadioButton.setSelected(false);
                enterpriseRadioButton.setSelected(false);
                urlPanel.setVisible(false);
                gpt35TurboDeploymentNamePanel.setVisible(false);
                gpt35Turbo16kDeploymentNamePanel.setVisible(false);
                gpt4DeploymentNamePanel.setVisible(false);
                gpt432kDeploymentNamePanel.setVisible(false);
            } else if (codactorConnectionService.getConnectionType() == CodactorConnectionType.AZURE) {
                keyPanel.setVisible(true);
                keyLabel.setText("Azure OpenAI API Key");
                defaultConnectionButton.setSelected(false);
                enterpriseRadioButton.setSelected(false);
                urlPanel.setVisible(true);
                urlLabel.setText("Azure Resource");
                httpLabel.setVisible(true);
                urlCompletionLabel.setVisible(true);
                gpt35TurboDeploymentNamePanel.setVisible(true);
                gpt35Turbo16kDeploymentNamePanel.setVisible(true);
                gpt4DeploymentNamePanel.setVisible(true);
                gpt432kDeploymentNamePanel.setVisible(true);
                keyField.setText(azureConnectionService.getKey());
                textField1.setText(azureConnectionService.getResource());
                textField3.setText(azureConnectionService.getGpt35TurboDeployment());
                textField4.setText(azureConnectionService.getGpt35Turbo16kDeployment());
                textField5.setText(azureConnectionService.getGpt4Deployment());
                textField6.setText(azureConnectionService.getGpt432kDeployment());
                textField7.setText(azureConnectionService.getGpt4oDeployment());
            } else {
                keyPanel.setVisible(false);
                defaultConnectionButton.setSelected(false);
                azureRadioButton.setSelected(false);
                urlPanel.setVisible(true);
                urlLabel.setText("Backend URL");
                httpLabel.setVisible(false);
                urlCompletionLabel.setVisible(false);
                gpt35TurboDeploymentNamePanel.setVisible(false);
                gpt35Turbo16kDeploymentNamePanel.setVisible(false);
                gpt4DeploymentNamePanel.setVisible(false);
                gpt432kDeploymentNamePanel.setVisible(false);
                textField1.setText("");
            }
        } else {
            accountNameLabel.setVisible(false);
            accountEmailPanel.setVisible(false);
            accountPasswordPanel.setVisible(false);
            loginButton.setVisible(true);
            logOutButton.setVisible(false);
            codactorConnectionTypePanel.setVisible(false);
            codactorConnectionInfoPanel.setVisible(false);
        }
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void applySettingsTo() {
        if (defaultConnectionButton.isSelected()) {
            codactorConnectionService.setConnectionType(CodactorConnectionType.DEFAULT);
            defaultConnectionService.setOpenAiApiKey(keyField.getText());
        } else if (azureRadioButton.isSelected()) {
            codactorConnectionService.setConnectionType(CodactorConnectionType.AZURE);
            azureConnectionService.setKey(keyField.getText());
            azureConnectionService.setResource(textField1.getText());
            azureConnectionService.setGpt35TurboDeployment(textField3.getText());
            azureConnectionService.setGpt35Turbo16kDeployment(textField4.getText());
            azureConnectionService.setGpt4Deployment(textField5.getText());
            azureConnectionService.setGpt432kDeployment(textField6.getText());
            azureConnectionService.setGpt4oDeployment(textField7.getText());
        } else {
            codactorConnectionService.setConnectionType(CodactorConnectionType.ENTERPRISE);
            //enterpriseConnectionService.setBackendUrl(textField1.getText());
        }

        modified = false; // reset modified after settings have been applied
    }

    public boolean isModified() {
        return modified;
    }



    public void reset() {
        CodactorConnectionType codactorConnectionType = codactorConnectionService.getConnectionType();
        defaultConnectionButton.setSelected(codactorConnectionType == CodactorConnectionType.DEFAULT);
        azureRadioButton.setSelected(codactorConnectionType == CodactorConnectionType.AZURE);
        enterpriseRadioButton.setSelected(codactorConnectionType == CodactorConnectionType.ENTERPRISE);
        if (codactorConnectionType == CodactorConnectionType.DEFAULT) {
            keyField.setText(defaultConnectionService.getOpenAiApiKey());
        } else {
            keyField.setText(azureConnectionService.getKey());
            textField1.setText(azureConnectionService.getResource());
            textField3.setText(azureConnectionService.getGpt35TurboDeployment());
            textField4.setText(azureConnectionService.getGpt35Turbo16kDeployment());
            textField5.setText(azureConnectionService.getGpt4Deployment());
            textField6.setText(azureConnectionService.getGpt432kDeployment());
            textField7.setText(azureConnectionService.getGpt4oDeployment());
        }



        // After resetting buttons, we also need to reset the visibility of panels
        accountPanel.setVisible(true);
        errorLabel.setVisible(false);

        modified = false; //reset modified after settings have been reset
     }
 }