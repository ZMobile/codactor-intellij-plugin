package com.translator.view.uml.factory.adapter;

import com.translator.model.uml.draw.figure.listener.CustomMouseAdapter;
import com.translator.view.uml.CodactorUmlDefaultDrawingView;
import org.jhotdraw.draw.DefaultDrawingView;

public interface CustomMouseAdapterFactory {
    CustomMouseAdapter create(CodactorUmlDefaultDrawingView defaultDrawingView);
}
