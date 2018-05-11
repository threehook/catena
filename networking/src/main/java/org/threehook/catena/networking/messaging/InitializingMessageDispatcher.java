package org.threehook.catena.networking.messaging;

import io.scalecube.cluster.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.threehook.catena.core.Blockchain;
import org.threehook.catena.core.BlockchainFactory;
import org.threehook.catena.networking.cluster.ClusterNetwork;
import org.threehook.catena.networking.messages.Message;

import java.util.Map;

public abstract class InitializingMessageDispatcher<T extends Message> extends BaseMessageDispatcher<T> implements InitializingMessageProducer<T> {

    protected abstract T createMessage(String targetAddress);

    public T produceMessage(String targetAddress) {
        return createMessage(targetAddress);
    }

    public void produceAndSendMessage(String targetAddress) {
        T message = createMessage(targetAddress);
        dispatchMessage(targetAddress, message);
    }

}
