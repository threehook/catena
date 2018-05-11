package org.threehook.catena.networking.messages;

import org.threehook.catena.networking.messaging.MessageType;

public class GetBlocks implements Message<MessageType.GET_BLOCKS> {

    private String addressFrom;

    public GetBlocks(String addrFrom) {
        this.addressFrom = addrFrom;
    }

    public String getAddressFrom() {
        return addressFrom;
    }

    @Override
    public Class<MessageType.GET_BLOCKS> getTypeClass() {
        return MessageType.GET_BLOCKS.class;
    }
}
