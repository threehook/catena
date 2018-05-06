package org.threehook.catena.core.hash;

public class HashException extends RuntimeException {

    public HashException() {
        super();
    }

    public HashException(String s) {
        super(s);
    }

    public HashException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public HashException(Throwable throwable) {
        super(throwable);
    }

    protected HashException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
