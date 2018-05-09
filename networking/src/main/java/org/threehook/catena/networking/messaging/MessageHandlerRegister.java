package org.threehook.catena.networking.messaging;

import org.threehook.catena.networking.messages.Message;

import java.util.HashMap;
import java.util.Map;

public class MessageHandlerRegister {

    @SuppressWarnings("rawtypes")
    private final Map<Class<? extends MessageType>, MessageHandler> handlers = new HashMap<Class<? extends MessageType>, MessageHandler>();

    public <T extends MessageType, M extends Message<T>>  void registerHandler(final Class<T> messageTypeCls, final MessageHandler<T, M> handler) {
        handlers.put(messageTypeCls, handler);
    }

    @SuppressWarnings("unchecked")
    public <T extends MessageType, M extends Message<T>> MessageHandler<T, M> getHandler(final Class<T> messageTypeCls) {
        return handlers.get(messageTypeCls);
    }


    public <T extends MessageType, M extends Message<T>> void send(final M message) {
        MessageHandler<T, M> handler = getHandler(message.getTypeClass());
        handler.handle(message);
    }
}
