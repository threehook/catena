package org.threehook.catena.networking.serverthread;

public class ServerThread {

    private final static String THREAD_NAME = "Catena server";
    private Thread thread;


    public ServerThread() {
        thread = new Thread(THREAD_NAME) {
            public void run() {
                try {
                    Thread.currentThread().join();
                } catch (InterruptedException ie) {
                    System.exit(0);
                }
            }
        };
        thread.start();
    }

    public void stopThread() {
        thread.interrupt();
    }


}
