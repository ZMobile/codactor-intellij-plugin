package com.translator.model.uml.draw.io;


import com.intellij.openapi.project.Project;
import com.translator.model.uml.draw.io.xml.AdvancedNanoXMLDOMInput;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.io.DOMStorableInputOutputFormat;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.io.OutputFormat;
import org.jhotdraw.gui.datatransfer.InputStreamTransferable;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.nanoxml.NanoXMLDOMInput;
import org.jhotdraw.nanoxml.NanoXMLDOMOutput;
import org.jhotdraw.xml.DOMFactory;

import javax.swing.JComponent;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * An OutputFormat that can write Drawings with DOMStorable Figure's.
 * <p>
 * This class is here to support quick-and-dirty implementations of drawings
 * that can be read and written from/to output streams. For example, in student
 * projects.
 * <p>
 * This class should no be used as a means to implement long-term storage of
 * drawings, since it does not support structural changes that might occur in
 * a drawing application over time.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AdvancedDOMStorableInputOutputFormat extends DOMStorableInputOutputFormat {
    private Project project;
    private DOMFactory factory;
    private String mimeType;
    private String description;

    public AdvancedDOMStorableInputOutputFormat(Project project, DOMFactory factory) {
        this(project, factory, "Drawing", "xml", "image/x-jhotdraw");
    }

    public AdvancedDOMStorableInputOutputFormat(Project project,
                                                DOMFactory factory,
                                                String description, String fileExtension, String mimeType) {
        super(factory, description, fileExtension, mimeType);
        this.project = project;
        this.factory = factory;
        this.mimeType = mimeType;
        this.description = description;
    }

    @Override
    protected void read(URL url, InputStream in, Drawing drawing, LinkedList<Figure> figures) throws IOException {
        AdvancedNanoXMLDOMInput domi = new AdvancedNanoXMLDOMInput(project, factory, in);
        domi.openElement(factory.getName(drawing));
        domi.openElement("figures", 0);
        figures.clear();
        for (int i = 0, n = domi.getElementCount(); i < n; i++) {
            Figure f = (Figure) domi.readObject();
            figures.add(f);
        }
        domi.closeElement();
        domi.closeElement();
        drawing.basicAddAll(drawing.getChildCount(), figures);
    }

    @Override
    public void read(InputStream in, Drawing drawing, boolean replace) throws IOException {
        AdvancedNanoXMLDOMInput domi = new AdvancedNanoXMLDOMInput(project, factory, in);
        domi.openElement(factory.getName(drawing));
        if (replace) {
            drawing.removeAllChildren();
        }
        drawing.read(domi);
        domi.closeElement();
        domi.dispose();
    }


    public void read(Transferable t, Drawing drawing, boolean replace) throws UnsupportedFlavorException, IOException {
        LinkedList<Figure> figures = new LinkedList<Figure>();
        InputStream in = (InputStream) t.getTransferData(new DataFlavor(mimeType, description));
        AdvancedNanoXMLDOMInput domi = new AdvancedNanoXMLDOMInput(project, factory, in);
        domi.openElement("Drawing-Clip");
        for (int i = 0, n = domi.getElementCount(); i < n; i++) {
            Figure f = (Figure) domi.readObject(i);
            figures.add(f);
        }
        domi.closeElement();
        if (replace) {
            drawing.removeAllChildren();
        }
        drawing.addAll(figures);
    }
}

