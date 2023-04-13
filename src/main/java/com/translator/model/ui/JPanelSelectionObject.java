package com.translator.model.ui;

import javax.swing.*;

public class JPanelSelectionObject {
    private String name;
    private JPanel panel;

    public JPanelSelectionObject(String name, JPanel panel) {
        this.name = name;
        this.panel = panel;
    }

    public String getName() {
        return name;
    }

    public JPanel getPanel() {
        return panel;
    }
}