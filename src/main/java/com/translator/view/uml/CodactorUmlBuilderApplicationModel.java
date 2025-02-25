/* @(#)DrawApplicationModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package com.translator.view.uml;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.intellij.openapi.project.Project;
import com.translator.model.uml.draw.figure.*;
import com.translator.service.uml.node.tool.AdvancedDelegationSelectionTool;
import com.translator.view.uml.factory.tool.NodeConnectionToolFactory;
import com.translator.view.uml.factory.tool.PromptNodeCreationToolFactory;
import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.ApplicationModel;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.View;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.AbstractAttributedFigure;
import org.jhotdraw.draw.BezierFigure;
import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.ImageFigure;
import org.jhotdraw.draw.LineFigure;
import org.jhotdraw.draw.TextAreaFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.draw.tool.*;
import org.jhotdraw.draw.tool.BezierTool;
import org.jhotdraw.draw.tool.ConnectionTool;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.ImageTool;
import org.jhotdraw.draw.tool.TextAreaCreationTool;
import org.jhotdraw.draw.tool.TextCreationTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.jhotdraw.draw.AttributeKeys.END_DECORATION;

/**
 * Provides factory methods for creating views, menu bars and toolbars.
 * <p>
 * See {@link ApplicationModel} on how this class interacts with an application.
 * 
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class CodactorUmlBuilderApplicationModel extends DefaultApplicationModel {
    private Project project;
    private List<JToolBar> list;
    private static final long serialVersionUID = 1L;

    /**
     * This editor is shared by all views.
     */
    private DefaultDrawingEditor sharedEditor;
    private final PromptNodeCreationToolFactory promptNodeCreationToolFactory;
    private final NodeConnectionToolFactory nodeConnectionToolFactory;
    private CodactorUmlBuilderView codactorUmlBuilderView;
    private final Gson gson;

    /** Creates a new instance. */
    @Inject
    public CodactorUmlBuilderApplicationModel(Project project,
                                              PromptNodeCreationToolFactory promptNodeCreationToolFactory,
                                              NodeConnectionToolFactory nodeConnectionToolFactory,
                                              Gson gson) {
        this.list = new LinkedList<>();
        this.project = project;
        this.promptNodeCreationToolFactory = promptNodeCreationToolFactory;
        this.nodeConnectionToolFactory = nodeConnectionToolFactory;
        this.gson = gson;
    }

    public DefaultDrawingEditor getSharedEditor() {
        if (sharedEditor == null) {
            sharedEditor = new DefaultDrawingEditor();
        }
        return sharedEditor;
    }

    public List<JToolBar> getToolBars() {
        return list;
    }

    @Override
    public void initView(Application a,View p) {
        if (a.isSharingToolsAmongViews()) {
            ((CodactorUmlBuilderView)p).setEditor(getSharedEditor());
        }
    }

    /**
     * Creates toolbars for the application.
     * This class always returns an empty list. Subclasses may return other
     * values.
     */
    @Override
    public List<JToolBar> createToolBars(Application a, @Nullable View pr) {
        ResourceBundleUtil labels = DrawLabels.getLabels();
        codactorUmlBuilderView = (CodactorUmlBuilderView) pr;

        DrawingEditor editor;
        if (codactorUmlBuilderView == null) {
            editor = getSharedEditor();
        } else {
            editor = codactorUmlBuilderView.getEditor();
        }

        JToolBar tb;
        tb = new JToolBar();
        tb.setBackground(Color.darkGray);
        addCreationButtonsTo(tb, editor);
        tb.setName(labels.getString("window.drawToolBar.title"));
        list.add(tb);
        tb = new JToolBar();
        tb.setBackground(Color.darkGray);
        ButtonFactory.addAttributesButtonsTo(tb, editor);
        tb.setName(labels.getString("window.attributesToolBar.title"));
        list.add(tb);
        tb = new JToolBar();
        tb.setBackground(Color.darkGray);
        ButtonFactory.addAlignmentButtonsTo(tb, editor);
        tb.setName(labels.getString("window.alignmentToolBar.title"));
        list.add(tb);
        return list;
    }

    private void addCreationButtonsTo(JToolBar tb, DrawingEditor editor) {
        addDefaultCreationButtonsTo(tb, editor,
                ButtonFactory.createDrawingActions(editor),
                ButtonFactory.createSelectionActions(editor));
    }

    public void addDefaultCreationButtonsTo(JToolBar tb, final DrawingEditor editor,
            Collection<Action> drawingActions, Collection<Action> selectionActions) {
        ResourceBundleUtil labels = DrawLabels.getLabels();

        //ButtonFactory.addToolTo(tb, editor, new DelegationSelectionTool(drawingActions, selectionActions), "selectionTool", labels);
        Tool selectionTool = new AdvancedDelegationSelectionTool(drawingActions, selectionActions);
        ButtonFactory.addSelectionToolTo(tb, editor, selectionTool);
        tb.addSeparator();

        AbstractAttributedFigure af;
        CreationTool ct;
        ConnectionTool cnt;
        ConnectionFigure lc;
        ButtonFactory.addToolTo(tb, editor, promptNodeCreationToolFactory.create(new LabeledRectangleFigure("Prompt")), "edit.createRectangle", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new LabeledRoundRectangleFigure("Calculator")), "edit.createRoundRectangle", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new LabeledEllipseFigure("Custom Code")), "edit.createEllipse", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new LabeledDiamondFigure("Verifier")), "edit.createDiamond", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new LabeledTriangleFigure("Transformer")), "edit.createTriangle", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new LabeledEllipseFigure("File Modifier")), "edit.createEllipse", labels);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new LineFigure()), "edit.createLine", labels);
        ButtonFactory.addToolTo(tb, editor, ct = new CreationTool(new LineFigure()), "edit.createArrow", labels);
        af = (AbstractAttributedFigure) ct.getPrototype();
        af.set(END_DECORATION, new ArrowTip(0.35, 12, 11.3));
        ButtonFactory.addToolTo(tb, editor, nodeConnectionToolFactory.create(new MetadataLabeledLineConnectionFigure()), "edit.createLineConnection", labels);
        ButtonFactory.addToolTo(tb, editor, cnt = nodeConnectionToolFactory.create(new MetadataLabeledLineConnectionFigure()), "edit.createElbowConnection", labels);
        lc = cnt.getPrototype();
        lc.setLiner(new ElbowLiner());
        ButtonFactory.addToolTo(tb, editor, cnt = nodeConnectionToolFactory.create(new MetadataLabeledLineConnectionFigure()), "edit.createCurvedConnection", labels);
        lc = cnt.getPrototype();
        lc.setLiner(new CurvedLiner());
        ButtonFactory.addToolTo(tb, editor, new BezierTool(new BezierFigure()), "edit.createScribble", labels);
        ButtonFactory.addToolTo(tb, editor, new BezierTool(new BezierFigure(true)), "edit.createPolygon", labels);
        ButtonFactory.addToolTo(tb, editor, new TextCreationTool(new TextFigure()), "edit.createText", labels);
        ButtonFactory.addToolTo(tb, editor, new TextAreaCreationTool(new TextAreaFigure()), "edit.createTextArea", labels);
        ButtonFactory.addToolTo(tb, editor, new ImageTool(new ImageFigure()), "edit.createImage", labels);
    }

    @Override
    public URIChooser createOpenChooser(Application a, @Nullable View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("Drawing .xml","xml"));
        return c;
    }

    @Override
    public URIChooser createSaveChooser(Application a, @Nullable View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("Drawing .xml","xml"));
        return c;
    }
}
