package org.threehook.catena.networking.messages;

import org.threehook.catena.networking.messaging.MessageType;

public class GetBlocks implements Message<MessageType.GET_BLOCKS> {

    private String addrFrom;

    public GetBlocks(String addrFrom) {
        this.addrFrom = addrFrom;
    }

    public String getAddrFrom() {
        return addrFrom;
    }

    @Override
    public Class<MessageType.GET_BLOCKS> getTypeClass() {
        return MessageType.GET_BLOCKS.class;
    }
}
