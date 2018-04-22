package org.threehook.catena.leveldb;

import org.threehook.catena.BlockchainException;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

public class BlocksDbSession {

    private final static String blocksFile = "D:/tmp/simple-blockchain/%s/blocks.db";
    private static final Logger logger = LoggerFactory.getLogger(BlocksDbSession.class);
    private static BlocksDbSession levelDb = new BlocksDbSession();
    private DB db;

    private BlocksDbSession() {
    }

    public static BlocksDbSession getLevelDbSession(String nodeId) {
        String dbFilePath = String.format(blocksFile, nodeId);
        levelDb.createLevelDb(dbFilePath);
        return levelDb;
    }

    public DB getLevelDb() {
        return db;
    }

    public static boolean dbExists(String nodeId) {
        String dbFilePath = String.format(blocksFile, nodeId);
        File dbFile = new File(dbFilePath);
        if (!dbFile.exists()) {
            return false;
        }
        return true;
    }

    private void createLevelDb(String dbFilePath) {
        Options options = new Options();
        options.createIfMissing(true);
        try {
            db = factory.open(new File(dbFilePath), options);
        } catch (IOException ioe) {
            throw new BlockchainException(ioe.getMessage());
        }
    }
}
