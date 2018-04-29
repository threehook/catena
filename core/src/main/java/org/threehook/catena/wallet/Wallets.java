package org.threehook.catena.wallet;

import com.google.common.collect.Lists;
import org.threehook.catena.BlockchainException;
import org.threehook.catena.proto.CatenaProto;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Wallets implements Serializable{

    private final static String walletFile = "D:/tmp/simple-blockchain/wallet_%s.dat";

    private Map<String, Wallet> wallets = new HashMap<>();

    public Wallets(String nodeId) throws BlockchainException{
        try {
            loadFromFile(nodeId);
        } catch (Exception ioe) {
            throw new BlockchainException(ioe.getMessage());
        }
    }

    public Wallets(Map<String, Wallet> wallets) {
        this.wallets = wallets;
    }

    public String createWallet() {
        KeyPair keyPair;
        try {
            keyPair = createKeyPair();
        } catch (Exception e) {
            throw new BlockchainException(e.getMessage());
        }
        Wallet wallet = new Wallet(keyPair.getPrivate().getEncoded(), keyPair.getPublic().getEncoded());
        String address = String.format("%s", wallet.getAddress());
        wallets.put(address, wallet);
        return address;
    }

    // GetAddresses returns an array of addresses stored in the wallet file
    public List<String> getAddresses() {
        return Lists.newArrayList(wallets.keySet());
    }

    // GetWallet returns a Wallet by its address
    public Wallet getWallet(String address) {
        return wallets.get(address);
    }

    private KeyPair createKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecsp = new ECGenParameterSpec("secp256r1");
        keyGen.initialize(ecsp);
        return keyGen.generateKeyPair();
    }

//    func newKeyPair() (ecdsa.PrivateKey, []byte) {
//        curve := elliptic.P256()
//        private, err := ecdsa.GenerateKey(curve, rand.Reader)
//        if err != nil {
//            log.Panic(err)
//        }
//        pubKey := append(private.PublicKey.X.Bytes(), private.PublicKey.Y.Bytes()...)
//
//        return *private, pubKey
//    }

    private void loadFromFile(String nodeId) throws Exception {
        String walletFilePath = String.format(walletFile, nodeId);
        File walletFile = new File(walletFilePath);
        if (!walletFile.exists()) {
            walletFile.createNewFile();
        }
        Path path = Paths.get(walletFilePath);
//        byte[] fileContent = Files.readAllBytes(path);

        CatenaProto.Wallets protoWallets = CatenaProto.Wallets.newBuilder().mergeFrom(Files.newInputStream(path)).build();
        this.wallets = WalletMapper.reverseMap(protoWallets).wallets;
    }

    // SaveToFile saves wallets to a file
    public void saveToFile(String nodeId) {
        try {
            String walletFilePath = String.format(walletFile, nodeId);
//            byte[] content = SerializationUtils.serialize(this);
            byte[] content = WalletMapper.map(this).toByteArray();
            Path path = Paths.get(walletFilePath);
            Files.write(path, content);
        } catch (Exception ioe) {
            throw new BlockchainException(ioe.getMessage());
        }
    }

}
