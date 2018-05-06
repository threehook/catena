package org.threehook.catena.core;

public class BlockchainException extends RuntimeException {

    public BlockchainException() {
        super();
    }

    public BlockchainException(String s) {
        super(s);
    }

    public BlockchainException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public BlockchainException(Throwable throwable) {
        super(throwable);
    }

    protected BlockchainException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
