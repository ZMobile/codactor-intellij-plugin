package com.translator.model.uml.draw.figure;

import org.jhotdraw.draw.LabeledLineConnectionFigure;

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
}
