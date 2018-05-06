package org.threehook.catena.networking.messages;

import org.threehook.catena.networking.messaging.MessageHandler;
import org.threehook.catena.networking.messaging.handlers.GetBlocksMessageHandler;

public class GetBlocks extends Message {

    private String addrFrom;

    public GetBlocks(String addrFrom) {
        this.addrFrom = addrFrom;
    }

    public String getAddrFrom() {
        return addrFrom;
    }

    @Override
    public MessageHandler getMessageHandler() {
        return new GetBlocksMessageHandler();
    }
}
