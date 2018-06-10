package org.threehook.catena.networking.messaging.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.threehook.catena.networking.messaging.messages.BestHeight;
import org.threehook.catena.networking.messaging.services.model.BestHeightResponse;
import org.threehook.catena.networking.messaging.services.model.Version;
import reactor.core.publisher.Mono;

public class BestHeightServiceImpl implements BestHeightService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BestHeightServiceImpl.class);

    @Autowired
    private BestHeightHandler bestHeightHandler;
    @Autowired
    private VersionServiceHelper versionServiceHelper;

    @Override
    public Mono<BestHeightResponse> getBestHeight(Version version) {
        LOGGER.debug("Entering VersionServiceImpl.getBestHeight");
        return Mono.just( new BestHeightResponse(bestHeightHandler.getBestHeight()));
    }

    @Override
    public Mono<Void> bestHeightCallback(BestHeightResponse bestHeight) {
        LOGGER.debug("Entering VersionServiceImpl.bestHeightCallback");
        versionServiceHelper.processBestHeightCallbacks(bestHeight);
        return Mono.empty();
    }

    @Override
    public void processNewBestHeight(BestHeight bestHeight) {

    }
}
