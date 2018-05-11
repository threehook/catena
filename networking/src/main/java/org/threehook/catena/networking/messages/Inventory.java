package org.threehook.catena.networking.messages;

import org.threehook.catena.networking.messaging.MessageType;

import java.util.List;

public class Inventory implements Message<MessageType.INVENTORY> {

    private String addressFrom;
    private List<byte[]> items;
    private Class type;

    public Inventory(List<byte[]> blocks, String addrFrom) {
        this.items = blocks;
        this.addressFrom = addrFrom;
    }

    public String getAddressFrom() {
        return addressFrom;
    }

    public List<byte[]> getItems() {
        return items;
    }

    public Class getType() {
        return type;
    }

    @Override
    public Class<MessageType.INVENTORY> getTypeClass() {
        return MessageType.INVENTORY.class;
    }

}


