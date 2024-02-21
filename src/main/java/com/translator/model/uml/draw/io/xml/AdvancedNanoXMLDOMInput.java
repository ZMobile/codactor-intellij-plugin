package com.translator.model.uml.draw.io.xml;

import com.intellij.openapi.project.Project;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLParserFactory;
import org.jhotdraw.app.Disposable;
import org.jhotdraw.nanoxml.NanoXMLDOMInput;
import org.jhotdraw.xml.DOMFactory;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMStorable;

import org.jhotdraw.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

/**
 * NanoXMLDOMInput.
 * <p>
 * Design pattern:<br>
 * Name: Adapter.<br>
 * Role: Adapter.<br>
 * Partners: {@link net.n3.nanoxml.XMLElement} as Adaptee.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class AdvancedNanoXMLDOMInput extends NanoXMLDOMInput implements DOMInput {
    private Project project;

    public AdvancedNanoXMLDOMInput(Project project, DOMFactory factory, InputStream in) throws IOException {
        super(factory, in);
        this.project = project;
    }
    public AdvancedNanoXMLDOMInput(Project project, DOMFactory factory, Reader in) throws IOException {
        super(factory, in);
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}

