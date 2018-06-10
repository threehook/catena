package org.threehook.catena.networking.messaging.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threehook.catena.core.Blockchain;
import org.threehook.catena.core.BlockchainFactory;
import org.threehook.catena.networking.messaging.messages.BestHeight;
import org.threehook.catena.networking.messaging.services.model.Version;


@Component
public class VersionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionHandler.class);

    @Autowired
    private BlockchainFactory blockchainFactory;

    public boolean checkVersion() {
        return true;
    }


}
