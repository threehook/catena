package org.threehook.catena;

import java.util.ArrayList;
import java.util.List;

public class MerkleTree {

    MerkleNode rootNode;

    // Creates a new Merkle tree from a sequence of data
    public MerkleTree(List<byte[]> data) {
        List<MerkleNode> merkleNodes = new ArrayList<>();
        if (data.size()%2 != 0) {
            data.add(data.get(data.size()-1));
        }
        for (byte[] datum : data) {
            MerkleNode node = new MerkleNode(null, null, datum);
            merkleNodes.add(node);
        }
        for (int i=0; i<data.size()/2; i++) {
            List<MerkleNode> newLevel = new ArrayList<>();
            for (int j=0; j<merkleNodes.size(); j+=2) {
                MerkleNode node = new MerkleNode(merkleNodes.get(j), merkleNodes.get(j+1), null);
                newLevel.add(node);
            }
            merkleNodes = newLevel;
        }
        rootNode = merkleNodes.get(0);
    }

    public MerkleNode getRootNode() {
        return rootNode;
    }
}
