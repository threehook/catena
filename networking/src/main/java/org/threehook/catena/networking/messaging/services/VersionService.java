package org.threehook.catena.networking.messaging.services;

import io.scalecube.services.annotations.Service;
import io.scalecube.services.annotations.ServiceMethod;
import io.scalecube.services.api.ServiceMessage;
import org.threehook.catena.networking.messaging.services.model.BestHeightResponse;
import org.threehook.catena.networking.messaging.services.model.Version;
import org.threehook.catena.networking.messaging.services.model.VersionResponse;
import reactor.core.publisher.Mono;

@Service
public interface VersionService {

}