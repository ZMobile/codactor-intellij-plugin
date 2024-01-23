package com.translator.view.uml.application;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.ApplicationModel;
import org.jhotdraw.app.View;
import org.jhotdraw.gui.URIChooser;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public interface CodactorUmlBuilderApplication extends Application {
    View startAndReturn(List<URI> uris);

    void createNewDetachedView(View view);

    void showPalettes();

    void hidePalettes();

    void setActiveView(@Nullable View newValue);
}


