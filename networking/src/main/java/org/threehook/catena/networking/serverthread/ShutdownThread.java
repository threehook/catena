package org.threehook.catena.networking.serverthread;

public class ShutdownThread extends Thread {

    private ServerThread serverThread;

    public ShutdownThread(ServerThread shutdown) {
        super();
        this.serverThread = shutdown;
    }

    public void run() {
        System.out.println("Shutting down...");
        serverThread.stopThread();
        System.out.println("Shutdown complete.");
    }
}

