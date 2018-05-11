package org.threehook.catena.networking.messaging;

import io.scalecube.cluster.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threehook.catena.core.Blockchain;
import org.threehook.catena.core.BlockchainFactory;
import org.threehook.catena.networking.cluster.ClusterNetwork;
import org.threehook.catena.networking.messages.Message;

import java.util.Map;

@Component
public class BaseMessageDispatcher<T extends Message> {

    @Autowired
    private ClusterNetwork clusterNetwork;
    @Autowired
    private Map<String, Member> membersByHost;
    @Autowired
    private BlockchainFactory blockchainFactory;

    protected void dispatchMessage(String targetAddress, T message) {
        io.scalecube.transport.Message versionMessage = io.scalecube.transport.Message.builder().data(message).build();
        clusterNetwork.getCluster().send(membersByHost.get(targetAddress), versionMessage);
    }

    protected Blockchain getBlockchain() {
        return blockchainFactory.getBlockchain();
    }
}
