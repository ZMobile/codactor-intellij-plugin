package com.translator.view.uml.factory.adapter;

import com.translator.model.uml.draw.figure.listener.CustomMouseAdapter;
import org.jhotdraw.draw.DefaultDrawingView;

public interface CustomMouseAdapterFactory {
    CustomMouseAdapter create(DefaultDrawingView defaultDrawingView);
}
