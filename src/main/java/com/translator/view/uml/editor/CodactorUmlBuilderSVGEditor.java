package com.translator.view.uml.editor;

import com.google.inject.Injector;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.translator.CodactorInjector;
import com.translator.view.uml.CodactorUmlBuilderApplicationModel;
import com.translator.view.uml.action.CodactorUmlBuilderTogglePaletteAction;
import com.translator.view.uml.application.CodactorUmlBuilderApplication;
import com.translator.view.uml.factory.CodactorUmlBuilderApplicationModelFactory;
import com.translator.view.uml.factory.CodactorUmlBuilderViewFactory;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.ActionUtil;
import org.jhotdraw.app.action.edit.*;
import org.jhotdraw.app.action.file.*;
import org.jhotdraw.app.action.window.FocusWindowAction;
import org.jhotdraw.app.action.window.MaximizeWindowAction;
import org.jhotdraw.app.action.window.MinimizeWindowAction;
import org.jhotdraw.app.action.window.TogglePaletteAction;
import org.jhotdraw.app.osx.OSXAdapter;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.gui.Worker;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.samples.svg.io.SVGInputFormat;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.prefs.PreferencesUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.prefs.Preferences;

public class CodactorUmlBuilderSVGEditor extends UserDataHolderBase implements FileEditor {
    private final CodactorUmlBuilderApplication application;
    private final JComponent myComponent;
    private final View view;
    private final VirtualFile file;

    public CodactorUmlBuilderSVGEditor(Project project, VirtualFile file) {
        this.file = file;
        Injector injector = CodactorInjector.getInstance().getInjector(project);
        this.application = injector.getInstance(CodactorUmlBuilderApplication.class);
        URI uri = null;
        try {
            uri = new URI(file.getUrl());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        List<URI> uriList = new ArrayList<>();
        uriList.add(uri);
        this.view = application.startAndReturn(uriList);

        this.myComponent = new JPanel(new BorderLayout());
        this.myComponent.add(view.getComponent(), BorderLayout.CENTER);
    }

    @Override
    public VirtualFile getFile() {
        return file;
    }

    @Override
    public @NotNull JComponent getComponent() {
        return myComponent;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return myComponent;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return "Codactor Uml Builder SVG Editor";
    }

    @Override
    public void setState(@NotNull FileEditorState state) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {

    }

    public void activateView() {
        application.showPalettes();
        application.setActiveView(view);
    }

    public void deactivateView() {
        application.hidePalettes();
        application.setActiveView(null);
    }
}