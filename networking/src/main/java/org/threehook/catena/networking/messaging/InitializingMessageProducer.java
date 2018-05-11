package org.threehook.catena.networking.messaging;

import org.threehook.catena.networking.messages.Message;

public interface InitializingMessageProducer<T extends Message>{

    T produceMessage(String targetAddress);
    void produceAndSendMessage(String targetAddress);
}
