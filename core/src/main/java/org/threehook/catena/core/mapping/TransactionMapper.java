package org.threehook.catena.core.mapping;

import com.google.protobuf.ByteString;
import org.threehook.catena.proto.CatenaProto;
import org.threehook.catena.core.transaction.Transaction;
import org.threehook.catena.core.transaction.TransactionInput;
import org.threehook.catena.core.transaction.TransactionOutput;

public class TransactionMapper {

    public static CatenaProto.Transaction map(Transaction transaction) {
        CatenaProto.Transaction.Builder transactionBuilder = CatenaProto.Transaction.newBuilder();
        CatenaProto.TransactionInput.Builder transactionInputBuilder = CatenaProto.TransactionInput.newBuilder();
        CatenaProto.TransactionOutput.Builder transactionOutputBuilder = CatenaProto.TransactionOutput.newBuilder();

        transactionBuilder.setId(ByteString.copyFrom(transaction.getId()));
        TransactionInput[] txInput = transaction.getTransactionInputs();
        for (int i=0; i < txInput.length; i++ ) {
            transactionInputBuilder.setTxId(ByteString.copyFrom(txInput[i].getTxId()));
            transactionInputBuilder.setVOut(txInput[i].getvOut());
            if (txInput[i].getSignature()!=null) {
                transactionInputBuilder.setSignature(ByteString.copyFrom(txInput[i].getSignature()));
            }
            transactionInputBuilder.setPubKey(ByteString.copyFrom(txInput[i].getPubKey()));
            transactionBuilder.addTransactionInputs(transactionInputBuilder);
        }

        TransactionOutput[] txOutput = transaction.getTransactionOutputs();
        for (int i=0; i < txOutput.length; i++ ) {
            transactionOutputBuilder.setValue(txOutput[i].getValue());
            transactionOutputBuilder.setPubKeyHash(ByteString.copyFrom(txOutput[i].getPubKeyHash()));
            transactionBuilder.addTransactionOutputs(transactionOutputBuilder);
        }

        return transactionBuilder.build();
    }

    public static Transaction reverseMap(CatenaProto transactionProto) {
        return null;
    }
}
