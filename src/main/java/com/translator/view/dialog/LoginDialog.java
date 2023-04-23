package com.translator.view.dialog;

import com.translator.dao.firebase.FirebaseTokenService;
import com.translator.service.openai.OpenAiApiKeyService;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LoginDialog extends JDialog {
    private FirebaseTokenService firebaseTokenService;

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginDialog(FirebaseTokenService firebaseTokenService) {
        this.firebaseTokenService = firebaseTokenService;
        setTitle("Login");
        setLocationRelativeTo(null);
        setModal(true);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);

        JLabel emailLabel = new JLabel("Email:");
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(emailLabel, c);

        emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(240, emailField.getPreferredSize().height));
        emailField.getActionMap().put("paste", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                String data;
                try {
                    data = (String) clipboard.getData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException | IOException ex) {
                    throw new RuntimeException(ex);
                }
                emailField.replaceSelection(data);
            }
        });
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(emailField, c);

        JLabel passwordLabel = new JLabel("Password:");
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.NONE;
        panel.add(passwordLabel, c);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, passwordField.getPreferredSize().height));
        passwordField.getActionMap().put("paste", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                String data;
                try {
                    data = (String) clipboard.getData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException | IOException ex) {
                    throw new RuntimeException(ex);
                }
                passwordField.replaceSelection(data);
            }
        });
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(passwordField, c);

        JButton loginButton = new JButton("Login");
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        panel.add(loginButton, c);
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            if (firebaseTokenService.login(email, password)) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JLabel forgotPasswordLabel = new JLabel("Forgot password");
        forgotPasswordLabel.setForeground(Color.BLUE);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        c.gridx = 0;
        c.gridy = 5;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(forgotPasswordLabel, c);
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    // Replace "your_password_reset_url" with the actual URL of the password reset page
                    Desktop.getDesktop().browse(new URI("https://www.codactor.com/forgot-password"));
                } catch (IOException | URISyntaxException ex) {
                    // Handle any exceptions that might occur when launching the browser
                    ex.printStackTrace();
                }
            }
        });

        JLabel createAccountLabel = new JLabel("Create Account");
        createAccountLabel.setForeground(Color.BLUE);
        createAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(createAccountLabel, c);
        createAccountLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    // Replace "your_password_reset_url" with the actual URL of the password reset page
                    Desktop.getDesktop().browse(new URI("https://www.codactor.com/signup"));
                } catch (IOException | URISyntaxException ex) {
                    // Handle any exceptions that might occur when launching the browser
                    ex.printStackTrace();
                }
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null); // centers the dialog
        setResizable(false);
        setVisible(true);
    }
}
