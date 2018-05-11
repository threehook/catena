package org.threehook.catena.networking.messaging.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threehook.catena.core.Blockchain;
import org.threehook.catena.core.BlockchainFactory;
import org.threehook.catena.networking.messages.Version;
import org.threehook.catena.networking.messaging.BaseMessageHandler;
import org.threehook.catena.networking.messaging.MessageType;
import org.threehook.catena.networking.messaging.dispatchers.GetBlocksMessageProducer;
import org.threehook.catena.networking.messaging.dispatchers.VersionMessageProducer;

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
            getBlocksMessageProducer.produceAndSendMessage(message.getAddressFrom());
        } else if (myBestHeight > foreignerBestHeight) {
            versionMessageProducer.produceAndSendMessage(message.getAddressFrom());
        }
        if (!isSeedAddress(message.getAddressFrom())) {
            seedAddresses.add(message.getAddressFrom());
        }
    }

    private boolean isSeedAddress(String address) {
        return seedAddresses.contains(address);
    }

}
