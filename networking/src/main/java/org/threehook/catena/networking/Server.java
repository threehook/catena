package org.threehook.catena.networking;

import io.scalecube.services.Microservices;
import io.scalecube.transport.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.threehook.catena.networking.messaging.gossips.RequestBestHeight;
import org.threehook.catena.networking.messaging.listening.BestHeightListener;
import org.threehook.catena.networking.messaging.listening.BestHeightRequestListener;
import org.threehook.catena.networking.serverthread.ServerThread;
import org.threehook.catena.networking.serverthread.ShutdownThread;

@Component
public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    @Value("${node.version}")
    private int nodeVersion;
    @Value("${node.server.address}")
    private String nodeServerAddress;
    @Value("${node.server.port}")
    private int nodeServerPort;
    @Value("${node.service.port}")
    private int nodeServicePort;


    @Autowired
    @Qualifier("consumer")
    private Microservices consumer;
    @Autowired
    private BestHeightRequestListener bestHeightRequestListener;

    @Autowired
    private BestHeightListener bestHeightListener;

    private String nodeAddress;
    private String miningAddress;



    public void startServer(String minerAddress) {
        this.miningAddress = minerAddress; //???
        // Listen to BestHeightRequest messages
        bestHeightRequestListener.subscribe();
        // Listen to BestHeightResponse messages
        bestHeightListener.subscribe();

        consumer.cluster().spreadGossip(Message.builder()
                .header("requestorHost", nodeServerAddress)
                .header("requestorPort", String.valueOf(nodeServicePort))
                .data(new RequestBestHeight()).build());

        ServerThread serverThread = new ServerThread();
        Runtime.getRuntime().addShutdownHook(new ShutdownThread(serverThread));
        LOGGER.info("Catena server (Version " + nodeVersion + ") started on " + nodeServerAddress + ":" + nodeServerPort + ".\nPress Ctrl-C to stop. Running threads will be cleanly finished.\n");
    }

}

