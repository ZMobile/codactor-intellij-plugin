package com.translator.view.uml.action;

import com.translator.view.uml.application.CodactorUmlBuilderOSXApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class CodactorUmlBuilderTogglePaletteAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    private Window palette;
    private CodactorUmlBuilderOSXApplication app;
    private WindowListener windowHandler;

    public CodactorUmlBuilderTogglePaletteAction(CodactorUmlBuilderOSXApplication app, Window palette, String label) {
        super(label);
        this.app = app;
        this.windowHandler = new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                CodactorUmlBuilderTogglePaletteAction.this.putValue("SwingSelectedKey", false);
            }
        };
        this.putValue("SwingSelectedKey", false);
        this.setPalette(palette);
    }

    public void putValue(String key, Object newValue) {
        super.putValue(key, newValue);
    }

    public void setPalette(Window newValue) {
        if (this.palette != null) {
            this.palette.removeWindowListener(this.windowHandler);
        }

        this.palette = newValue;
        if (this.palette != null) {
            this.palette.addWindowListener(this.windowHandler);
            if (this.getValue("SwingSelectedKey") == Boolean.TRUE) {
                this.app.addPalette(this.palette);
                this.palette.setVisible(true);
            } else {
                this.app.removePalette(this.palette);
                this.palette.setVisible(false);
            }
        }

    }

    public void actionPerformed(ActionEvent e) {
        if (this.palette != null) {
            boolean b = (Boolean)this.getValue("SwingSelectedKey");
            if (b) {
                this.app.addPalette(this.palette);
                this.palette.setVisible(true);
            } else {
                this.app.removePalette(this.palette);
                this.palette.setVisible(false);
            }
        }

    }
}
