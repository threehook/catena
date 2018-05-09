package org.threehook.catena.networking.messages;

import org.threehook.catena.networking.messaging.MessageType;

public class Version implements Message<MessageType.VERSION> {

    private int version;
    private int bestHeight;
    private String addrFrom;

    public Version(int version, int bestHeight, String addrFrom) {
        this.version = version;
        this.bestHeight = bestHeight;
        this.addrFrom = addrFrom;
    }

    public int getVersion() {
        return version;
    }

    public int getBestHeight() {
        return bestHeight;
    }

    public String getAddrFrom() {
        return addrFrom;
    }

    @Override
    public Class<MessageType.VERSION> getTypeClass() {
        return MessageType.VERSION.class;
    }

}
