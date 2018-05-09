package org.threehook.catena.networking.messaging.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threehook.catena.core.Blockchain;
import org.threehook.catena.core.BlockchainFactory;
import org.threehook.catena.networking.messages.Version;
import org.threehook.catena.networking.messaging.BaseMessageHandler;
import org.threehook.catena.networking.messaging.MessageHandler;
import org.threehook.catena.networking.messaging.MessageType;
import org.threehook.catena.networking.messaging.producers.GetBlocksMessageProducer;
import org.threehook.catena.networking.messaging.producers.VersionMessageProducer;

import java.util.List;

@Component
public class VersionMessageHandler extends BaseMessageHandler<MessageType.VERSION, Version> {

    @Autowired
    // Initial seed address list is constantly updated because of received messages
    protected List<String> seedAddresses;
    @Autowired
    private GetBlocksMessageProducer getBlocksMessageProducer;
    @Autowired
    private VersionMessageProducer versionMessageProducer;
    @Autowired
    private BlockchainFactory blockchainFactory;

    @Override
    protected void processMessage(Version message) {
        Blockchain blockchain = blockchainFactory.getBlockchain();
        int myBestHeight = blockchain.getBestHeight();
        int foreignerBestHeight = message.getBestHeight();
        if (myBestHeight < foreignerBestHeight) {
            getBlocksMessageProducer.createAndSendMessage(message.getAddrFrom());
        } else if (myBestHeight > foreignerBestHeight) {
            versionMessageProducer.createAndSendMessage(message.getAddrFrom());
        }
        if (!isSeedAddress(message.getAddrFrom())) {
            seedAddresses.add(message.getAddrFrom());
        }
    }

    private boolean isSeedAddress(String address) {
        return seedAddresses.contains(address);
    }

}
