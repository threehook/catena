package org.threehook.catena.wallet;

import com.google.protobuf.ByteString;
import org.threehook.catena.proto.CatenaProto;

import java.util.HashMap;
import java.util.Map;

public class WalletMapper {

    public static CatenaProto.Wallets map(Wallets wallets) {
        Map<String, CatenaProto.Wallet> protoWalletMap = new HashMap<>();
        for (String address : wallets.getAddresses()) {
            Wallet wallet = wallets.getWallet(address);
            CatenaProto.Wallet protoWallet = map2ProtoWallet(wallet);
            protoWalletMap.put(address, protoWallet);
        }
        CatenaProto.Wallets.Builder walletsBuilder = CatenaProto.Wallets.newBuilder();
        return walletsBuilder.putAllWallets(protoWalletMap).build();
    }

    private static CatenaProto.Wallet map2ProtoWallet(Wallet wallet) {

        CatenaProto.Wallet.Builder walletBuilder = CatenaProto.Wallet.newBuilder();
        ByteString privateKey = ByteString.copyFrom(wallet.getPrivateKey());
        ByteString publicKey = ByteString.copyFrom(wallet.getPublicKey());
        return walletBuilder.setPrivateKey(privateKey).setPublicKey(publicKey).build();
    }



    public static Wallets reverseMap(CatenaProto.Wallets protoWallets) {
        Map<String, Wallet> walletMap = new HashMap<>();
        Map<String, CatenaProto.Wallet> protoWalletMap = protoWallets.getWalletsMap();
        for (String address : protoWalletMap.keySet()) {
            Wallet wallet = map2Wallet(protoWalletMap.get(address));
            walletMap.put(address, wallet);
        }
        return new Wallets(walletMap);
    }

    private static Wallet map2Wallet(CatenaProto.Wallet protoWallet) {
        return new Wallet(protoWallet.getPrivateKey().toByteArray(), protoWallet.getPublicKey().toByteArray());
    }

}

