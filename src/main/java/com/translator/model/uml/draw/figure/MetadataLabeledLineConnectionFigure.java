package com.translator.model.uml.draw.figure;

import org.jhotdraw.draw.LabeledLineConnectionFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.ResizeHandleKit;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class MetadataLabeledLineConnectionFigure extends LabeledLineConnectionFigure {
    private String metadata;

    public MetadataLabeledLineConnectionFigure() {
        super();

        this.metadata = null;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public void swapFigures() {
        willChange();
        Connector startConnector = getStartConnector();
        Connector endConnector = getEndConnector();

        setStartPoint(startConnector.findStart(this));
        setEndPoint(endConnector.findEnd(this));
        setStartConnector(endConnector);
        setEndConnector(startConnector);

// Update the connection
        updateConnection();

        changed();
    }

    public void write(DOMOutput out) throws IOException {
        this.writePoints(out);
        this.writeLiner(out);
        out.addAttribute("metadata", metadata);
        this.writeAttributes(out);
    }

    public void read(DOMInput in) throws IOException {
        this.readAttributes(in);
        this.readLiner(in);
        this.readPoints(in);
        this.setMetadata(in.getAttribute("metadata", null));
        //super.readAttributes(in);
        //super.setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
    }

    @Override
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel) {
            case -1:
                handles.add(new BoundsOutlineHandle(this,false,true));
                break;
            case 0:
                ResizeHandleKit.addResizeHandles(this, handles);
                break;
        }
        return handles;
    }
}
