package org.threehook.catena.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.threehook.catena.core.block.Block;
import org.threehook.catena.core.leveldb.BlocksDbSession;
import org.apache.commons.lang3.SerializationUtils;
import org.iq80.leveldb.DB;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public class BlockchainIterator implements Iterator<Block>, Closeable {

    @Autowired
    private BlocksDbSession blocksDbSession;

    private DB db;
    private byte[] currentHash;


    public BlockchainIterator(byte[] currentHash) {
        db = blocksDbSession.getLevelDb();
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
