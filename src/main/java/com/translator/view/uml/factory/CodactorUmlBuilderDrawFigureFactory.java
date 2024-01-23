package com.translator.view.uml.factory;

import com.translator.model.uml.draw.figure.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.connector.*;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.xml.DefaultDOMFactory;

public class CodactorUmlBuilderDrawFigureFactory extends CodactorUmlBuilderDefaultDOMFactory {
    private static final Object[][] classTagArray = new Object[][]{
            {DefaultDrawing.class, "drawing"},
            {QuadTreeDrawing.class, "drawing"},
            {DiamondFigure.class, "diamond"},
            {TriangleFigure.class, "triangle"},
            {BezierFigure.class, "bezier"},
            {RectangleFigure.class, "r"},
            {RoundRectangleFigure.class, "rr"},
            {LineFigure.class, "l"},
            {BezierFigure.class, "b"},
            {LineConnectionFigure.class, "lnk"},
            {EllipseFigure.class, "e"},
            {TextFigure.class, "t"},
            {TextAreaFigure.class, "ta"},
            {ImageFigure.class, "image"},
            {GroupFigure.class, "g"},

            {LabeledRectangleFigure.class, "lr"},
            {LabeledDiamondFigure.class, "ld"},
            {LabeledTriangleFigure.class, "lt"},
            {LabeledEllipseFigure.class, "le"},
            {LabeledRoundRectangleFigure.class, "lrr"},
            {MetadataLabeledLineConnectionFigure.class, "ll"},

            {ArrowTip.class, "arrowTip"},
            {ChopRectangleConnector.class, "rConnector"},
            {ChopEllipseConnector.class, "ellipseConnector"},
            {ChopRoundRectangleConnector.class, "rrConnector"},
            {ChopTriangleConnector.class, "triangleConnector"},
            {ChopDiamondConnector.class, "diamondConnector"},
            {ChopBezierConnector.class, "bezierConnector"},
            {ElbowLiner.class, "elbowLiner"},
            {CurvedLiner.class, "curvedLiner"}};
    private static final Object[][] enumTagArray = new Object[][]{{AttributeKeys.StrokePlacement.class, "strokePlacement"}, {AttributeKeys.StrokeType.class, "strokeType"}, {AttributeKeys.Underfill.class, "underfill"}, {AttributeKeys.Orientation.class, "orientation"}};

    public  CodactorUmlBuilderDrawFigureFactory() {
        Object[][] var1 = classTagArray;
        int var2 = var1.length;

        int var3;
        Object[] o;
        for(var3 = 0; var3 < var2; ++var3) {
            o = var1[var3];
            this.addStorableClass((String)o[1], (Class)o[0]);
        }

        var1 = enumTagArray;
        var2 = var1.length;

        for(var3 = 0; var3 < var2; ++var3) {
            o = var1[var3];
            this.addEnumClass((String)o[1], (Class)o[0]);
        }

    }
}
