package org.threehook.catena.networking.messaging.services.model;

import org.threehook.catena.networking.messaging.messages.BestHeight;

public class BestHeightResponse {

    private BestHeight bestHeight;

    public BestHeightResponse() {
    }

    public BestHeightResponse(BestHeight bestHeight) {
        this.bestHeight = bestHeight;
    }

    public BestHeight getBestHeight() {
        return bestHeight;
    }

    public void setBestHeight(BestHeight bestHeight) {
        this.bestHeight = bestHeight;
    }
}
