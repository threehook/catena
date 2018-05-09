package org.threehook.catena.core.leveldb;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.threehook.catena.core.BlockchainException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

@Component
public class BlocksDbSession {

    @Value("${db.blocks.path}")
    private String dbBlocksPath;

    private DB db;

    public DB getLevelDb() {
        try {
            if (db == null) {
                Options options = new Options();
                options.createIfMissing(true);
                db = factory.open(new File(dbBlocksPath), options);
            }
        } catch (IOException ioe) {
            throw new BlockchainException(ioe.getMessage());
        }
        return db;
    }

//    public void close() {
//        try {
//            if (db != null) {
//                db.close();
//            }
//        } catch (IOException ioe) {
//            throw new BlockchainException(ioe.getMessage());
//        }
//    }

    public boolean dbExists() {
        File dbFile = new File(dbBlocksPath);
        if (!dbFile.exists()) {
            return false;
        }
        return true;
    }

}


