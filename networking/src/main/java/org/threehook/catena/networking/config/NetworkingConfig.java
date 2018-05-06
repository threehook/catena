package org.threehook.catena.networking.config;

import io.scalecube.cluster.Member;
import io.scalecube.transport.Address;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.threehook.catena.core.BlockchainException;
import org.threehook.catena.networking.cluster.ClusterNetwork;
import org.threehook.catena.networking.cluster.ClusterNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class NetworkingConfig {

    private final static String SEPARATOR = ":";

    @Value("${node.server.address}")
    private String nodeServerAddress;
    @Value("${node.server.port}")
    private int nodeServerPort;
    @Value("${node.id}")
    private String nodeId;
    @Value("#{'${known.node.addresses}'.split(',')}")
    private List<String> seedAddresses;

    private Map<String, Member> membersByHost = new HashMap<>();

    @Bean
    public ClusterNetwork clusterNetwork() {
        List<ClusterNode> clusterNodes = new ArrayList<>();
        if (!checkSeedAddresses(seedAddresses)) {
            throw new BlockchainException("Property 'known.node.addresses' in application.properties is invalid or empty.");
        }
        for (String knownNodeAddress : seedAddresses) {
            String[] data = knownNodeAddress.split(SEPARATOR);
            ClusterNode clusterNode = new ClusterNode(data[0], data[1], Integer.valueOf(data[2]));
            clusterNodes.add(clusterNode);
            membersByHost.put(data[1], new Member(clusterNode.getName(), Address.create(clusterNode.getHost(), clusterNode.getPort())));
        }
        return new ClusterNetwork.Builder().setNodeId(nodeId).setListenerAddress(nodeServerAddress).setLocalPort(nodeServerPort)
                .setClusterNodes(clusterNodes).build();
    }

    @Bean
    public Map<String, Member> membersByHost() {
        return membersByHost;
    }

    @Bean
    public List<String> seedAddresses() {
        return seedAddresses;
    }

    protected boolean checkSeedAddresses(List<String> seedAddresses) {
        if (seedAddresses.isEmpty()) {
            return false;
        }
        return seedAddresses.stream().allMatch(item -> {
            String[] data = item.split(SEPARATOR);
            if (data.length != 3) return false;
            try {
                Integer.getInteger(data[2]);
            } catch (NumberFormatException nfe) {
                return false;
            }
            return true;
        });
    }

}
