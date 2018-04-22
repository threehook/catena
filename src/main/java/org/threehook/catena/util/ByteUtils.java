package org.threehook.catena.util;

import org.threehook.catena.BlockchainException;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ByteUtils {

    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public static byte[] intToBytes(int i) {
        buffer.putInt(0, i);
        return buffer.array();
    }

    public static long bytesToInt(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getInt();
    }

    public static byte[] longToBytes(long l) {
        buffer.putLong(0, l);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public static byte[] stringToBytes(String s) {
        byte[] data = null;
        try {
            data = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException uce) {
            throw new BlockchainException(uce.getMessage());
        }
        return data;
    }
}
