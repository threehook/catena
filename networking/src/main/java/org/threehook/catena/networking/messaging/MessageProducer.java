package org.threehook.catena.networking.messaging;

import org.threehook.catena.networking.messages.Message;

public interface MessageProducer<T extends Message>{


//    public T produceMessage(/*String nodeAddress,*/ String targetAddress) {
//        return produceMessage(targetAddress, getBlockchain());
//    }

    T produceMessage(String targetAddress);
}
