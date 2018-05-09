package org.threehook.catena.networking.messaging;

public interface MessageType {

    public static final class VERSION implements MessageType {};
    public static final class GET_BLOCKS implements MessageType {};
}
