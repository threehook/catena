package org.threehook.catena.networking.messages;

import org.threehook.catena.networking.messaging.MessageType;

public interface Message<T extends MessageType> {

    Class<T> getTypeClass();
}
