package org.threehook.catena.core;

import org.bouncycastle.util.encoders.Hex;
import org.iq80.leveldb.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.threehook.catena.core.block.Block;
import org.threehook.catena.core.leveldb.BlocksDbSession;
import org.threehook.catena.core.transaction.Transaction;
import org.threehook.catena.core.transaction.TransactionFactory;
import org.threehook.catena.core.util.ByteUtils;

import java.io.IOException;
import java.util.Collections;

@Component
public class BlockchainFactory {

    private final static String genesisCoinbaseData = "The Times 03/Jan/2009 Chancellor on brink of second bailout for banks";

    @Autowired
    private BlocksDbSession blocksDbSession;

    // Creates a new blockchain DB
    public Blockchain createBlockchain(String address) {
        if (blocksDbSession.dbExists()) {
            System.out.println("Blockchain already exists.");
            System.exit(1);
        }

        Transaction cbtx = TransactionFactory.createCoinBaseTransaction(address, genesisCoinbaseData);

        Block genesis = new Block(Collections.singletonList(cbtx), new byte[]{}, 0);
        DB db =  blocksDbSession.getLevelDb();
        Blockchain blockchain = new Blockchain(genesis.getHash());
        db.put(genesis.getHash(), genesis.serialize());
        db.put(ByteUtils.stringToBytes("l"), genesis.getHash());

        System.out.println("Genesis hash: " + Hex.toHexString(genesis.getHash()));
        System.out.println("Genesis nonce: " + genesis.getNonce());

        try {
            db.close();
        } catch (IOException ioe) {
            throw new BlockchainException(ioe.getMessage());
        }

        return blockchain;
    }

    // Gets the Blockchain by retrieving the last block (the tip) from the database
    public Blockchain getBlockchain() {
        if (!blocksDbSession.dbExists()) {
            System.out.println("No existing blockchain found. Create one first.");
            System.exit(1);
        }
        DB db = blocksDbSession.getLevelDb();
        Blockchain blockchain = new Blockchain(db.get(ByteUtils.stringToBytes("l")));

        try {
            db.close();
        } catch (IOException ioe) {
            throw new BlockchainException(ioe.getMessage());
        }

        return blockchain;
    }

}
