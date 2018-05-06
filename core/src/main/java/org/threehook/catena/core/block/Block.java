package org.threehook.catena.core.block;

import org.threehook.catena.core.pow.ProofOfWork;
import org.threehook.catena.core.MerkleTree;
import org.threehook.catena.core.transaction.Transaction;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Block implements Serializable {

    private static final long serialVersionUID = -2304315894161663652L;

    private long timestamp;
    private List<Transaction> transactions;
    private byte[] prevBlockHash;
    private byte[] hash;
    private long nonce;
    private int height;

    // Creates and returns a Block
    public Block(List<Transaction> transactions, byte[] prevBlockHash, int height) {

        timestamp = System.currentTimeMillis();
        this.transactions = transactions;
        this.prevBlockHash = prevBlockHash;
        this.height = height;

        ProofOfWork pow = new ProofOfWork(this);
        ProofOfWork.HashWithNonce hashWithNonce = pow.run();
        hash = hashWithNonce.hash;
        nonce = hashWithNonce.nonce;
    }

    // Returns a hash of the transactions in the block
    public byte[] hashTransactions() {
       List<byte[]> transactions = new ArrayList<>();
        for (Transaction tx : this.transactions) {
            transactions.add(tx.serialize());
        }
        MerkleTree mTree = new MerkleTree(transactions);
        return mTree.getRootNode().getData();
    }

    // Serializes the block
    public byte[] serialize() {
        return SerializationUtils.serialize(this);
    }


    public long getTimestamp() {
        return timestamp;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public byte[] getPrevBlockHash() {
        return prevBlockHash;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public long getNonce() {
        return nonce;
    }

    public int getHeight() {
        return height;
    }

}
