package com.translator.model.uml.draw.figure;

import org.jhotdraw.draw.*;
import java.awt.*;
import java.awt.geom.*;

public class LabeledDiamondFigure extends DiamondFigure implements LabeledMetadataFigure {
    private TextFigure label;
    private String metadata;

    public LabeledDiamondFigure(String name) {
        super();

        label = new TextFigure();
        label.setText(name);

        this.metadata = null;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public TextFigure getLabel() {
        return label;
    }

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        label.draw(g);
    }

    @Override
    public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
        super.setBounds(anchor, lead);

        // Position the label in the center of the triangle
        Rectangle2D.Double bounds = getBounds();
        double labelWidth = label.getPreferredSize().width;
        double labelHeight = label.getPreferredSize().height;
        Point2D.Double labelPosition = new Point2D.Double(
                bounds.getCenterX() - labelWidth / 2.0,
                bounds.getCenterY() - labelHeight / 2.0
        );
        label.setBounds(labelPosition, labelPosition);
    }
}
