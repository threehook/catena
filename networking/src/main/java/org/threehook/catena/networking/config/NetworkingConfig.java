package org.threehook.catena.networking.config;

import io.scalecube.cluster.ClusterConfig;
import io.scalecube.cluster.Member;
import io.scalecube.services.Microservices;
import io.scalecube.transport.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.threehook.catena.networking.messaging.services.VersionServiceImpl;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class NetworkingConfig {

    @Value("${node.server.address}")
    private String nodeServerAddress;
    @Value("${node.server.port}")
    private int nodeServerPort;
    @Value("${node.service.port}")
    private int nodeServicePort;
    @Value("${node.id}")
    private String nodeId;
    @Autowired
    private SeedProperties seedProperties;

    private Map<String, Member> membersByHost = new HashMap<>();

    @Bean
    public Map<String, Member> membersByHost() {
        return membersByHost;
    }

    @Bean
    public VersionServiceImpl versionService() {
        return new VersionServiceImpl();
    }

    @Bean
    public ClusterConfig.Builder clusterConfig() {
        ClusterConfig.Builder builder = ClusterConfig.builder();
        return builder
                .listenAddress(nodeServerAddress)
                .port(nodeServerPort);
    }

    // Create microservice consumer
    @Bean(name="consumer")
    public Microservices consumer() {
        return Microservices.builder()
                .clusterConfig(clusterConfig())
                .seeds(seedAddresses())
                .services(versionService())
                .servicePort(nodeServicePort)
                .build().startAwait();
    }

    @Bean
    public Address[] seedAddresses() {
        return seedProperties.getAddresses();
    }

}
