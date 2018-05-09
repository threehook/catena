package org.threehook.catena.networking.messaging;

import org.threehook.catena.networking.messages.Message;

public abstract class BaseMessageHandler<T extends MessageType, M extends Message<T>> implements MessageHandler<T, M> {

    protected abstract void processMessage(M message);

    public void handle(M message) {
        processMessage(message);
    }

}
