package org.threehook.catena.networking.config;

import io.scalecube.transport.Address;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("scalecube.seed")
public class SeedProperties {

    private Address[] addresses;

    public Address[] getAddresses() {
        return addresses;
    }

    public void setAddresses(Address[] addresses) {
        this.addresses = addresses;
    }

}
