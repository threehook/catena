package org.threehook.catena.networking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("seed")
public class SeedProperties {

    private List<SeedAddress> seedAddresses = new ArrayList<>();

    public List<SeedAddress> getSeedAddresses() {
        return seedAddresses;
    }

    public void setSeedAddresses(List<SeedAddress> seedAddresses) {
        this.seedAddresses = seedAddresses;
    }

}
