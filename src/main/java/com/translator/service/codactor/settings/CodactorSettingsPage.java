package com.translator.service.codactor.settings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CodactorSettingsPage {
    private CodactorSettings codactorSettings;
    private boolean modified;
    private JPanel rootPanel;
    private JLabel codactorConnectionLabel;
    private JRadioButton defaultConnectionButton;
    private JRadioButton azureRadioButton;
    private JRadioButton enterpriseRadioButton;
    private JTextField textField1;
    private JTextField textField2;
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

    public CodactorSettingsPage(CodactorSettings codactorSettings) {
        this.codactorSettings = codactorSettings;
        textField1.getDocument().addDocumentListener(textChangedListener);
        textField2.getDocument().addDocumentListener(textChangedListener);

        logOutButton.addActionListener(buttonChangedListener);
        loginButton.addActionListener(buttonChangedListener);
        defaultConnectionButton.addActionListener(buttonChangedListener);
        azureRadioButton.addActionListener(buttonChangedListener);
        enterpriseRadioButton.addActionListener(buttonChangedListener);
        logOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // perform logic here
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // perform logic here
            }
        });

        defaultConnectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                azureRadioButton.setSelected(false);
                enterpriseRadioButton.setSelected(false);
            }
        });

        azureRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                defaultConnectionButton.setSelected(false);
                enterpriseRadioButton.setSelected(false);
            }
        });

        enterpriseRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                defaultConnectionButton.setSelected(false);
                azureRadioButton.setSelected(false);
            }
        });
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void applySettingsTo() {
        codactorSettings.setConnectionType(defaultConnectionButton.isEnabled() ? "default" : azureRadioButton.isEnabled() ? "azure" : "enterprise");
        codactorSettings.setUrl(textField1.getText());
        codactorSettings.setKey(textField2.getText());

        modified = false; // reset modified after settings have been applied
    }

    public boolean isModified() {
        return modified;
    }

    public void reset() {
        defaultConnectionButton.setSelected(codactorSettings.getConnectionType().equalsIgnoreCase("default"));
        azureRadioButton.setSelected(codactorSettings.getConnectionType().equalsIgnoreCase("azure"));
        enterpriseRadioButton.setSelected(codactorSettings.getConnectionType().equalsIgnoreCase("enterprise"));
        textField1.setText(codactorSettings.getUrl());
        textField2.setText(codactorSettings.getKey());

        modified = false; //reset modified after settings have been reset
    }

    public CodactorSettings getCodactorSettings() {
        return codactorSettings;
    }

    public void setCodactorSettings(CodactorSettings codactorSettings) {
        this.codactorSettings = codactorSettings;
    }
}
