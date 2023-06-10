package com.translator.model.uml.draw.figure;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.LabeledLineConnectionFigure;
import org.jhotdraw.draw.connector.Connector;

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
        System.out.println("This gets calledaaaaa");
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
}
