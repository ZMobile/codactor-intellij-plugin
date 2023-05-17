package com.translator.model.uml.draw.figure;

import org.jhotdraw.draw.RectangleFigure;
import org.jhotdraw.draw.TextFigure;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class LabeledRectangleFigure extends RectangleFigure {
    private TextFigure label;
    private String metadata;

    public LabeledRectangleFigure(String name) {
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

    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        label.draw(g);
    }

    @Override
    public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
        super.setBounds(anchor, lead);

        // Position the label with its center aligned with the center of the rectangle
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

