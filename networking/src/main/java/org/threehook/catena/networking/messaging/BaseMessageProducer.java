package org.threehook.catena.networking.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.threehook.catena.core.Blockchain;
import org.threehook.catena.core.BlockchainFactory;
import org.threehook.catena.networking.messages.Message;

public abstract class BaseMessageProducer<T extends Message> implements MessageProducer<T> {

    @Value("${node.id}")
    private String nodeId;

    @Autowired
    private BlockchainFactory blockchainFactory;


    protected abstract T createMessage(String address);

    protected abstract void createAndSendMessage(String address);

    protected abstract void sendMessage(String address, Message message);


    public T produceMessage(String targetAddress) {
        return createMessage(targetAddress);
    }

    protected Blockchain getBlockchain() {
        return blockchainFactory.getBlockchain();
    }
}
