package org.threehook.catena.transaction;

import org.threehook.catena.BlockchainException;
import org.threehook.catena.UTXOSet;
import org.threehook.catena.util.ByteUtils;
import org.threehook.catena.wallet.Wallet;
import org.apache.commons.lang3.RandomUtils;
import org.bouncycastle.util.encoders.Hex;
import sun.security.ec.ECPrivateKeyImpl;

import java.security.InvalidKeyException;
import java.security.interfaces.ECPrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionFactory {

    private final static int SUBSIDY = 10;

    // Creates a new coinbase transaction
    public static Transaction createCoinBaseTransaction(String to, String data) {
        if (data == null || data.equals("")) {
            byte[] randData = RandomUtils.nextBytes(20);
            data = Hex.toHexString(randData);
        }
        TransactionInput[] txin = {new TransactionInput(new byte[]{}, -1, new byte[]{}, ByteUtils.stringToBytes(data))};
        TransactionOutput[] txout = {new TransactionOutput(SUBSIDY, to)};
        Transaction tx = new Transaction(null,  txin, txout);
        tx.setId(tx.hash());
        return tx;
    }

    // Creates a new transaction
    public static Transaction createUTXOTransaction(Wallet wallet, String to, int amount, UTXOSet utxoSet) {
        List<TransactionInput> txInputs = new ArrayList();
        List<TransactionOutput> txOutputs = new ArrayList();
        byte[] pubKeyHash = wallet.hashPublicKey(wallet.getPublicKey());

        Map<String, Integer[]> validOutputs = new HashMap();
        int accumulated = utxoSet.findSpendableOutputs(pubKeyHash, amount, validOutputs);
        if (accumulated < amount) {
            System.out.println("ERROR: Not enough funds");
            System.exit(1);
        }

        // Build a list of inputs
        for (Map.Entry<String, Integer[]> entry : validOutputs.entrySet()) {
            byte[] txId = Hex.decode(entry.getKey());
            for (int out : entry.getValue()) {
                TransactionInput txInput = new TransactionInput(txId, out, null, wallet.getPublicKey());
                txInputs.add(txInput);
            }
        }

        // Build a list of outputs
        String from = String.format("%s", wallet.getAddress());
        txOutputs.add(new TransactionOutput(amount, to));
        if (accumulated > amount) {
            txOutputs.add(new TransactionOutput(accumulated-amount, from));
        }

        Transaction tx = new Transaction( null, txInputs.stream().toArray(TransactionInput[]::new), txOutputs.stream().toArray(TransactionOutput[]::new));
        tx.setId(tx.hash());

        ECPrivateKey privateKey;
        try {
            privateKey = new ECPrivateKeyImpl(wallet.getPrivateKey());
        } catch (InvalidKeyException ike) {
            throw new BlockchainException(ike.getMessage());
        }
        utxoSet.getBlockchain().signTransaction(tx, privateKey);

        return tx;
    }


}
