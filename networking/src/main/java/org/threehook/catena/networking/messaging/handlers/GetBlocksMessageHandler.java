package org.threehook.catena.networking.messaging.handlers;

import org.threehook.catena.networking.messages.GetBlocks;
import org.threehook.catena.networking.messages.Version;
import org.threehook.catena.networking.messaging.BaseMessageHandler;
import org.threehook.catena.networking.messaging.MessageType;

public class GetBlocksMessageHandler extends BaseMessageHandler<MessageType.GET_BLOCKS, GetBlocks> {

    @Override
    protected void processMessage(GetBlocks message) {

    }
}
