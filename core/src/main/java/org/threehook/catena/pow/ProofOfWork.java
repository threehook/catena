package org.threehook.catena.pow;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import org.threehook.catena.block.Block;
import org.threehook.catena.hash.HashUtils;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class ProofOfWork {

    private static final Logger logger = LoggerFactory.getLogger(ProofOfWork.class);
    private final static long MAX_NONCE = Long.MAX_VALUE; // math.MaxInt64;
    private final static int TARGET_BITS = 16;

    Block block;
    BigInteger target = BigInteger.ONE;

    public ProofOfWork(Block block) {
        this.block = block;
        target = target.shiftLeft(256-TARGET_BITS);
        System.out.println(target);
    }

    public HashWithNonce run() {
        BigInteger hashInt;
        byte[] hash = new byte[32];
        long nonce = 0L;

        logger.info("Mining a new block");
        System.out.println("Processing:");
        while (nonce < MAX_NONCE) {
            byte[] data = prepareData(nonce);
            hash = HashUtils.checksum256(data);
            if (nonce%1000 == 0) {
                System.out.printf("\r%s", Hex.toHexString(hash));
            }
            hashInt = new BigInteger(1, hash).abs();

            if (hashInt.compareTo(target) == -1) {
                break;
            } else {
                nonce++;
            }
        }
        System.out.println("\n\n");

        return new HashWithNonce(hash, nonce);
    }

    public byte[] prepareData(long nonce) {
        return Bytes.concat(
                block.getPrevBlockHash(),
                block.hashTransactions(),
                Longs.toByteArray(block.getTimestamp()),
                Longs.toByteArray(new Long(TARGET_BITS)),
                Longs.toByteArray(nonce)
        );
    }

    public class HashWithNonce {
        public byte[] hash;
        public long nonce;

        public HashWithNonce(byte[] hash, long nonce) {
            this.hash = hash;
            this.nonce = nonce;
        }
    }

    // Validates block's PoW
    public boolean validate() {
        byte[] data = prepareData(block.getNonce());
        byte[] hash = HashUtils.checksum256(data);
        BigInteger hashInt = new BigInteger(hash);
        return hashInt.compareTo(target) == -1;
    }

}
