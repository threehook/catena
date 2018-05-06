package org.threehook.catena.networking.messaging;

import org.threehook.catena.networking.messages.Message;

public interface MessageDispatcherService {

    void sendMessage(Message message);

}
