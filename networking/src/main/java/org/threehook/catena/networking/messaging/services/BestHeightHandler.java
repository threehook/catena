package org.threehook.catena.networking.messaging.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threehook.catena.core.Blockchain;
import org.threehook.catena.core.BlockchainFactory;
import org.threehook.catena.networking.messaging.messages.BestHeight;


@Component
public class BestHeightHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BestHeightHandler.class);

    @Autowired
    private BlockchainFactory blockchainFactory;

    public BestHeight getBestHeight() {
        LOGGER.debug("Retrieving the best height from the blockchain.");
        Blockchain blockchain = blockchainFactory.getBlockchain();
        return new BestHeight(blockchain.getBestHeight());
    }
}
