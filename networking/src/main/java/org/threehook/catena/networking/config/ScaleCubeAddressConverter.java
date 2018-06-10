package org.threehook.catena.networking.config;

import io.scalecube.transport.Address;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class ScaleCubeAddressConverter implements Converter<String, Address> {
    @Override
    public Address convert(String source) {
        if(source==null){
            return null;
        }
        return Address.from(source);
    }
}