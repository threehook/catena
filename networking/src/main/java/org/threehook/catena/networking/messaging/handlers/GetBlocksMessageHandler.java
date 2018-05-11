package org.threehook.catena.networking.messaging.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threehook.catena.networking.messages.GetBlocks;
import org.threehook.catena.networking.messaging.BaseMessageHandler;
import org.threehook.catena.networking.messaging.MessageType;
import org.threehook.catena.networking.messaging.dispatchers.InventoryMessageProducer;

@Component
public class GetBlocksMessageHandler extends BaseMessageHandler<MessageType.GET_BLOCKS, GetBlocks> {

    @Autowired
    private InventoryMessageProducer inventoryMessageProducer;

    @Override
    protected void processMessage(GetBlocks message) {
        inventoryMessageProducer.produceAndSendMessage(message.getAddressFrom());
    }
}
