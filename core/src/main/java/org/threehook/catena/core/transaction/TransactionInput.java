package org.threehook.catena.core.transaction;

import java.io.Serializable;

public class TransactionInput implements Serializable { // Serializable not needed when using protobuf

    private byte[] txId;
    private int vOut;
    private byte[] signature;
    private byte[] pubKey;

    public TransactionInput(byte[] txId, int vOut, byte[] signature, byte[] pubKey) {
        this.txId = txId;
        this.vOut = vOut;
        this.signature = signature;
        this.pubKey = pubKey;
    }

    public byte[] getTxId() {
        return txId;
    }

    public int getvOut() {
        return vOut;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getPubKey() {
        return pubKey;
    }

    public void setPubKey(byte[] pubKey) {
        this.pubKey = pubKey;
    }
}
