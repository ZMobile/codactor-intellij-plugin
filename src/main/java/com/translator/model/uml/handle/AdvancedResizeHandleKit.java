package com.translator.model.uml.handle;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.event.TransformRestoreEdit;
import org.jhotdraw.draw.handle.*;
import org.jhotdraw.draw.locator.Locator;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.util.ResourceBundleUtil;
import javax.swing.undo.AbstractUndoableEdit;
import com.intellij.openapi.command.CommandProcessor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

public class AdvancedResizeHandleKit {
    private static final boolean DEBUG = false;


    public AdvancedResizeHandleKit() {
    }

    public static void addCornerResizeHandles(Figure f, Collection<Handle> handles) {
        if (f.isTransformable()) {
            handles.add(southEast(f));
            handles.add(southWest(f));
            handles.add(northEast(f));
            handles.add(northWest(f));
        }

    }

    public static void addEdgeResizeHandles(Figure f, Collection<Handle> handles) {
        if (f.isTransformable()) {
            handles.add(south(f));
            handles.add(north(f));
            handles.add(east(f));
            handles.add(west(f));
        }

    }

    public static void addResizeHandles(Figure f, Collection<Handle> handles) {
        handles.add(new BoundsOutlineHandle(f));
        if (f.isTransformable()) {
            addCornerResizeHandles(f, handles);
            addEdgeResizeHandles(f, handles);
        }

    }

    public static Handle south(Figure owner) {
        return new AdvancedResizeHandleKit.SouthHandle(owner);
    }

    public static Handle southEast(Figure owner) {
        return new AdvancedResizeHandleKit.SouthEastHandle(owner);
    }

    public static Handle southWest(Figure owner) {
        return new AdvancedResizeHandleKit.SouthWestHandle(owner);
    }

    public static Handle north(Figure owner) {
        return new AdvancedResizeHandleKit.NorthHandle(owner);
    }

    public static Handle northEast(Figure owner) {
        return new AdvancedResizeHandleKit.NorthEastHandle(owner);
    }

    public static Handle northWest(Figure owner) {
        return new AdvancedResizeHandleKit.NorthWestHandle(owner);
    }

    public static Handle east(Figure owner) {
        return new AdvancedResizeHandleKit.EastHandle(owner);
    }

    public static Handle west(Figure owner) {
        return new AdvancedResizeHandleKit.WestHandle(owner);
    }

    private static class SouthHandle extends ResizeHandle {
        SouthHandle(Figure owner) {
            super(owner, RelativeLocator.south(true));
        }

        protected void trackStepNormalized(Point2D.Double p, boolean keepAspect) {
            this.setBounds(new Point2D.Double(this.sb.x, this.sb.y), new Point2D.Double(this.sb.x + this.sb.width, Math.max(this.sb.y + 1.0, p.y)));
        }

