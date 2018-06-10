package org.threehook.catena.networking.aspects;

import io.scalecube.services.Microservices;
import io.scalecube.services.ServiceCall;
import io.scalecube.services.transport.LocalServiceHandlers;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class ScaleCubeAspect {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${node.server.address}")
    private String nodeServerAddress;
    @Value("${node.service.port}")
    private int nodeServicePort;

    @Around("execution(* io.scalecube.services.Microservices.call(..))")
    public ServiceCall.Call aroundMicroservicesCall(ProceedingJoinPoint joinPoint) throws Throwable {
        LOGGER.debug("Removing the local service endpoint for ScaleCube via {}.", joinPoint);
        Microservices consumer = (Microservices) joinPoint.getTarget();

        Field serviceDispatchersField = ReflectionUtils.findField(Microservices.class, "serviceHandlers");
        ReflectionUtils.makeAccessible(serviceDispatchersField);
        LocalServiceHandlers localServiceHandlers = (LocalServiceHandlers) ReflectionUtils.getField(serviceDispatchersField, consumer);
        List<Object> services = new ArrayList(localServiceHandlers.services());
        localServiceHandlers = localServiceHandlers.builder().services(services).build();
        ReflectionUtils.setField(serviceDispatchersField, consumer, localServiceHandlers);

        return (ServiceCall.Call) joinPoint.proceed();
    }

}
