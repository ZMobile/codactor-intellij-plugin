package com.translator.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class CodactorApiServer {
    private Server server;

    public void start(int port) throws Exception {
        server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        server.setHandler(context);
        context.addServlet(new ServletHolder(new CodactorApiServlet()), "/codactor/*");

        server.start();
    }

    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }
}