        public void keyPressed(KeyEvent evt) {
            if (!this.getOwner().isTransformable()) {
                evt.consume();
            } else {
                Rectangle2D.Double r = this.getOwner().getBounds();
                switch (evt.getKeyCode()) {
                    case 37:
                        evt.consume();
                        break;
                    case 38:
                        if (r.height > 1.0) {
                            this.setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width, r.y + r.height - 1.0));
                        }

                        evt.consume();
                        break;
                    case 39:
                        evt.consume();
                        break;
                    case 40:
                        this.setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width, r.y + r.height + 1.0));
                        evt.consume();
                }

            }
        }

        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(this.getOwner().isTransformable() ? 9 : 0);
        }
    }

    private static class SouthEastHandle extends ResizeHandle {
        SouthEastHandle(Figure owner) {
            super(owner, RelativeLocator.southEast(true));
        }

        protected void trackStepNormalized(Point2D.Double p, boolean keepAspect) {
            double nx = Math.max(this.sb.x + 1.0, p.x);
            double ny = Math.max(this.sb.y + 1.0, p.y);
            if (keepAspect) {
                double nxx = this.sb.x + Math.max(1.0, (p.y - this.sb.y) / this.aspectRatio);
                if (nxx >= p.x) {
                    nx = nxx;
                } else {
                    ny = this.sb.y + Math.max(1.0, (p.x - this.sb.x) * this.aspectRatio);
                }
            }

            this.setBounds(new Point2D.Double(this.sb.x, this.sb.y), new Point2D.Double(nx, ny));
        }

        public void keyPressed(KeyEvent evt) {
            if (!this.getOwner().isTransformable()) {
                evt.consume();
            } else {
                Rectangle2D.Double r = this.getOwner().getBounds();
                switch (evt.getKeyCode()) {
                    case 37:
                        if (r.width > 1.0) {
                            this.setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width - 1.0, r.y + r.height));
                        }

                        evt.consume();
                        break;
                    case 38:
                        if (r.height > 1.0) {
                            this.setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width, r.y + r.height - 1.0));
                        }

                        evt.consume();
                        break;
                    case 39:
                        this.setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width + 1.0, r.y + r.height));
                        evt.consume();
                        break;
                    case 40:
                        this.setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width, r.y + r.height + 1.0));
                        evt.consume();
                }

            }
        }

        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(this.getOwner().isTransformable() ? 5 : 0);
        }
    }

    private static class SouthWestHandle extends ResizeHandle {
        SouthWestHandle(Figure owner) {
            super(owner, RelativeLocator.southWest(true));
        }

        protected void trackStepNormalized(Point2D.Double p, boolean keepAspect) {
            double nx = Math.min(this.sb.x + this.sb.width - 1.0, p.x);
            double ny = Math.max(this.sb.y + 1.0, p.y);
            if (keepAspect) {
                double nxx = this.sb.x + this.sb.width - Math.max(1.0, (p.y - this.sb.y) / this.aspectRatio);
                if (nxx <= p.x) {
                    nx = nxx;
                } else {
                    ny = this.sb.y + Math.max(1.0, (this.sb.x + this.sb.width - 1.0 - p.x) * this.aspectRatio);
                }
            }

            this.setBounds(new Point2D.Double(nx, this.sb.y), new Point2D.Double(this.sb.x + this.sb.width, ny));
        }

        public void keyPressed(KeyEvent evt) {
            if (!this.getOwner().isTransformable()) {
                evt.consume();
            } else {
                Rectangle2D.Double r = this.getOwner().getBounds();
                switch (evt.getKeyCode()) {
                    case 37:
                        this.setBounds(new Point2D.Double(r.x - 1.0, r.y), new Point2D.Double(r.x + r.width, r.y + r.height));
                        evt.consume();
                        break;
                    case 38:
                        if (r.height > 1.0) {
                            this.setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width, r.y + r.height - 1.0));
                        }

                        evt.consume();
                        break;
                    case 39:
                        if (r.width > 1.0) {
                            this.setBounds(new Point2D.Double(r.x + 1.0, r.y), new Point2D.Double(r.x + r.width, r.y + r.height));
                        }

                        evt.consume();
                        break;
                    case 40:
                        this.setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width, r.y + r.height + 1.0));
                        evt.consume();
                }

            }
        }

        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(this.getOwner().isTransformable() ? 4 : 0);
        }
    }

    private static class NorthHandle extends ResizeHandle {
        NorthHandle(Figure owner) {
            super(owner, RelativeLocator.north(true));
        }

        protected void trackStepNormalized(Point2D.Double p, boolean keepAspect) {
            this.setBounds(new Point2D.Double(this.sb.x, Math.min(this.sb.y + this.sb.height - 1.0, p.y)), new Point2D.Double(this.sb.x + this.sb.width, this.sb.y + this.sb.height));
        }

        public void keyPressed(KeyEvent evt) {
            if (!this.getOwner().isTransformable()) {
                evt.consume();
            } else {
                Rectangle2D.Double r = this.getOwner().getBounds();
                switch (evt.getKeyCode()) {
                    case 37:
                    case 39:
                        evt.consume();
                        break;
                    case 38:
                        this.setBounds(new Point2D.Double(r.x, r.y - 1.0), new Point2D.Double(r.x + r.width, r.y + r.height));
                        evt.consume();
                        break;
                    case 40:
                        if (r.height > 1.0) {
                            this.setBounds(new Point2D.Double(r.x, r.y + 1.0), new Point2D.Double(r.x + r.width, r.y + r.height));
                        }

                        evt.consume();
                }

            }
        }

        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(this.getOwner().isTransformable() ? 8 : 0);
        }
    }

    private static class NorthEastHandle extends ResizeHandle {
        NorthEastHandle(Figure owner) {
            super(owner, RelativeLocator.northEast(true));
        }

        protected void trackStepNormalized(Point2D.Double p, boolean keepAspect) {
            double nx = Math.max(this.sb.x + 1.0, p.x);
            double ny = Math.min(this.sb.y + this.sb.height - 1.0, p.y);
            if (keepAspect) {
                double nxx = this.sb.x + this.sb.width - 1.0 + Math.max(1.0, (this.sb.y - p.y) / this.aspectRatio);
                if (nxx >= p.x) {
                    nx = nxx;
                } else {
                    ny = this.sb.y + this.sb.height - Math.max(1.0, (p.x - this.sb.x) * this.aspectRatio);
                }
            }

            this.setBounds(new Point2D.Double(this.sb.x, ny), new Point2D.Double(nx, this.sb.y + this.sb.height));
        }

        public void keyPressed(KeyEvent evt) {
            if (!this.getOwner().isTransformable()) {
                evt.consume();
            } else {
                Rectangle2D.Double r = this.getOwner().getBounds();
                switch (evt.getKeyCode()) {
                    case 37:
                        if (r.width > 1.0) {
                            this.setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width - 1.0, r.y + r.height));
                        }

                        evt.consume();
                        break;
                    case 38:
                        this.setBounds(new Point2D.Double(r.x, r.y - 1.0), new Point2D.Double(r.x + r.width, r.y + r.height));
                        evt.consume();
                        break;
                    case 39:
                        this.setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width + 1.0, r.y + r.height));
                        evt.consume();
                        break;
                    case 40:
                        if (r.height > 1.0) {
                            this.setBounds(new Point2D.Double(r.x, r.y + 1.0), new Point2D.Double(r.x + r.width, r.y + r.height));
                        }

                        evt.consume();
                }

            }
        }

        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(this.getOwner().isTransformable() ? 7 : 0);
        }
    }

    private static class NorthWestHandle extends ResizeHandle {
        NorthWestHandle(Figure owner) {
            super(owner, RelativeLocator.northWest(true));
        }

        protected void trackStepNormalized(Point2D.Double p, boolean keepAspect) {
            double nx = Math.min(this.sb.x + this.sb.width - 1.0, p.x);
            double ny = Math.min(this.sb.y + this.sb.height - 1.0, p.y);
            if (keepAspect) {
                double nxx = this.sb.x - Math.max(1.0, (this.sb.y - p.y) / this.aspectRatio);
                if (nxx <= p.x) {
                    nx = nxx;
                } else {
                    ny = this.sb.y - Math.max(1.0, (this.sb.x - p.x) * this.aspectRatio);
                }
            }

            this.setBounds(new Point2D.Double(nx, ny), new Point2D.Double(this.sb.x + this.sb.width, this.sb.y + this.sb.height));
        }

        public void keyPressed(KeyEvent evt) {
            if (!this.getOwner().isTransformable()) {
                evt.consume();
            } else {
                Rectangle2D.Double r = this.getOwner().getBounds();
                switch (evt.getKeyCode()) {
                    case 37:
                        this.setBounds(new Point2D.Double(r.x - 1.0, r.y), new Point2D.Double(r.x + r.width, r.y + r.height));
                        evt.consume();
                        break;
                    case 38:
                        this.setBounds(new Point2D.Double(r.x, r.y - 1.0), new Point2D.Double(r.x + r.width, r.y + r.height));
                        evt.consume();
                        break;
                    case 39:
                        if (r.width > 1.0) {
                            this.setBounds(new Point2D.Double(r.x + 1.0, r.y), new Point2D.Double(r.x + r.width, r.y + r.height));
                        }

                        evt.consume();
                        break;
                    case 40:
                        if (r.height > 1.0) {
                            this.setBounds(new Point2D.Double(r.x, r.y + 1.0), new Point2D.Double(r.x + r.width, r.y + r.height));
                        }

                        evt.consume();
                }

            }
        }

        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(this.getOwner().isTransformable() ? 6 : 0);
        }
    }

    private static class EastHandle extends ResizeHandle {
        EastHandle(Figure owner) {
            super(owner, RelativeLocator.east(true));
        }

        protected void trackStepNormalized(Point2D.Double p, boolean keepAspect) {
            this.setBounds(new Point2D.Double(this.sb.x, this.sb.y), new Point2D.Double(Math.max(this.sb.x + 1.0, p.x), this.sb.y + this.sb.height));
        }

        public void keyPressed(KeyEvent evt) {
            if (!this.getOwner().isTransformable()) {
                evt.consume();
            } else {
                Rectangle2D.Double r = this.getOwner().getBounds();
                switch (evt.getKeyCode()) {
                    case 37:
                        if (r.width > 1.0) {
                            this.setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width - 1.0, r.y + r.height));
                        }

                        evt.consume();
                        break;
                    case 38:
                    case 40:
                        evt.consume();
                        break;
                    case 39:
                        this.setBounds(new Point2D.Double(r.x, r.y), new Point2D.Double(r.x + r.width + 1.0, r.y + r.height));
                        evt.consume();
                }

            }
        }

        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(this.getOwner().isTransformable() ? 11 : 0);
        }
    }

    private static class WestHandle extends ResizeHandle {
        WestHandle(Figure owner) {
            super(owner, RelativeLocator.west(true));
        }

        protected void trackStepNormalized(Point2D.Double p, boolean keepAspect) {
            this.setBounds(new Point2D.Double(Math.min(this.sb.x + this.sb.width - 1.0, p.x), this.sb.y), new Point2D.Double(this.sb.x + this.sb.width, this.sb.y + this.sb.height));
        }

        public void keyPressed(KeyEvent evt) {
            if (!this.getOwner().isTransformable()) {
                evt.consume();
            } else {
                Rectangle2D.Double r = this.getOwner().getBounds();
                switch (evt.getKeyCode()) {
                    case 37:
                        this.setBounds(new Point2D.Double(r.x - 1.0, r.y), new Point2D.Double(r.x + r.width, r.y + r.height));
                        evt.consume();
                        break;
                    case 38:
                    case 40:
                        evt.consume();
                        break;
                    case 39:
                        if (r.width > 1.0) {
                            this.setBounds(new Point2D.Double(r.x + 1.0, r.y), new Point2D.Double(r.x + r.width, r.y + r.height));
                        }

                        evt.consume();
                }

            }
        }

        public Cursor getCursor() {
            return Cursor.getPredefinedCursor(this.getOwner().isTransformable() ? 10 : 0);
        }
    }

    private static class ResizeHandle extends LocatorHandle {
        private int sx;
        private int sy;
        private Object geometry;
        protected Rectangle2D.Double sb;
        double aspectRatio;
        private boolean isTransformableCache;


        ResizeHandle(Figure owner, Locator loc) {
            super(owner, loc);
        }

        public String getToolTipText(Point p) {
            ResourceBundleUtil labels = DrawLabels.getLabels();
            return labels.getString("handle.resize.toolTipText");
        }

        public void draw(Graphics2D g) {
            if (this.getEditor().getTool().supportsHandleInteraction()) {
                if (this.getOwner().isTransformable()) {
                    this.drawRectangle(g, (Color)this.getEditor().getHandleAttribute(HandleAttributeKeys.RESIZE_HANDLE_FILL_COLOR), (Color)this.getEditor().getHandleAttribute(HandleAttributeKeys.RESIZE_HANDLE_STROKE_COLOR));
                } else {
                    this.drawRectangle(g, (Color)this.getEditor().getHandleAttribute(HandleAttributeKeys.NULL_HANDLE_FILL_COLOR), (Color)this.getEditor().getHandleAttribute(HandleAttributeKeys.NULL_HANDLE_STROKE_COLOR));
                }
            } else {
                this.drawRectangle(g, (Color)this.getEditor().getHandleAttribute(HandleAttributeKeys.HANDLE_FILL_COLOR_DISABLED), (Color)this.getEditor().getHandleAttribute(HandleAttributeKeys.HANDLE_STROKE_COLOR_DISABLED));
            }

        }

        public void trackStart(Point anchor, int modifiersEx) {
            this.isTransformableCache = this.getOwner().isTransformable();
            if (this.isTransformableCache) {
                this.geometry = this.getOwner().getTransformRestoreData();
                Point location = this.getLocation();
                this.sx = -anchor.x + location.x;
                this.sy = -anchor.y + location.y;
                this.sb = this.getOwner().getBounds();
                this.aspectRatio = this.sb.height / this.sb.width;
            }
        }

        public void trackStep(Point anchor, Point lead, int modifiersEx) {
            if (this.isTransformableCache) {
                Point2D.Double p = this.view.viewToDrawing(new Point(lead.x + this.sx, lead.y + this.sy));
                this.view.getConstrainer().constrainPoint(p);
                if (this.getOwner().get(AttributeKeys.TRANSFORM) != null) {
                    try {
                        ((AffineTransform)this.getOwner().get(AttributeKeys.TRANSFORM)).inverseTransform(p, p);
                    } catch (NoninvertibleTransformException var6) {
                    }
                }

                this.trackStepNormalized(p, (modifiersEx & 704) != 0);
            }
        }

        public void trackEnd(Point anchor, Point lead, int modifiersEx) {
            if (this.isTransformableCache) {
                //this.fireUndoableEditHappened(new TransformRestoreEdit(this.getOwner(), this.geometry, this.getOwner().getTransformRestoreData()));
                System.out.println("this is called 2");
                // add into CommandProcessor runUndoTransparentAction
                //here we use IntelliJ's CommandProcessor API:
                CommandProcessor.getInstance().runUndoTransparentAction(() ->
                    fireUndoableEditHappened(new TransformRestoreEdit(this.getOwner(), this.geometry, this.getOwner().getTransformRestoreData())));
            }
        }


        protected void trackStepNormalized(Point2D.Double p, boolean keepAspect) {
        }

        protected void setBounds(Point2D.Double anchor, Point2D.Double lead) {
            Figure f = this.getOwner();
            f.willChange();
            f.setBounds(anchor, lead);
            f.changed();
        }
    }
}