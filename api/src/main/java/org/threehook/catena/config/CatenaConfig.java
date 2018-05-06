package org.threehook.catena.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatenaConfig {

    @Value("${node.id}")
    private String nodeId;

//    @Value("${node.server.port}")
//    private int nodeServerPort;

    @Bean(name="nodeId")
    public String nodeId() {
        return nodeId;
    }

//    @Bean
//    public int nodeServerPort() {
//        return nodeServerPort;
//    }

}
