package org.threehook.catena.networking.messaging;

public interface MessageType {

    public static final class VERSION implements MessageType {};
    public static final class GET_BLOCKS implements MessageType {};
    public static final class INVENTORY implements MessageType {};
    public static final class GET_BLOCKS_DATA implements MessageType {};
}
