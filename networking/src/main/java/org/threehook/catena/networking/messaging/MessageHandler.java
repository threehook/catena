package org.threehook.catena.networking.messaging;

import org.threehook.catena.networking.messages.Message;

public interface MessageHandler<T extends MessageType, M extends Message<T>> {
    void handle(M message);
}