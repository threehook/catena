package org.threehook.catena.networking.messaging.routing;

import io.scalecube.services.ServiceReference;
import io.scalecube.services.api.ServiceMessage;
import io.scalecube.services.registry.api.ServiceRegistry;
import io.scalecube.services.routing.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberRouter implements Router {

    @Override
    public Optional<ServiceReference> route(ServiceRegistry serviceRegistry, ServiceMessage request) {
        List<ServiceReference> serviceInstances = this.routes(serviceRegistry, request);
        String host = request.header("fromHost");
        int port = Integer.parseInt(request.header("fromPort"));

        if (!serviceInstances.isEmpty()) {
            List<ServiceReference> remoteServiceInstances = new ArrayList<>();
            // Add the service instance that equals the specific member with host/port
            for (ServiceReference serviceInstance : serviceInstances) {
                if (serviceInstance.host().equals(host) && serviceInstance.port() == port) {
                    remoteServiceInstances.add(serviceInstance);
                }
            }
            return Optional.of(remoteServiceInstances.get(0));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<ServiceReference> routes(ServiceRegistry serviceRegistry, ServiceMessage serviceMessage) {
        return serviceRegistry.lookupService(serviceMessage.qualifier());
    }
}
