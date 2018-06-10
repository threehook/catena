package org.threehook.catena.networking.messaging.listening;

import io.scalecube.cluster.Cluster;
import io.scalecube.services.Microservices;
import io.scalecube.transport.Address;
import io.scalecube.transport.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.threehook.catena.networking.messaging.gossips.RequestBestHeight;
import org.threehook.catena.networking.messaging.services.model.BestHeightResponse;
import org.threehook.catena.networking.messaging.services.model.VersionResponse;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class BestHeightListener extends MessageListener<RequestBestHeight> implements Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BestHeightListener.class);

    @Value("${node.version}")
    private int nodeVersion;
    @Value("${fan-in.waiting.time}")
    private int waitTime;
    @Autowired
    @Qualifier("consumer")
    private Microservices consumer;
    @Autowired
    private Address[] seedAddresses;

    public void subscribe() {
        LOGGER.debug("Start listening for VersionService.bestHeightCallback messages...");
        Cluster cluster = consumer.cluster();
        cluster.listenGossips().subscribe(gossip -> {
            cluster.listen().filter(message-> {
                return message.data() instanceof BestHeightResponse;
            }).buffer(waitTime, TimeUnit.SECONDS, seedAddresses.length).asObservable()
                    .map(x -> messageList2BestHeightResponseList(x))
                    .subscribe(success -> {
//                System.out.println("BestHeightListener/subscribe: " + ((BestHeightResponse)success).getBestHeight());
//                BestHeightResponse bestHeightResponse = Collections.max(((Collection<BestHeightResponse>)success), BEST_HEIGHT_COMPARATOR);
            });
        });
    }

    private List<BestHeightResponse> messageList2BestHeightResponseList(List<Message> messages) {
        return messages.stream().map(message -> (BestHeightResponse) message.data()).collect(Collectors.toList());
    }

//    private final static Comparator<Message> BEST_HEIGHT_COMPARATOR =
//            new Comparator<Message>() {
//                public int compare(Message r1, Message r2) {
//                    BestHeightResponse bh1 = (BestHeightResponse) r1.data();
//                    BestHeightResponse bh2 = (BestHeightResponse) r2.data();
//                    return Integer.compare(bh1.getBestHeight(), bh2.getBestHeight());
//                }
//            };

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
