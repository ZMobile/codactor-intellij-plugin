package com.translator.model.uml.draw.figure;

import org.jhotdraw.draw.RectangleFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class LabeledRectangleFigure extends RectangleFigure implements LabeledMetadataFigure {
    private TextFigure label;
    private String metadata;

    public LabeledRectangleFigure() {
        super();
        this.label = new TextFigure();
    }

    public LabeledRectangleFigure(String name) {
        super();
        this.label = new TextFigure();
        this.label.setText(name);
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


    public void write(DOMOutput out) throws IOException {
        Rectangle2D.Double r = this.getBounds();
        out.addAttribute("x", r.x);
        out.addAttribute("y", r.y);
        out.addAttribute("w", r.width);
        out.addAttribute("h", r.height);
        out.addAttribute("metadata", metadata);
        System.out.println("Woo testo: " + label.getText());
        out.addAttribute("label", label.getText());
        super.writeAttributes(out);
    }

    public void read(DOMInput in) throws IOException {
        double x = in.getAttribute("x", 0.0);
        double y = in.getAttribute("y", 0.0);
        double w = in.getAttribute("w", 0.0);
        double h = in.getAttribute("h", 0.0);
        this.setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
        this.setMetadata(in.getAttribute("metadata", null));
        this.label = new TextFigure();
        this.label.setText(in.getAttribute("label", null));
        super.readAttributes(in);
        setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
    }
}

