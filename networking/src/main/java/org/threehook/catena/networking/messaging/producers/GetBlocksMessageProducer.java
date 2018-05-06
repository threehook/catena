package org.threehook.catena.networking.messaging.producers;

import org.springframework.stereotype.Component;
import org.threehook.catena.networking.messages.GetBlocks;
import org.threehook.catena.networking.messages.Message;
import org.threehook.catena.networking.messaging.BaseMessageProducer;

@Component
public class GetBlocksMessageProducer extends BaseMessageProducer<GetBlocks> {

    @Override
    public GetBlocks createMessage(String nodeAddress) {
        return new GetBlocks(nodeAddress);
    }

    @Override
    public void createAndSendMessage(String nodeAddress) {
        GetBlocks getBlocks = createMessage(nodeAddress);
        sendMessage(nodeAddress, getBlocks);
    }

    @Override
    public void sendMessage(String address, Message message) {
        //TODO
    }

}
