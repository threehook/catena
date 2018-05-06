package org.threehook.catena.networking.messages;

import org.threehook.catena.networking.messaging.MessageHandler;

import java.io.Serializable;

public abstract class Message implements Serializable {

    public abstract MessageHandler getMessageHandler();
}
