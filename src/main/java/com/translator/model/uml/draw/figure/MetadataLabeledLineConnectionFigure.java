package com.translator.model.uml.draw.figure;

import org.jhotdraw.draw.LabeledLineConnectionFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

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
}
