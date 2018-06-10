package org.threehook.catena.networking.messaging.listening;

import io.scalecube.cluster.Cluster;
import io.scalecube.services.Microservices;
import io.scalecube.services.ServiceCall;
import io.scalecube.services.api.ServiceMessage;
import io.scalecube.transport.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.threehook.catena.core.BlockchainException;
import org.threehook.catena.networking.messaging.gossips.RequestBestHeight;
import org.threehook.catena.networking.messaging.routing.MemberRouter;
import org.threehook.catena.networking.messaging.services.BestHeightHandler;
import org.threehook.catena.networking.messaging.services.VersionHandler;
import org.threehook.catena.networking.messaging.services.VersionService;
import org.threehook.catena.networking.messaging.services.model.BestHeightResponse;
import org.threehook.catena.networking.messaging.services.model.Version;
import org.threehook.catena.networking.messaging.services.model.VersionResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class BestHeightRequestListener extends MessageListener<RequestBestHeight> implements Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BestHeightRequestListener.class);

    @Value("${node.version}")
    private int nodeVersion;

    @Autowired
    @Qualifier("consumer")
    private Microservices consumer;
    @Autowired
    private BestHeightHandler bestHeightHandler;

    public void subscribe() {
        LOGGER.debug("Start listening for bestHeight request messages...");
        Cluster cluster = consumer.cluster();
        cluster.listenGossips()
                .filter(message -> {
                    return message.data() instanceof RequestBestHeight;
                })
                .doOnError(error -> System.out.println(error.getMessage()))
                .subscribe(message -> {
                    BestHeightResponse bestHeightResponse = new BestHeightResponse(bestHeightHandler.getBestHeight());
                    // Call service one-way
                    ServiceMessage BEST_HEIGHT_CB = ServiceMessage.builder()
                            .qualifier("org.threehook.catena.networking.messaging.services.VersionService", "bestHeightCallback")
                            .header("fromHost", message.header("requestorHost")).header("fromPort", message.header("requestorPort"))
                            .data(bestHeightResponse).build();
                    consumer.call().router(MemberRouter.class).create().requestOne(BEST_HEIGHT_CB)
                            .doOnError(error -> System.out.println(error.getMessage())).block();
                });
    }


//                // 1. Count other members
//                Iterator<Member> memberIt = cluster.otherMembers().iterator();
//                while (memberIt.hasNext()) {
//                    // 2. For each member
//                    // 3. subscribe to this member and filter for bestHeightCallbacks and use timeout
//                    // 4. collect and save its best height together with address
//                    memberIt.next()
//                }
//
//                // 5. End loop when all received/timeout
//                // 6. Received enough
//                    // 7. start best height processing
//                // 8. Too many timeouts
//                    // 9. throw exception (for now)
//                System.out.println("Heard gossip: " + gossip.data());
//        });
//    }


//        LOGGER.debug("Listening for BestHeightResponse messages...");
//        ServiceMessage VERSION_REQUEST_REQ = ServiceMessage.builder()
//                .qualifier("org.threehook.catena.networking.messaging.services.VersionService", "getBestHeight")
//                .data(new Version(nodeVersion)).build();
//        Mono<ServiceMessage> serviceMessage = consumer.call().create().requestOne(VERSION_REQUEST_REQ, VersionResponse.class);
//
//       Mono.from(serviceMessage)
//        .doOnError(onError -> {
//            throw new BlockchainException(Exceptions.propagate(onError));
//        })
//        .subscribe(success -> {
//            System.out.println(((VersionResponse)success.data()).getBestHeight());
//        });
//        }

//        if (!consumer.cluster().otherMembers().isEmpty()) {
//            VersionService versionService = consumer.call().create().api(VersionService.class);
//            Mono.from(versionService.getBestHeight(new Version(nodeVersion)))
////                    .onErrorResume(error -> {
////                        throw new BlockchainException(error.getMessage());
////                    })
//                    .doOnError(onError -> {
//                        throw new BlockchainException(Exceptions.propagate(onError));
//                    })
//                    .subscribe(success -> {
//                        System.out.println(success.getBestHeight());
//                    });
//        }
//    }

}
