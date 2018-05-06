package org.threehook.catena.core.transaction;

import org.threehook.catena.core.util.Base58;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Arrays;

public class TransactionOutput implements Serializable {

    private int value;
    private byte[] pubKeyHash = null;

    public TransactionOutput(int value, String address) {
        this(value, (byte[]) null);
        lock(address);
    }

    public TransactionOutput(int value, byte[] pubKeyHash) {
        this.value = value;
        this.pubKeyHash = pubKeyHash;
    }

    public static byte[] serializeOutputs(TransactionOutput[] transactionOutputs) {
        return SerializationUtils.serialize(transactionOutputs);
    }

    public static TransactionOutput[] deserializeOutputs(byte[] data) {
        return SerializationUtils.deserialize(data);
    }

    // IsLockedWithKey checks if the output can be used by the owner of the pubkey
    public boolean isLockedWithKey(byte[] pubKeyHash) {
        return Arrays.equals(this.pubKeyHash, pubKeyHash);
    }

    private void lock(String address) {
        byte[] pubKeyHash = Base58.decode(address);
        this.pubKeyHash = Arrays.copyOfRange(pubKeyHash, 1, pubKeyHash.length-4);

    }


    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public byte[] getPubKeyHash() {
        return pubKeyHash;
    }

    public void setPubKeyHash(byte[] pubKeyHash) {
        this.pubKeyHash = pubKeyHash;
    }
}
