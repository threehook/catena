package org.threehook.catena;

import org.threehook.catena.block.Block;
import org.threehook.catena.leveldb.BlocksDbSession;
import org.threehook.catena.transaction.Transaction;
import org.threehook.catena.transaction.TransactionFactory;
import org.threehook.catena.util.ByteUtils;
import org.bouncycastle.util.encoders.Hex;
import org.iq80.leveldb.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

public class BlockchainFactory {

    private final static String genesisCoinbaseData = "The Times 03/Jan/2009 Chancellor on brink of second bailout for banks";
    private static final Logger logger = LoggerFactory.getLogger(Blockchain.class);

    // Creates a new blockchain DB
    public static Blockchain createBlockchain(String address, String nodeId) {
        if (BlocksDbSession.dbExists(nodeId)) {
            logger.error("Blockchain already exists.");
            System.exit(1);
        }

        Transaction cbtx = TransactionFactory.createCoinBaseTransaction(address, genesisCoinbaseData);

        Block genesis = new Block(Collections.singletonList(cbtx), new byte[]{}, 0);
        DB db =  BlocksDbSession.getLevelDbSession(nodeId).getLevelDb();
        Blockchain blockchain = new Blockchain(genesis.getHash());
        db.put(genesis.getHash(), genesis.serialize());
        db.put(ByteUtils.stringToBytes("l"), genesis.getHash());

        logger.info("Genesis hash: " + Hex.toHexString(genesis.getHash()));
        logger.info("Genesis nonce: " + genesis.getNonce());

        try {
            db.close();
        } catch (IOException ioe) {
            throw new BlockchainException(ioe.getMessage());
        }

        return blockchain;
    }

    // Creates a new Blockchain by retrieving the last block (the tip) from the database
    public static Blockchain getBlockchain(String nodeId) {
        if (!BlocksDbSession.dbExists(nodeId)) {
            logger.error("No existing blockchain found. Create one first.");
            System.exit(1);
        }
        DB db = BlocksDbSession.getLevelDbSession(nodeId).getLevelDb();
        Blockchain blockchain = new Blockchain(db.get(ByteUtils.stringToBytes("l")));

        try {
            db.close();
        } catch (IOException ioe) {
            throw new BlockchainException(ioe.getMessage());
        }

        return blockchain;
    }

}
