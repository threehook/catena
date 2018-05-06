package org.threehook.catena.core;

import com.google.common.primitives.Bytes;
import org.threehook.catena.core.hash.HashUtils;

public class MerkleNode {

    MerkleNode left;
    MerkleNode right;
    byte[] data;

    // Creates a new Merkle tree node
    public MerkleNode(MerkleNode left, MerkleNode right, byte[] data) {
        byte[] hash = null;
        if (left == null && right == null) {
            hash = HashUtils.checksum256(data);
            this.data = hash;
        } else {
            byte[] prevHashes = Bytes.concat(left.data, right.data);
            hash = HashUtils.checksum256(prevHashes);
            this.data = hash;
        }
        this.left = left;
        this.right = right;
    }

    public byte[] getData() {
        return data;
    }
}
