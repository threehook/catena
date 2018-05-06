package org.threehook.catena.networking.messaging.producers;

import io.scalecube.cluster.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threehook.catena.core.Blockchain;
import org.threehook.catena.networking.cluster.ClusterNetwork;
import org.threehook.catena.networking.messages.Message;
import org.threehook.catena.networking.messages.Version;
import org.threehook.catena.networking.messaging.BaseMessageProducer;

import java.util.Map;

@Component
public class VersionMessageProducer extends BaseMessageProducer<Version> {

    @Autowired
    private ClusterNetwork clusterNetwork;
    @Autowired
    private Map<String, Member> membersByHost;

    private final static int NODE_VERSION = 1; //TODO: Do something with this

    @Override
    protected Version createMessage(String nodeAddress) {
        Blockchain blockchain = getBlockchain();
        int bestHeight = blockchain.getBestHeight();
        return new Version(NODE_VERSION, bestHeight, nodeAddress);
    }

    @Override
    public void createAndSendMessage(String nodeAddress) {
        Version version = createMessage(nodeAddress);
        sendMessage(nodeAddress, version);
    }

    @Override
    protected void sendMessage(String nodeAddress, Message message) {
        io.scalecube.transport.Message versionMessage = io.scalecube.transport.Message.builder()
                .data(message).build();
        clusterNetwork.getCluster().send(membersByHost.get(nodeAddress), versionMessage);
    }
}
