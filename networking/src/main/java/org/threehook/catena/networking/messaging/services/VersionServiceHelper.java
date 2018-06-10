package org.threehook.catena.networking.messaging.services;

import org.springframework.stereotype.Component;
import org.threehook.catena.networking.messaging.services.model.BestHeightResponse;

@Component
public class VersionServiceHelper {

    public void processBestHeightCallbacks(BestHeightResponse bestHeight) {
        System.out.println(bestHeight.getBestHeight());
    }

}
