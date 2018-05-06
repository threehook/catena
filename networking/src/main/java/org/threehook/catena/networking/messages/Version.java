package org.threehook.catena.networking.messages;

import org.threehook.catena.networking.messaging.MessageHandler;
import org.threehook.catena.networking.messaging.handlers.VersionMessageHandler;

public class Version extends Message {

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
    public MessageHandler getMessageHandler() {
        return new VersionMessageHandler();
    }
}
