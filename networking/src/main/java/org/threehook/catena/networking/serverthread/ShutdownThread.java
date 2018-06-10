package org.threehook.catena.networking.serverthread;

import io.scalecube.cluster.Member;
import io.scalecube.services.Microservices;
import io.scalecube.services.ServiceEndpoint;
import io.scalecube.services.registry.api.ServiceRegistry;
import io.scalecube.transport.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ShutdownThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownThread.class);

    private ServerThread serverThread;

    public ShutdownThread(ServerThread shutdown) {
        super();
        this.serverThread = shutdown;
    }

    public void run() {
        // And shutdown server thread
        serverThread.stopThread();
        LOGGER.info("Shutdown complete.");
    }

}

