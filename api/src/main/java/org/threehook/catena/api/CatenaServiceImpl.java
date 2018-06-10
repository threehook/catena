package org.threehook.catena.api;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.threehook.catena.core.Blockchain;
import org.threehook.catena.core.BlockchainFactory;
import org.threehook.catena.core.BlockchainIterator;
import org.threehook.catena.core.UTXOSet;
import org.threehook.catena.core.block.Block;
import org.threehook.catena.core.pow.ProofOfWork;
import org.threehook.catena.core.transaction.Transaction;
import org.threehook.catena.core.transaction.TransactionFactory;
import org.threehook.catena.core.transaction.TransactionOutput;
import org.threehook.catena.core.util.Base58;
import org.threehook.catena.core.wallet.Wallet;
import org.threehook.catena.core.wallet.Wallets;
import org.threehook.catena.networking.Server;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class CatenaServiceImpl implements CatenaApi {

    private static final Logger log = LoggerFactory.getLogger(CatenaServiceImpl.class);

    @Autowired
    private BlockchainFactory blockchainFactory;
    @Autowired
    private TransactionFactory transactionFactory;
    @Autowired
    private Wallets wallets;
    @Autowired
    private Server server;
    @Autowired
    private UTXOSet utxoSet;

    @Override
    public void createBlockchain(String address) {
//        System.out.println("Address is: " + address);
//        System.out.println("NodeId is: " + nodeId);
        blockchainFactory.createBlockchain(address);
        utxoSet.reindex();
    }

    @Override
    public String createWallet() {
//        System.out.println("NodeId is: " + nodeId);
//        Wallets wallets = new Wallets();
        String address = wallets.createWallet();
        wallets.saveToFile();
        return address;
    }

    @Override
    public void listAddresses() {
//        System.out.println("NodeId is: " + nodeId);
//        Wallets wallets = new Wallets();
        for (String address : wallets.getAddresses()) {
            System.out.println(address);
        }
    }

    @Override
    public int getBalance(String address) {
//        System.out.println("NodeId is: " + nodeId);
        int balance = 0;
//        Blockchain blockchain = blockchainFactory.getBlockchain();
        byte[] pubKeyHash = Base58.decode(address);

        pubKeyHash = Arrays.copyOfRange(pubKeyHash, 1, pubKeyHash.length-4);
        List<TransactionOutput> utxos = utxoSet.findUTXO(pubKeyHash);

        for (TransactionOutput utxo : utxos) {
            balance += utxo.getValue();
        }
        return balance;
    }

    @Override
    public void printChain() {
//        System.out.println("NodeId is: " + nodeId);
        Blockchain blockchain = blockchainFactory.getBlockchain();
        BlockchainIterator bci = blockchain.iterator();
        while (bci.hasNext()) {
            Block block = bci.next();
            System.out.printf("============ Block %s ============\n", Hex.toHexString(block.getHash()));
            System.out.printf("Height: %d\n", block.getHeight());
            System.out.printf("Prev. block: %s\n", Hex.toHexString(block.getPrevBlockHash()));
            ProofOfWork pow = new ProofOfWork(block);
            System.out.printf("PoW: %s\n\n", pow.validate());
            for (Transaction tx : block.getTransactions()) {
                System.out.println(tx);
            }
            System.out.printf("\n\n");
            if (block.getPrevBlockHash().length == 0) {
                break;
            }
        }
    }

    @Override
    public int reindexUtxo() {
//        System.out.println("NodeId is: " + nodeId);
        Blockchain blockchain = blockchainFactory.getBlockchain();
        utxoSet.reindex();
        return utxoSet.countTransactions();
    }

    @Override
    public void send(String fromAddress, String toAddress, int amount, boolean mineNow) {
//        System.out.println("NodeId is: " + System.getenv("NODE_ID"));
        if (!Wallet.validateAddress(fromAddress)) {
            System.out.println("ERROR: Sender address is not valid");
        }
        if (!Wallet.validateAddress(toAddress)) {
            System.out.println("ERROR: Recipient address is not valid");
        }
        Blockchain blockchain = blockchainFactory.getBlockchain();
//        Wallets wallets = new Wallets();
        Wallet wallet = wallets.getWallet(fromAddress);

        Transaction tx = transactionFactory.createUTXOTransaction(wallet, toAddress, amount, utxoSet);
        if (mineNow) {
            Transaction cbtx = transactionFactory.createCoinBaseTransaction(fromAddress, "");
            Transaction[] txs = new Transaction[] {cbtx, tx};
            Block newBlock = blockchain.mineBlock(txs);
            utxoSet.update(newBlock);
        } else {
            //TODO
            //server.sendTx(server.getSeedAddresses[0], tx);
        }
    }

    @Override
    public void startNode(String minerAddress, int serverPort) {
//        System.out.printf("Starting node %s\n", nodeId);
        if (minerAddress.length() > 0) {
            log.info("Mining is on. Address to receive rewards: " + minerAddress);
        } else {
            log.error("Wrong miner address!");
        }
        server.startServer(minerAddress);
    }
}
