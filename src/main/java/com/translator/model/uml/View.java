package com.translator.model.uml;

import com.translator.model.uml.connection.Connection;
import com.translator.model.uml.node.Node;
import org.jhotdraw.draw.Figure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class View {
    private List<Connection> connections;
    private Map<Figure, Node> nodes;

    public View() {
        this.connections = new ArrayList<>();
        this.nodes = new HashMap<>();
    }
}
