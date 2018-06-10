package org.threehook.catena.networking.messaging.routing;

import io.scalecube.services.ServiceReference;
import io.scalecube.services.api.ServiceMessage;
import io.scalecube.services.registry.api.ServiceRegistry;
import io.scalecube.services.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomRemoteSeedMemberServiceRouter implements Router {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomRemoteSeedMemberServiceRouter.class);

    @Value("${node.server.address}")
    private String nodeServerAddress;
    @Value("${node.service.port}")
    private int nodeServicePort;

    @Override
    public Optional<ServiceReference> route(ServiceRegistry serviceRegistry, ServiceMessage request) {
        List<ServiceReference> serviceInstances = this.routes(serviceRegistry, request);
        if (!serviceInstances.isEmpty()) {
            List<ServiceReference> remoteServiceInstances = new ArrayList<>();
            // Filter out the service instance of our own local seed member
            for (ServiceReference serviceInstance : serviceInstances) {
                if (!serviceInstance.host().equals(nodeServerAddress) || serviceInstance.port() != nodeServicePort /*((LocalNode)ctx.getBean("localNode")).getNodeServerPort()*/) {
                    remoteServiceInstances.add(serviceInstance);
                }
            }
            int index = ThreadLocalRandom.current().nextInt(serviceInstances.size()-1);
            return Optional.of(remoteServiceInstances.get(index));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<ServiceReference> routes(ServiceRegistry serviceRegistry, ServiceMessage serviceMessage) {
        return serviceRegistry.lookupService(serviceMessage.qualifier());
    }

}
