package org.threehook.catena.core.wallet;

import com.google.common.primitives.Bytes;
import org.threehook.catena.core.hash.HashUtils;
import org.threehook.catena.core.util.Base58;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;

import java.io.Serializable;
import java.util.Arrays;

public class Wallet implements Serializable {

    private final static byte version = 0x00;
    private final static int addressChecksumLen = 4;

    private byte[] privateKey;
    private byte[] publicKey;

    public Wallet(byte[] privateKey, byte[] publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getAddress() {
        byte[] pubKeyHash = hashPublicKey(publicKey);
        byte[] version = new byte[]{0x00};
        byte[] versionedPayload =  Bytes.concat(version, pubKeyHash);
        byte[] checksum = checksum(versionedPayload);
        byte[] fullPayload = Bytes.concat(versionedPayload, checksum);
        return Base58.encode(fullPayload);
    }

    // ValidateAddress check if address if valid
    public static boolean validateAddress(String address) {
        byte[] pubKeyHash = Base58.decode(address);
        byte[] actualChecksum = Arrays.copyOfRange(pubKeyHash, pubKeyHash.length - addressChecksumLen, pubKeyHash.length);
        byte[] version = Arrays.copyOfRange(pubKeyHash, 0, 1);
        pubKeyHash = Arrays.copyOfRange(pubKeyHash, 1, pubKeyHash.length - addressChecksumLen);
        byte[] targetChecksum = checksum(Bytes.concat(version, pubKeyHash));
        return Arrays.equals(actualChecksum, targetChecksum);
    }

    // HashPubKey hashes public key
    public byte[] hashPublicKey(byte[] publicKey) {
        byte[] publicSHA256 = HashUtils.checksum256(publicKey);
        RIPEMD160Digest digester = new RIPEMD160Digest();
        byte[] retValue=new byte[digester.getDigestSize()];
        digester.update(publicSHA256, 0, publicSHA256.length);
        digester.doFinal(retValue, 0);
        return retValue;
    }

    // Checksum generates a checksum for a public key
    private static byte[] checksum(byte[] payload) {
        byte[] firstSHA = HashUtils.checksum256(payload);
        byte[] secondSHA = HashUtils.checksum256(firstSHA);
        return Arrays.copyOfRange(secondSHA, 0, addressChecksumLen);
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }
}
