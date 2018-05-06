package org.threehook.catena.core.wallet;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.threehook.catena.core.BlockchainException;
import org.threehook.catena.proto.CatenaProto;

import javax.annotation.PostConstruct;
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

@Component
public class Wallets implements Serializable {

    @Value("${node.id}")
    private String nodeId;

    @Value("${wallet.path}")
    private String walletPath;

//    private final static String walletPath = "D:/tmp/simple-blockchain/wallet_%s.dat";

    private Map<String, Wallet> wallets = new HashMap<>();

//    public Wallets() throws BlockchainException{
//        try {
//            loadFromFile();
//        } catch (Exception ioe) {
//            throw new BlockchainException(ioe.getMessage());
//        }
//    }

    @PostConstruct
    public void init() {
        try {
            loadFromFile();
        } catch (Exception ioe) {
            throw new BlockchainException(ioe.getMessage());
        }
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

    private void loadFromFile() throws Exception {
//        String walletFilePath = String.format(walletPath, nodeId);
        File walletFile = new File(walletPath);
        if (!walletFile.exists()) {
            walletFile.createNewFile();
        }
        Path path = Paths.get(walletPath);

        CatenaProto.Wallets protoWallets = CatenaProto.Wallets.newBuilder().mergeFrom(Files.newInputStream(path)).build();
        this.wallets = WalletMapper.reverseMap(protoWallets);
    }

    // SaveToFile saves wallets to a file
    public void saveToFile() {
        try {
            byte[] content = WalletMapper.map(this).toByteArray();
            Path path = Paths.get(walletPath);
            Files.write(path, content);
        } catch (Exception ioe) {
            throw new BlockchainException(ioe.getMessage());
        }
    }

}
