package org.threehook.catena.networking.messaging.dispatchers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.threehook.catena.networking.messages.GetBlocksData;
import org.threehook.catena.networking.messages.Inventory;
import org.threehook.catena.networking.messaging.ResponseMessageDispatcher;
import org.threehook.catena.networking.serverthread.ServerThread;

import java.util.List;

@Component
public class GetBlocksDataMessageProducer extends ResponseMessageDispatcher<GetBlocksData, Inventory> {

//    private final static String BLOCK = "Block";

    @Value("${node.server.address}")
    private String nodeServerAddress;
    @Value("${node.server.port}")
    private int nodeServerPort;

    @Override
    protected GetBlocksData createMessage(String targetAddress, Inventory receivedMessage) {
        List<byte[]> blocksInTransit = receivedMessage.getItems();
        byte[] blockHash = receivedMessage.getItems().get(0);
        String addressFrom = nodeServerAddress + ":" + nodeServerPort;

        return new GetBlocksData(addressFrom, "block", blockHash);

    }

    protected void postDispatchMessage() {
        ThreadLocal<List<byte[]>> newBlocksInTransit = ServerThread.getNewBlocksInTransit();
        System.out.println(newBlocksInTransit.get());
    }
}

//	if payload.Type == "block" {
//            blocksInTransit = payload.Items
//
//            blockHash := payload.Items[0]
//            sendGetData(payload.AddrFrom, "block", blockHash)
//
//            newInTransit := [][]byte{}
//            for _, b := range blocksInTransit {
//            if bytes.Compare(b, blockHash) != 0 {
//            newInTransit = append(newInTransit, b)
//            }
//            }
//            blocksInTransit = newInTransit
//            }