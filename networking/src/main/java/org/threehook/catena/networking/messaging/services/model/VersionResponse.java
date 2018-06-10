package org.threehook.catena.networking.messaging.services.model;

public class VersionResponse {

    private int bestHeight;

    public VersionResponse() {
    }

    public VersionResponse(int bestHeight) {
        this.bestHeight = bestHeight;
    }

    public int getBestHeight() {
        return bestHeight;
    }

    public void setBestHeight(int bestHeight) {
        this.bestHeight = bestHeight;
    }

    @Override
    public String toString() {
        return "VersionResponse{" +
                "bestHeight=" + bestHeight +
                '}';
    }
}
