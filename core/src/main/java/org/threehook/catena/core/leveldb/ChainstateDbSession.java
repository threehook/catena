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
public class ChainstateDbSession {

    @Value("${db.chainstate.path}")
    private String dbChainstatePath;

    private DB db;

    @PostConstruct
    public void init(){
        createLevelDb();
    }

    public DB getLevelDb() {
        return db;
    }

    private void createLevelDb() {
        Options options = new Options();
        options.createIfMissing(true);
        try {
            db = factory.open(new File(dbChainstatePath), options);
        } catch (IOException ioe) {
            throw new BlockchainException(ioe.getMessage());
        }
    }

}
