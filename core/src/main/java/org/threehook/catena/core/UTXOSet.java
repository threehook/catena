package org.threehook.catena.core;

import com.google.common.collect.Iterators;
import org.bouncycastle.util.encoders.Hex;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.threehook.catena.core.block.Block;
import org.threehook.catena.core.leveldb.ChainstateDbSession;
import org.threehook.catena.core.transaction.Transaction;
import org.threehook.catena.core.transaction.TransactionInput;
import org.threehook.catena.core.transaction.TransactionOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UTXOSet {

    @Autowired
    private ChainstateDbSession chainstateDbSession;

    Blockchain blockchain;

    public UTXOSet(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    // Finds and returns unspent outputs to reference in inputs
    public int findSpendableOutputs(byte[] pubKeyHash, int amount, Map<String, Integer[]> unspentOutputs) {
        int accumulated = 0;
//        DB chainstateDb = ChainstateDbSession.getLevelDbSession(System.getenv("NODE_ID")).getLevelDb();
        DB chainstateDb = chainstateDbSession.getLevelDb();
        DBIterator iterator = chainstateDb.iterator();
        iterator.seekToFirst();
        List<Integer> txIds = new ArrayList<>();
        while (iterator.hasNext()){
            byte[] key = iterator.peekNext().getKey();
            byte[] value = iterator.peekNext().getValue();
            String txId = Hex.toHexString(key);
            TransactionOutput[] outs = TransactionOutput.deserializeOutputs(value);
            for (int i=0; i < outs.length; i++) {
                if (outs[i].isLockedWithKey(pubKeyHash) && accumulated < amount) {
                    int out = outs[i].getValue();
                    accumulated += out;
                    Integer[] existingTxIds = unspentOutputs.get(txId);
                    if (existingTxIds != null) {
                        txIds.addAll(Arrays.asList(existingTxIds));
                    } else {
                        unspentOutputs.put(txId, new Integer[]{i});
                    }
                }
            }
            iterator.next();
        }
//        try {
//            chainstateDb.close();
//        } catch (IOException ioe) {
//            throw new BlockchainException(ioe.getMessage());
//        }
        return accumulated;
    }


    // FindUTXO finds UTXO for a public key hash
    public List<TransactionOutput> findUTXO(byte[] pubKeyHash) {
        List<TransactionOutput> utxos = new ArrayList<>();
//        DB chainstateDb = ChainstateDbSession.getLevelDbSession(System.getenv("NODE_ID")).getLevelDb();
        DB chainstateDb = chainstateDbSession.getLevelDb();
        DBIterator iterator = chainstateDb.iterator();
        iterator.seekToFirst();

        while (iterator.hasNext()){
            byte[] value = iterator.peekNext().getValue();
            TransactionOutput[] transactionOutputs = TransactionOutput.deserializeOutputs(value);
            for (TransactionOutput txOutput : transactionOutputs) {
                if (txOutput.isLockedWithKey(pubKeyHash)) {
                    utxos.add(txOutput);
                }
            }
            iterator.next();
        }
//        try {
//            chainstateDb.close();
//        } catch (IOException ioe) {
//            throw new BlockchainException(ioe.getMessage());
//        }
        return utxos;
    }

    // Updates the UTXO set with transactions from the Block
    // The Block is considered to be the tip of a blockchain
    public void update(Block block) {
        //TODO: What about transactional integrity if one of two chainstateDb updates fails?
//        DB chainstateDb = ChainstateDbSession.getLevelDbSession(System.getenv("NODE_ID")).getLevelDb();
        DB chainstateDb = chainstateDbSession.getLevelDb();
        for (Transaction tx : block.getTransactions()) {
            if (!tx.isCoinBase()) {
                List<TransactionOutput> updatedTransactionOutputs = new ArrayList<>();
                for (TransactionInput txInput : tx.getTransactionInputs()) {
                    byte[] outsBytes = chainstateDb.get(txInput.getTxId());
                    TransactionOutput[] outs = TransactionOutput.deserializeOutputs(outsBytes);
                    for (int i=0; i < outs.length; i++) {
                        TransactionOutput txOutput = outs[i];
                        if (i != txInput.getvOut()) {
                            updatedTransactionOutputs.add(txOutput);
                        }
                    }
                    if (updatedTransactionOutputs.size() == 0) {
                        try {
                            chainstateDb.delete(txInput.getTxId());
                        } catch (DBException dbe) {
                            System.out.println("ERROR: Updating UTXOs failed");
                            System.exit(1);
                        }
                    } else {
                        chainstateDb.put(txInput.getTxId(), TransactionOutput.serializeOutputs(updatedTransactionOutputs.stream().toArray(TransactionOutput[]::new)));
                    }
                }

                List<TransactionOutput> newTransactionOutputs = new ArrayList<>();
                for (TransactionOutput txOutPut : tx.getTransactionOutputs()) {
                    newTransactionOutputs.add(txOutPut);
                }
                chainstateDb.put(tx.getId(), TransactionOutput.serializeOutputs(newTransactionOutputs.stream().toArray(TransactionOutput[]::new)));
            }

        }
//        try {
//            chainstateDb.close();
//        } catch (IOException ioe) {
//            throw new BlockchainException(ioe.getMessage());
//        }
    }

    // Rebuilds the UTXO set
    public void reindex() {
//        DB chainstateDb = ChainstateDbSession.getLevelDbSession(System.getenv("NODE_ID")).getLevelDb();
        DB chainstateDb = chainstateDbSession.getLevelDb();
        //Delete the database
//        ChainstateDbSession.delete();

        Map<String, List<TransactionOutput>> utxos = blockchain.findUTXO();
        for (Map.Entry<String, List<TransactionOutput>> entry : utxos.entrySet()) {
            byte[] key = Hex.decode(entry.getKey());
            List<TransactionOutput> outs = entry.getValue();
            TransactionOutput[] txOutputs = outs.stream().toArray(TransactionOutput[]::new);
            chainstateDb.put(key, TransactionOutput.serializeOutputs(txOutputs));
        }

//        try {
//            chainstateDb.close();
//        } catch (IOException ioe) {
//            throw new BlockchainException(ioe.getMessage());
//        }

    }

    // Returns the number of transactions in the UTXO set
    public int countTransactions() {
//        DB chainstateDb = ChainstateDbSession.getLevelDbSession(System.getenv("NODE_ID")).getLevelDb();
        DB chainstateDb = chainstateDbSession.getLevelDb();
        DBIterator iterator = chainstateDb.iterator();
        iterator.seekToFirst();
        int count = Iterators.size(iterator);
//        try {
//            chainstateDb.close();
//        } catch (IOException ioe) {
//            throw new BlockchainException(ioe.getMessage());
//        }
        return count;
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }



}
