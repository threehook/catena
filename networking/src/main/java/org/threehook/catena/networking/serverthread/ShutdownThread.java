package org.threehook.catena.networking.serverthread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ShutdownThread extends Thread {

    private ExecutorService executorService;
    private ServerThread serverThread;

    public ShutdownThread(ExecutorService executorService, ServerThread shutdown) {
        super();
        this.executorService = executorService;
        this.serverThread = shutdown;
    }

    public void run() {
        // Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted
        executorService.shutdown();
        try {
            while(!executorService.awaitTermination(10, TimeUnit.MINUTES)) {
                System.out.printf("%s\r", "Shutting down...");
            }
        } catch (InterruptedException ie) {
            // No action
        }

        // And shutdown server thread
        serverThread.stopThread();
        System.out.println("Shutdown complete.");
    }

}

