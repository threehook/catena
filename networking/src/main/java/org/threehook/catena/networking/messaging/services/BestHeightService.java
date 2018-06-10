package org.threehook.catena.networking.messaging.services;

import io.scalecube.services.annotations.Service;
import io.scalecube.services.annotations.ServiceMethod;
import org.threehook.catena.networking.messaging.messages.BestHeight;
import org.threehook.catena.networking.messaging.services.model.BestHeightResponse;
import org.threehook.catena.networking.messaging.services.model.Version;
import org.threehook.catena.networking.messaging.services.model.VersionResponse;
import reactor.core.publisher.Mono;

@Service
public interface BestHeightService {

    @ServiceMethod
    Mono<BestHeightResponse> getBestHeight(Version version);

    @ServiceMethod
    Mono<Void> bestHeightCallback(BestHeightResponse bestHeight);

    @ServiceMethod
    void processNewBestHeight(BestHeight bestHeight);
}
