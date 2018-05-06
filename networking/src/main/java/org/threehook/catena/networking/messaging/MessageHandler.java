package org.threehook.catena.networking.messaging;

import org.threehook.catena.networking.messages.Message;

public interface MessageHandler<T extends Message> {

    void handleMessage(T message);


}
