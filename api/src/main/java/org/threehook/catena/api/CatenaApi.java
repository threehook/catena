package org.threehook.catena.api;

import org.springframework.stereotype.Service;

public interface CatenaApi {

    void createBlockchain(String address);

    String createWallet();

    void listAddresses();

    int getBalance(String address);

    void printChain();

    int reindexUtxo();

    void send(String fromAddress, String toAddress, int amount, boolean mineNow);

    void startNode(String minerAddress, int serverPort);

}
