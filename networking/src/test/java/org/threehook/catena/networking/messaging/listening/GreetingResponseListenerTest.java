package org.threehook.catena.networking.messaging.listening;

import io.scalecube.cluster.Cluster;
import io.scalecube.transport.Address;
import io.scalecube.transport.Message;
import org.junit.Before;
import org.junit.Test;
import org.threehook.catena.networking.messaging.listening.fixtures.GreetingResponse;

import java.util.concurrent.TimeUnit;

public class GreetingResponseListenerTest {

    private Address[] seedAddresses = new Address[2];

    @Before
    public void executedBeforeEach() {
        seedAddresses[0] = Address.create("localhost", 4810);
        seedAddresses[0] = Address.create("localhost", 4811);
    }

    @Test()
    public void subscribeToMultipleStreams() {

        // Start seed node
        Cluster alice = Cluster.joinAwait();

        // Join to cluster another member Bob
        Cluster bob = Cluster.joinAwait(alice.address());

        // Join to cluster another member Charles
        Cluster charles = Cluster.joinAwait(alice.address());

        alice.listenGossips().filter(message -> {
            return message.data() instanceof GreetingResponse;
        }).buffer(10, TimeUnit.SECONDS, seedAddresses.length).asObservable().subscribe(success -> {
            System.out.println(success);
        });

        // Spread gossip from bob
        bob.spreadGossip(Message.builder()
                .data(new GreetingResponse("Greetings from ClusterMember Bob")).build());

        // Spread gossip from charles
        charles.spreadGossip(Message.builder()
                .data(new GreetingResponse("Greetings from ClusterMember Charles")).build());

        try {
            Thread.sleep(10000);
        } catch (InterruptedException ie) {

        }
    }

}
