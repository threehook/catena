package org.threehook.catena.networking.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.threehook.catena.core.Blockchain;
import org.threehook.catena.core.BlockchainFactory;
import org.threehook.catena.networking.messages.Message;

public abstract class BaseMessageHandler<T extends Message> implements MessageHandler<T> {

//    @Value("${node.id}")
//    private String nodeId;
//    @Value("#{'${known.node.addresses}'.split(',')}")
//    private List<String> seedNodes;

    @Autowired
    private BlockchainFactory blockchainFactory;


    protected abstract void processMessage(T message);

    public void handleMessage(T message) {
        processMessage(message);
    }

    protected Blockchain getBlockchain() {
        return blockchainFactory.getBlockchain();
    }
}
