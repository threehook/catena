package org.threehook.catena.networking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.threehook.catena.networking.cluster.ClusterNetwork;
import org.threehook.catena.networking.config.SeedAddress;
import org.threehook.catena.networking.messages.Message;
import org.threehook.catena.networking.messaging.MessageHandlerRegister;
import org.threehook.catena.networking.messaging.MessageHandler;
import org.threehook.catena.networking.messaging.producers.VersionMessageProducer;
import org.threehook.catena.networking.serverthread.ServerThread;
import org.threehook.catena.networking.serverthread.ShutdownThread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class Server {

    @Value("${node.server.address}")
    private String nodeServerAddress;
    @Value("${node.server.port}")
    private int nodeServerPort;

    @Autowired
    private MessageHandlerRegister device;
    @Autowired
    private VersionMessageProducer versionMessageProducer;
    @Autowired
    private ClusterNetwork clusterNetwork;
    @Autowired
    private List<SeedAddress> seedAddresses;

    private String nodeAddress;
    private String miningAddress;

    public void startServer(String minerAddress) {
        this.miningAddress = minerAddress; //???

        // TODO: How to select a seedAddress from many?
        versionMessageProducer.produceAndSendMessage(seedAddresses.get(0).getHost());

        // Listen for messages
        final ExecutorService executorService = Executors.newCachedThreadPool();
        clusterNetwork.getCluster().listen().filter(message-> {
            return message.data() instanceof Message;
        }).subscribe(message-> {
            Message catenaMessage = message.data();
            executorService.execute(new Runnable() {
                public void run() {
                    MessageHandler messageHandler = device.getHandler(catenaMessage.getTypeClass());
                    messageHandler.handle(catenaMessage);
                }
            });
        });

        ServerThread serverThread = new ServerThread();
        Runtime.getRuntime().addShutdownHook(new ShutdownThread(executorService, serverThread));
        System.out.println("Catena server started on " + nodeServerAddress + ":" + nodeServerPort + ".\nPress Ctrl-C to stop. Running threads will be cleanly finished.\n");
    }


}
