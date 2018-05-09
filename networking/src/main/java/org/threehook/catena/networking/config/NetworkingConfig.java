package org.threehook.catena.networking.config;

import io.scalecube.cluster.Member;
import io.scalecube.transport.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.threehook.catena.core.BlockchainException;
import org.threehook.catena.networking.cluster.ClusterNetwork;
import org.threehook.catena.networking.cluster.ClusterNode;
import org.threehook.catena.networking.messaging.MessageHandlerRegister;
import org.threehook.catena.networking.messaging.MessageType;
import org.threehook.catena.networking.messaging.handlers.GetBlocksMessageHandler;
import org.threehook.catena.networking.messaging.handlers.VersionMessageHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class NetworkingConfig {

    @Value("${node.server.address}")
    private String nodeServerAddress;
    @Value("${node.server.port}")
    private int nodeServerPort;
    @Value("${node.id}")
    private String nodeId;
    @Autowired
    private SeedProperties seedProperties;

    private Map<String, Member> membersByHost = new HashMap<>();


    @Bean
    public ClusterNetwork clusterNetwork() {
        List<ClusterNode> clusterNodes = new ArrayList<>();
        if (!checkSeedAddresses(seedProperties.getSeedAddresses())) {
            throw new BlockchainException("Property 'seed.seed-addresses' in application.properties is invalid or empty.");
        }
        for (SeedAddress seedAddress : seedProperties.getSeedAddresses()) {
            ClusterNode clusterNode = new ClusterNode(seedAddress.getName(), seedAddress.getHost(), seedAddress.getPort());
            clusterNodes.add(clusterNode);
            membersByHost.put(seedAddress.getHost(), new Member(seedAddress.getName(), Address.create(seedAddress.getHost(), seedAddress.getPort())));
        }
        return new ClusterNetwork.Builder().setNodeId(nodeId).setListenerAddress(nodeServerAddress).setLocalPort(nodeServerPort)
                .setClusterNodes(clusterNodes).build();
    }

    @Bean
    public Map<String, Member> membersByHost() {
        return membersByHost;
    }

//    @Bean
//    public List<String> seedProperties() {
//        return seedProperties;
//    }

    @Bean
    public List<SeedAddress> seedAddresses() {
        return seedProperties.getSeedAddresses();
    }

    @Bean
    public VersionMessageHandler versionMessageHandler() {
        return new VersionMessageHandler();
    }

    @Bean
    public GetBlocksMessageHandler getBlocksMessageHandler() {
        return new GetBlocksMessageHandler();
    }

    @Bean
    public MessageHandlerRegister device() {
        MessageHandlerRegister device = new MessageHandlerRegister();
        device.registerHandler(MessageType.VERSION.class, versionMessageHandler());
        device.registerHandler(MessageType.GET_BLOCKS.class, getBlocksMessageHandler());

        return device;
    }

    protected boolean checkSeedAddresses(List<SeedAddress> seedProperties) {
        if (seedProperties.isEmpty()) {
            return false;
        }
        return true;
    }
}
