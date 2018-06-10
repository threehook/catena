package org.threehook.catena.networking.messaging.services.model;

public class Version {

    private int version;
    private int bestHeight;

    public Version() {
    }

    public Version(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getBestHeight() {
        return bestHeight;
    }

    public void setBestHeight(int bestHeight) {
        this.bestHeight = bestHeight;
    }
}
