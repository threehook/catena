package org.threehook.catena.networking.messaging.dispatchers;

import org.springframework.stereotype.Component;
import org.threehook.catena.networking.messages.GetBlocks;
import org.threehook.catena.networking.messaging.InitializingMessageDispatcher;

@Component
public class GetBlocksMessageProducer extends InitializingMessageDispatcher<GetBlocks> {

    @Override
    public GetBlocks createMessage(String targetAddress) {
        return new GetBlocks(targetAddress);
    }

    @Override
    public void produceAndSendMessage(String targetAddress) {
        GetBlocks getBlocks = createMessage(targetAddress);
        dispatchMessage(targetAddress, getBlocks);
    }


}
