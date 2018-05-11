package org.threehook.catena.networking.messaging;

import org.threehook.catena.networking.messages.Message;

public abstract class ResponseMessageDispatcher<T extends Message, R extends Message> extends BaseMessageDispatcher<T> implements ResponseMessageProducer<R> {


    protected abstract T createMessage(String targetAddress, R receivedMessage);
    protected abstract void postDispatchMessage();

    @Override
    public void produceMessage(String targetAddress, R receivedMessage) {
        T message = createMessage(targetAddress, receivedMessage);

    }

    @Override
    public void produceAndDispatchMessage(String targetAddress, R receivedMessage) {
        T message = createMessage(targetAddress, receivedMessage);
        dispatchMessage(targetAddress, message);
        postDispatchMessage();
    }

}
