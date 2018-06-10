package org.threehook.catena.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.LoadTimeWeavingConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.ReflectiveLoadTimeWeaver;

import java.util.Locale;

@Configuration
public class CatenaConfig {

    @Value("${node.id}")
    private String nodeId;

//    @Value("${node.server.port}")
//    private int nodeServerPort;

    @Bean(name = "nodeId")
    public String nodeId() {
        return nodeId;
    }

//    @Bean
//    public int nodeServerPort() {
//        return nodeServerPort;
//    }

}