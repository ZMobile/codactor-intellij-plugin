package com.translator.model.uml.draw.figure;

import org.jhotdraw.draw.TextFigure;

public interface LabeledMetadataFigure {
    String getMetadata();

    void setMetadata(String metadata);

    TextFigure getLabel();
}
