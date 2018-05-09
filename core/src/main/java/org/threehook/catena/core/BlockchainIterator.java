package org.threehook.catena.core;

import org.apache.commons.lang3.SerializationUtils;
import org.iq80.leveldb.DB;
import org.threehook.catena.core.block.Block;

import java.util.Iterator;

public class BlockchainIterator implements Iterator<Block> {

    private DB db;
    private byte[] tip;


    public BlockchainIterator(byte[] tip, DB db) {
        this.db = db;
        this.tip = tip;
    }

    @Override
    public boolean hasNext() {
        return tip.length > 0;
    }

    @Override
    // Next returns next block starting from the tip
    public Block next() {

        byte[] encodedBlock = db.get(tip);
        Block block = SerializationUtils.deserialize(encodedBlock);
        tip = block.getPrevBlockHash();

        return block;
    }

}
