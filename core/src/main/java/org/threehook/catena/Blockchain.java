package org.threehook.catena;

import org.threehook.catena.block.Block;
import org.threehook.catena.leveldb.BlocksDbSession;
import org.threehook.catena.transaction.Transaction;
import org.threehook.catena.transaction.TransactionInput;
import org.threehook.catena.transaction.TransactionOutput;
import org.threehook.catena.util.ByteUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.bouncycastle.util.encoders.Hex;
import org.iq80.leveldb.DB;

import java.io.IOException;
import java.security.interfaces.ECPrivateKey;
import java.util.*;

public class Blockchain {

    byte[] tip;

    public Blockchain(byte[] tip) {
        this.tip = tip;
    }

    // SignTransaction signs inputs of a Transaction
    public void signTransaction(Transaction tx, ECPrivateKey privateKey) {
        Map<String, Transaction> prevTXs = new HashMap<>();
        for (TransactionInput txInput : tx.getTransactionInputs()) {
            Transaction prevTx = findTransaction(txInput.getTxId());
            prevTXs.put(Hex.toHexString(prevTx.getId()), prevTx);
        }
        tx.sign(privateKey, prevTXs);
    }

    // Mines a new block with the provided transactions
    public Block mineBlock(Transaction[] transactions) {
        for (Transaction tx : transactions) {
            if (!verifyTransaction(tx)) {
                System.out.println("ERROR: Invalid transaction");
                System.exit(1);
            }
        }
        DB blocksDb = BlocksDbSession.getLevelDbSession(System.getenv("NODE_ID")).getLevelDb();
        byte[] lastHash;
        lastHash = blocksDb.get(ByteUtils.stringToBytes("l"));
        byte[] blockData = blocksDb.get(lastHash);
        Block block = SerializationUtils.deserialize(blockData);
        int lastHeight = block.getHeight();
        Block newBlock = new Block(Arrays.asList(transactions), lastHash, lastHeight+1);
        blocksDb.put(newBlock.getHash(), newBlock.serialize());
        blocksDb.put(ByteUtils.stringToBytes("l"), newBlock.getHash());

        try {
            blocksDb.close();
        } catch (IOException ioe) {
            throw new BlockchainException(ioe.getMessage());
        }

        return newBlock;
    }



    // Finds a transaction by its ID
    private Transaction findTransaction(byte[] id) {

        BlockchainIterator bci = iterator();
        while (bci.hasNext()) {
            Block block = bci.next();
            for (Transaction tx : block.getTransactions()) {
                if (Arrays.equals(tx.getId(), id)) {
                    bci.close();
                    return tx;
                }
            }
        }
        bci.close();
        return null;
    }

    private boolean verifyTransaction(Transaction tx) {
        if (tx.isCoinBase()) return true;
        Map<String, Transaction> prevTxMap = new HashMap<>();
        for (TransactionInput txInput : tx.getTransactionInputs()) {
            Transaction prevTx = findTransaction(txInput.getTxId());
            prevTxMap.put(Hex.toHexString(prevTx.getId()), prevTx);
        }
        return tx.verify(prevTxMap);
    }

    // Finds all unspent transaction outputs and returns transactions with spent outputs removed
    public Map<String, List<TransactionOutput>> findUTXO() {
        Map<String, List<TransactionOutput>> utxoMap = new HashMap<>();
        Map<String,List<Integer>> spentTXOs = new HashMap<>();
        BlockchainIterator bci = iterator();
        while (bci.hasNext()) {
            Block block = bci.next();
            for (Transaction tx : block.getTransactions()) {
                String txId = Hex.toHexString(tx.getId());
                Outputs:
                for (int i=0; i<tx.getTransactionOutputs().length; i++) {
                    TransactionOutput txOutput = tx.getTransactionOutputs()[i];
                    // Was the output spent?
                    if (spentTXOs.get(txId) != null) {
                        for (int spentOutIdx : spentTXOs.get(txId)) {
                            if (spentOutIdx == i) {
                                break Outputs;
                            }
                        }
                    }
                    List<TransactionOutput> outs = utxoMap.get(txId);
                    if (outs == null) outs = new ArrayList<TransactionOutput>(); // TODO: check if TransactionOutput can be left out
                    outs.add(txOutput);
                    utxoMap.put(txId, outs);
                }
                if (!tx.isCoinBase()) {
                    for (int i=0; i < tx.getTransactionInputs().length; i++) {
                        TransactionInput txInput = tx.getTransactionInputs()[i];
                        String inTxId = Hex.toHexString(txInput.getTxId());
                        List<Integer> inTxIds = spentTXOs.get(inTxId);
                        if (inTxIds == null) inTxIds = new ArrayList<Integer>(); // TODO: check if Integer can be left out
                        inTxIds.add(txInput.getvOut());
                        spentTXOs.put(inTxId, inTxIds);
                    }
                }
            }
            if (block.getPrevBlockHash().length == 0) {
                break;
            }
        }
        return utxoMap;
    }

    // Returns a BlockchainIterator
    private BlockchainIterator iterator() {
        return new BlockchainIterator(tip);
    }

}
