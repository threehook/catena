package org.threehook.catena.networking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.threehook.catena.networking.cluster.ClusterNetwork;
import org.threehook.catena.networking.messages.Message;
import org.threehook.catena.networking.messaging.producers.VersionMessageProducer;
import org.threehook.catena.networking.serverthread.ServerThread;
import org.threehook.catena.networking.serverthread.ShutdownThread;

import java.util.List;

@Component
public class Server {

    @Value("${node.server.address}")
    private String nodeServerAddress;
    @Value("${node.server.port}")
    private int nodeServerPort;

    @Autowired
    private VersionMessageProducer versionMessageProducer;
    @Autowired
    private ClusterNetwork clusterNetwork;
    @Autowired
    private List<String> seedAddresses;

    private String nodeAddress;
    private String miningAddress;

    public void startServer(String minerAddress) {
        this.miningAddress = minerAddress; //???

        // TODO: How to select a seedAddress from many?
//        versionMessageProducer.produceMessage(seedAddresses.get(0));

        // Listen for messages
        clusterNetwork.getCluster().listen().filter(message-> {
            return message.data() instanceof Message;
        }).subscribe(message-> {
            Message catenaMessage = message.data();
            catenaMessage.getMessageHandler().handleMessage(catenaMessage);
        });

        ServerThread serverThread = new ServerThread();
        Runtime.getRuntime().addShutdownHook(new ShutdownThread(serverThread));
        System.out.println("Catena server started on " + nodeServerAddress + ":" + nodeServerPort + ". Press Ctrl-C to stop.\n");
    }

}
