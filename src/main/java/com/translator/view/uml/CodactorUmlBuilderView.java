/* @(#)DrawView.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package com.translator.view.uml;

import com.google.inject.Inject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.translator.model.uml.draw.io.AdvancedDOMStorableInputOutputFormat;
import com.translator.service.uml.undo.AdvancedUndoRedoManager;
import com.translator.view.uml.factory.CodactorUmlBuilderDrawFigureFactory;
import com.translator.view.uml.factory.adapter.CustomMouseAdapterFactory;
import org.jhotdraw.app.AbstractView;
import org.jhotdraw.app.ApplicationLabels;
import org.jhotdraw.app.action.edit.RedoAction;
import org.jhotdraw.app.action.edit.UndoAction;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.draw.io.*;
import org.jhotdraw.draw.print.DrawingPageable;
import org.jhotdraw.gui.PlacardScrollPaneLayout;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.samples.draw.DrawFigureFactory;
import org.jhotdraw.undo.UndoRedoManager;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.print.Pageable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

/**
 * Provides a view on a drawing.
 * <p>
 * See {@link org.jhotdraw.app.View} interface on how this view interacts with an application.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CodactorUmlBuilderView extends AbstractView {
      private static final long serialVersionUID = 1L;
      private String filePath;
      private Project project;
    /**
     * Each DrawView uses its own undo redo manager.
     * This allows for undoing and redoing actions per view.
     */
    private AdvancedUndoRedoManager undo;
    
    /**
     * Depending on the type of an application, there may be one editor per
     * view, or a single shared editor for all views.
     */
    private DrawingEditor editor;

    private CustomMouseAdapterFactory customMouseAdapterFactory;

    /**
     * Creates a new view.
     */
    @Inject
    public CodactorUmlBuilderView(Project project,
                                  CustomMouseAdapterFactory customMouseAdapterFactory) {
        this.project = project;
        this.customMouseAdapterFactory = customMouseAdapterFactory;

        initComponents();
        
        scrollPane.setLayout(new PlacardScrollPaneLayout());
        scrollPane.setBorder(new EmptyBorder(0,0,0,0));
        
        setEditor(new DefaultDrawingEditor());
        undo = new AdvancedUndoRedoManager(project);
        view.setDrawing(createDrawing());
        view.getDrawing().addUndoableEditListener(undo);
        initActions();
        undo.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                setHasUnsavedChanges(undo.hasSignificantEdits());
            }
        });

        
        ResourceBundleUtil labels = ApplicationLabels.getLabels();
        
        JPanel placardPanel = new JPanel(new BorderLayout());
        javax.swing.AbstractButton pButton;
        pButton = ButtonFactory.createZoomButton(view);
        pButton.putClientProperty("Quaqua.Button.style","placard");
        pButton.putClientProperty("Quaqua.Component.visualMargin",new Insets(0,0,0,0));
        pButton.setFont(UIManager.getFont("SmallSystemFont"));
        placardPanel.add(pButton, BorderLayout.WEST);
        pButton = ButtonFactory.createToggleGridButton(view);
        pButton.putClientProperty("Quaqua.Button.style","placard");
        pButton.putClientProperty("Quaqua.Component.visualMargin",new Insets(0,0,0,0));
        pButton.setFont(UIManager.getFont("SmallSystemFont"));
        labels.configureToolBarButton(pButton, "view.toggleGrid.placard");
        placardPanel.add(pButton, BorderLayout.EAST);
        scrollPane.add(placardPanel, JScrollPane.LOWER_LEFT_CORNER);
    }
    
    /**
     * Creates a new Drawing for this view.
     */
    protected Drawing createDrawing() {
        Drawing drawing = new QuadTreeDrawing();
        DOMStorableInputOutputFormat ioFormat =
                //All hell breaks loose when this is active-- we need to figure out why
                new AdvancedDOMStorableInputOutputFormat(project, new CodactorUmlBuilderDrawFigureFactory());
        
        drawing.addInputFormat(ioFormat);
        ImageFigure prototype = new ImageFigure();
        drawing.addInputFormat(new ImageInputFormat(prototype));
        drawing.addInputFormat(new TextInputFormat(new TextFigure()));
        TextAreaFigure taf = new TextAreaFigure();
        taf.setBounds(new Point2D.Double(10,10), new Point2D.Double(60,40));
        drawing.addInputFormat(new TextInputFormat(taf));
        
        drawing.addOutputFormat(ioFormat);
        drawing.addOutputFormat(new ImageOutputFormat());
        return drawing;
    }
    
    
    /**
     * Creates a Pageable object for printing the view.
     */
    public Pageable createPageable() {
        return new DrawingPageable(view.getDrawing());
        
    }
    
    
    /**
     * Initializes view specific actions.
     */
    private void initActions() {
        getActionMap().put(UndoAction.ID, undo.getUndoAction());
        getActionMap().put(RedoAction.ID, undo.getRedoAction());
    }
    @Override
    protected void setHasUnsavedChanges(boolean newValue) {
        super.setHasUnsavedChanges(newValue);
        undo.setHasSignificantEdits(newValue);
    }
    
    /**
     * Writes the view to the specified uri.
     */
    @Override
    public void write(URI f, URIChooser fc) throws IOException {
        Drawing drawing = view.getDrawing();
        OutputFormat outputFormat = drawing.getOutputFormats().get(0);
        /* before writing out the drawing, write out the metadata of each SpecialNode
        /for (Figure figure : drawing.getChildren()) {
            if (figure instanceof SpecialNode) {
                SpecialNode node = (SpecialNode) figure;
                String metadata = node.getMetadata();
                // write out metadata here...
            }
        }*/
        //Testo
        outputFormat.write(f, drawing);
    }
    
    /**
     * Reads the view from the specified uri.
     */
    @Override
    public void read(URI f, URIChooser fc) throws IOException {

            final Drawing drawing = createDrawing();

            boolean success = false;
            for (InputFormat sfi : drawing.getInputFormats()) {
                try {
                    sfi.read(f, drawing, true);
                    success = true;
                    break;
                } catch (Exception e) {
                    // try with the next input format
                }
            }
            if (!success) {
                ResourceBundleUtil labels = ApplicationLabels.getLabels();
                //throw new IOException(labels.getFormatted("file.open.unsupportedFileFormat.message : " + uri.getPath(), URIUtil.getName(f)));
            }
            ApplicationManager.getApplication().invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    view.getDrawing().removeUndoableEditListener(undo);
                    view.setDrawing(drawing);
                    view.getDrawing().addUndoableEditListener(undo);
                    undo.discardAllEdits();
                }
            });
    }
    
    
    /**
     * Sets a drawing editor for the view.
     */
    public void setEditor(DrawingEditor newValue) {
        if (editor != null) {
            editor.remove(view);
        }
        editor = newValue;
        if (editor != null) {
            editor.add(view);
            if (editor.getActionMap() != null && editor.getInputMap() != null) {editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), UndoAction.ID);
                editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_DOWN_MASK), UndoAction.ID);
                editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), RedoAction.ID);
                editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.META_DOWN_MASK), RedoAction.ID);
            }
        }
    }
    
    /**
     * Gets the drawing editor of the view.
     */
    public DrawingEditor getEditor() {
        return editor;
    }
    
    /**
     * Clears the view.
     */
    @Override
    public void clear() {
        final Drawing newDrawing = createDrawing();
        ApplicationManager.getApplication().invokeAndWait(new Runnable() {
            @Override
            public void run() {
                view.getDrawing().removeUndoableEditListener(undo);
                view.setDrawing(newDrawing);
                view.getDrawing().addUndoableEditListener(undo);
                undo.discardAllEdits();
            }
        });
    }
    
    @Override
    public boolean canSaveTo(URI file) {
        return new File(file).getName().endsWith(".xml");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new JScrollPane();
        //view = new DefaultDrawingView();
        view = new CodactorUmlDefaultDrawingView();
        view.addMouseListener(customMouseAdapterFactory.create(view));

        setLayout(new BorderLayout());

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(view);

        add(scrollPane, BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JScrollPane scrollPane;
    //private org.jhotdraw.draw.DefaultDrawingView view;
    private CodactorUmlDefaultDrawingView view;
    // End of variables declaration//GEN-END:variables


    public CodactorUmlDefaultDrawingView getView() {
        return view;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        this.undo.setFilePath(filePath);
    }
}


