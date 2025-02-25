/* @(#)OSXPaletteHandler.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package com.translator.view.uml;

import com.translator.view.uml.application.CodactorUmlBuilderOSXApplication;
import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.View;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Hides all registered floating palettes, if none of the registered view
 * windows have focus anymore.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CodactorUmlBuilderOSXPaletteHandler {
    private HashSet<Window> palettes = new HashSet<Window>();
    private HashMap<Window,View> windows = new HashMap<Window,View>();
    private javax.swing.Timer timer;
    private CodactorUmlBuilderOSXApplication app;
    private WindowFocusListener focusHandler = new WindowFocusListener() {
        /**
         * Invoked when the com.translator.io.listener.pacman.Window is set to be the focused com.translator.io.listener.pacman.Window, which means
         * that the com.translator.io.listener.pacman.Window, or one of its subcomponents, will receive keyboard
         * events.
         */
        @Override
        public void windowGainedFocus(WindowEvent e) {
            timer.stop();
            if (windows.get(e.getWindow()) != null) {
                app.setActiveView(windows.get(e.getWindow()));
                showPalettes();
            }
        }
        
        /**
         * Invoked when the com.translator.io.listener.pacman.Window is no longer the focused com.translator.io.listener.pacman.Window, which means
         * that keyboard events will no longer be delivered to the com.translator.io.listener.pacman.Window or any of
         * its subcomponents.
         */
        @Override
        public void windowLostFocus(WindowEvent e) {
            timer.restart();
        }
    };
    
    /** Creates a new instance. */
    public CodactorUmlBuilderOSXPaletteHandler(CodactorUmlBuilderOSXApplication app) {
        this.app = app;
        timer = new javax.swing.Timer(60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                maybeHidePalettes();
            }
        });
        timer.setRepeats(false);
    }
    
    public void add(Window window, @Nullable View view) {
        window.addWindowFocusListener(focusHandler);
        windows.put(window, view);
    }
    
    public void remove(Window window) {
        windows.remove(window);
        window.removeWindowFocusListener(focusHandler);
    }
    
    public void addPalette(Window palette) {
        palette.addWindowFocusListener(focusHandler);
        palettes.add(palette);
    }
    
    public void removePalette(Window palette) {
        palettes.remove(palette);
        palette.removeWindowFocusListener(focusHandler);
    }
    
    public Set<Window> getPalettes() {
        return Collections.unmodifiableSet(palettes);
    }


    private boolean isFocused(Window w) {
        if (w.isFocused()) return true;
        Window[] ownedWindows = w.getOwnedWindows();
        for (int i=0; i < ownedWindows.length; i++) {
            if (isFocused(ownedWindows[i])) {
                return true;
            }
        }
        return false;
    }
    private void maybeHidePalettes() {
        boolean hasFocus = false;
        for (Window window : windows.keySet()) {
            if (isFocused(window)) {
                hasFocus = true;
                break;
            }
        }
        if (! hasFocus && windows.size() > 0) {
            for (Window palette : palettes) {
                if (isFocused(palette)) {
                    hasFocus = true;
                    break;
                }
            }
        }
        if (! hasFocus) {
            for (Window palette : palettes) {
                palette.setVisible(false);
            }
        }
    }

    public void addWindow(Window window) {
        window.addWindowFocusListener(focusHandler);
        windows.put(window, null);
    }
    public void removeWindow(Window window) {
        windows.remove(window);
        window.removeWindowFocusListener(focusHandler);
    }

    public void showPalettes() {
        for (Window palette : palettes) {
            if (!palette.isVisible()) {
                timer.stop();
                palette.setVisible(true);
            }
        }
    }

    public void hidePalettes() {
        for (Window palette : palettes) {
            timer.stop();
            palette.setVisible(false);
        }
    }
}
