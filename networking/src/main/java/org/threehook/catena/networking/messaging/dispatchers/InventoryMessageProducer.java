package org.threehook.catena.networking.messaging.dispatchers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threehook.catena.core.Blockchain;
import org.threehook.catena.core.BlockchainFactory;
import org.threehook.catena.networking.messages.Inventory;
import org.threehook.catena.networking.messaging.InitializingMessageDispatcher;

import java.util.List;

@Component
public class InventoryMessageProducer extends InitializingMessageDispatcher<Inventory> {

    @Autowired
    private BlockchainFactory blockchainFactory;

    @Override
    protected Inventory createMessage(String targetAddress) {
        Blockchain blockchain = blockchainFactory.getBlockchain();
        List<byte[]> blocks = blockchain.getBlockHashes();
        return new Inventory(blocks, targetAddress);
    }

    @Override
    public void produceAndSendMessage(String targetAddress) {
        Inventory inventory = createMessage(targetAddress);
        dispatchMessage(targetAddress, inventory);
    }


}
