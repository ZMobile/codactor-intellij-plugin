/* @(#)Main.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package com.translator.view.uml;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.OSXApplication;
import org.jhotdraw.app.SDIApplication;
import org.jhotdraw.samples.draw.DrawApplicationModel;
import org.jhotdraw.samples.draw.DrawView;
import org.jhotdraw.util.ResourceBundleUtil;
/**
 * Main entry point of the Draw sample application. Creates an {@link Application}
 * depending on the operating system we run, sets the {@link DrawApplicationModel}
 * and then launches the application. The application then creates
 * {@link org.jhotdraw.samples.draw.DrawView}s and menu bars as specified by the application model.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class Main {
    
    /** Creates a new instance. */
    public static void main(String[] args) {
        ResourceBundleUtil.setVerbose(true);

        Application app;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("mac")) {
            app = new CodactorUmlBuilderOSXApplication();
        } else if (os.startsWith("win")) {
            //app = new MDIApplication();
            app = new CodactorUmlBuilderOSXApplication();
        } else {
            app = new CodactorUmlBuilderSDIApplication();
        }

        CodactorUmlBuilderApplicationModel model = new CodactorUmlBuilderApplicationModel();
        model.setName("JHotDraw Draw");
        model.setVersion(Main.class.getPackage().getImplementationVersion());
        model.setCopyright("Copyright 2006-2009 (c) by the authors of JHotDraw and all its contributors.\n" +
                "This software is licensed under LGPL or Creative Commons 3.0 Attribution.");
        model.setViewFactory(CodactorUmlBuilderView::new);
        app.setModel(model);
        app.launch(args);
    }
    
}
