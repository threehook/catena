package org.threehook.catena.networking.messaging.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threehook.catena.core.BlockchainException;
import org.threehook.catena.networking.messaging.services.model.BestHeightResponse;
import org.threehook.catena.networking.messaging.services.model.Version;
import org.threehook.catena.networking.messaging.services.model.VersionResponse;
import reactor.core.publisher.Mono;

@Component
public class VersionServiceImpl implements VersionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionServiceImpl.class);

    @Autowired
    private VersionHandler versionHandler;
    @Autowired
    private VersionServiceHelper versionServiceHelper;



}