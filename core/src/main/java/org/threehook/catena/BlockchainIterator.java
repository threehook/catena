package org.threehook.catena;

import org.threehook.catena.block.Block;
import org.threehook.catena.leveldb.BlocksDbSession;
import org.apache.commons.lang3.SerializationUtils;
import org.iq80.leveldb.DB;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public class BlockchainIterator implements Iterator<Block>, Closeable {

    private DB db;
    private byte[] currentHash;


    public BlockchainIterator(byte[] currentHash) {
        db = BlocksDbSession.getLevelDbSession(System.getenv("NODE_ID")).getLevelDb();
        this.currentHash = currentHash;
    }

    @Override
    public boolean hasNext() {
        return currentHash.length > 0;
    }

    @Override
    // Next returns next block starting from the tip
    public Block next() {

        byte[] encodedBlock = db.get(currentHash);
        Block block = SerializationUtils.deserialize(encodedBlock);
        currentHash = block.getPrevBlockHash();

        return block;
    }

    public void close() {
        try {
            db.close();
        } catch (IOException ioe) {
            throw new BlockchainException(ioe.getMessage());
        }
    }
}
