package org.threehook.catena.networking.messaging.dispatchers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.threehook.catena.core.Blockchain;
import org.threehook.catena.networking.messages.Version;
import org.threehook.catena.networking.messaging.InitializingMessageDispatcher;

@Component
public class VersionMessageProducer extends InitializingMessageDispatcher<Version> {

    @Value("${node.server.address}")
    private String nodeServerAddress;
    @Value("${node.server.port}")
    private int nodeServerPort;

    private final static int NODE_VERSION = 1; //TODO: Do something with this

    @Override
    protected Version createMessage(String targetAddress) {
        Blockchain blockchain = getBlockchain();
        int bestHeight = blockchain.getBestHeight();
        return new Version(NODE_VERSION, bestHeight, nodeServerAddress + ":" + nodeServerPort);
    }

}
