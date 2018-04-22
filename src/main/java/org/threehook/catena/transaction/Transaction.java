package org.threehook.catena.transaction;

import org.threehook.catena.BlockchainException;
import org.threehook.catena.hash.HashUtils;
import org.threehook.catena.util.ByteUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.bouncycastle.util.encoders.Hex;
import sun.security.ec.ECPublicKeyImpl;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Transaction implements Serializable {

    private byte[] id;
    private TransactionInput[] transactionInputs;
    private TransactionOutput[] transactionOutputs;

    public Transaction(byte[] id, TransactionInput[] vIn, TransactionOutput[] vOut) {
        this.id = id;
        this.transactionInputs = vIn;
        this.transactionOutputs = vOut;
    }

    // Returns a serialized Transaction (for calculating the hash only, deserialize is not necessary)
    public byte[] serialize() {
//        TransactionMapper.map(this).toByteArray(); // To map to proto3
        return SerializationUtils.serialize(this);
    }

    // Signs each input of a Transaction
    public void sign(ECPrivateKey privateKey, Map<String, Transaction> prevTxs) {
        if (this.isCoinBase()) {
            return;
        }
        for (TransactionInput txInput : transactionInputs) {
            if (prevTxs.get(Hex.toHexString(txInput.getTxId())).getId() == null ) {
                System.out.println("ERROR: Previous transaction is not correct");
                System.exit(1);
            }
        }
        Transaction txCopy = trimmedCopy();

        TransactionInput[] txInputs = txCopy.getTransactionInputs();
        for (int i=0; i<txInputs.length; i++) {
            Transaction prevTx = prevTxs.get(Hex.toHexString(txInputs[i].getTxId()));
            TransactionInput txInput = txCopy.getTransactionInputs()[i];
            txInput.setSignature(null);
            txInput.setPubKey(prevTx.getTransactionOutputs()[txInput.getvOut()].getPubKeyHash());
            String dataToSign = Hex.toHexString(txCopy.serialize());

            byte[] signature;
            try {
                Signature dsa = Signature.getInstance("ECDSA", "BC");
                dsa.initSign(privateKey);
                dsa.update(ByteUtils.stringToBytes(dataToSign));
                signature = dsa.sign();
            } catch (GeneralSecurityException e) {
                throw new BlockchainException(e.getMessage());
            }
            transactionInputs[i].setSignature(signature);
            txCopy.getTransactionInputs()[i].setPubKey(null);
        }
    }

    // Returns the hash of the Transaction
    public byte[] hash() {
        setId(new byte[]{});
        return HashUtils.checksum256(this.serialize());
    }

    // Checks whether the transaction is coinbase
    public boolean isCoinBase() {
        return transactionInputs.length == 1 && transactionInputs[0].getTxId().length == 0 && transactionInputs[0].getvOut() == -1;
    }

    // Creates a trimmed copy of Transaction to be used in signing
    private Transaction trimmedCopy() {
        List<TransactionInput> txInputs = new ArrayList<>();
        List<TransactionOutput> txOutputs = new ArrayList<>();
        for (TransactionInput txInput : transactionInputs) {
            txInputs.add(new TransactionInput(txInput.getTxId(), txInput.getvOut(), null, null ));
        }
        for (TransactionOutput txOutput : transactionOutputs) {
            txOutputs.add(new TransactionOutput(txOutput.getValue(), txOutput.getPubKeyHash()));
        }
        return new Transaction(id, txInputs.stream().toArray(TransactionInput[]::new), txOutputs.stream().toArray(TransactionOutput[]::new));
    }

    // Verifies signatures of Transaction inputs
    public boolean verify(Map<String, Transaction> prevTxMap) {
        if (isCoinBase()) return true;
        for (TransactionInput txInput : transactionInputs) {
            if (prevTxMap.get(Hex.toHexString(txInput.getTxId())).getId() == null) {
                System.out.println("ERROR: Previous transaction is not correct");
                System.exit(1);
            }
        }
        Signature ecdsaVerify;
        try {
            ecdsaVerify = Signature.getInstance("SHA256withECDSA", "BC");
            Transaction txCopy = trimmedCopy();
            ECGenParameterSpec ecsp = new ECGenParameterSpec("secp256r1");
            for (int i=0; i<transactionInputs.length; i++) {
                TransactionInput txInput = transactionInputs[i];
                Transaction prevTx = prevTxMap.get(Hex.toHexString(txInput.getTxId()));
                txCopy.transactionInputs[i].setSignature(null);
                txCopy.transactionInputs[i].setPubKey(prevTx.getTransactionOutputs()[txInput.getvOut()].getPubKeyHash());

//                String dataToVerify = Hex.toHexString(txCopy.serialize());
                byte[] dataToVerify = txCopy.serialize();
                ecdsaVerify.initVerify(new ECPublicKeyImpl(txInput.getPubKey()));
//                ecdsaVerify.update(dataToVerify.getBytes("UTF-8"));
                ecdsaVerify.update(dataToVerify);
                if (ecdsaVerify.verify(txInput.getSignature())) return false;
            }
        } catch (Exception e) {
            throw new BlockchainException(e.getMessage());
        }
        return true;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public TransactionInput[] getTransactionInputs() {
        return transactionInputs;
    }

    public TransactionOutput[] getTransactionOutputs() {
        return transactionOutputs;
    }

}
