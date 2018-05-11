package org.threehook.catena.networking.messages;

import org.threehook.catena.networking.messaging.MessageType;

public class GetBlocksData implements Message<MessageType.GET_BLOCKS_DATA> {

    private String addressFrom;
    private String kind;
    private byte[] id;

    public GetBlocksData(String addressFrom, String kind, byte[] id) {
        this.addressFrom = addressFrom;
        this.kind = kind;
        this.id = id;
    }

    public String getAddressFrom() {
        return addressFrom;
    }

    public String getKind() {
        return kind;
    }

    public byte[] getId() {
        return id;
    }

    @Override
    public Class<MessageType.GET_BLOCKS_DATA> getTypeClass() {
        return MessageType.GET_BLOCKS_DATA.class;
    }
}
