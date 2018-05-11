package org.threehook.catena.networking.messaging;

import org.threehook.catena.networking.messages.Message;

public interface ResponseMessageProducer<R extends Message> {

    void produceMessage(String targetAddress, R receivedMessage);
    void produceAndDispatchMessage(String targetAddress, R receivedMessage);
}
