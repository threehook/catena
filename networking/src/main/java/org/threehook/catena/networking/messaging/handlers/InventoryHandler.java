package org.threehook.catena.networking.messaging.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.threehook.catena.networking.messages.GetBlocksData;
import org.threehook.catena.networking.messages.Inventory;
import org.threehook.catena.networking.messaging.BaseMessageHandler;
import org.threehook.catena.networking.messaging.MessageType;
import org.threehook.catena.networking.messaging.dispatchers.GetBlocksDataMessageProducer;

import java.util.List;

public class InventoryHandler extends BaseMessageHandler<MessageType.INVENTORY, Inventory> {

    private final static String BLOCK = "block";
    private final static String TRANSACTION = "tx";

    @Value("${node.server.address}")
    private String nodeServerAddress;
    @Value("${node.server.port}")
    private int nodeServerPort;
    @Autowired
    private GetBlocksDataMessageProducer getBlocksDataMessageProducer;

    @Override
    protected void processMessage(Inventory message) {
        List<byte[]> blocksInTransit = null;
        System.out.printf("Received inventory with %d %s\n", message.getItems().size(), message.getType());
        switch (message.getType().getName()) {
            case BLOCK:

                getBlocksDataMessageProducer.produceMessage(message.getAddressFrom(), message);
                break;
            case TRANSACTION:
//                getTransactionDataProducer.produceMessage(message.getAddressFrom(), message);
        }

    }
}



