package org.threehook.catena.networking.messaging.messages;

import java.util.Objects;

public class BestHeight {

    private int height;

    public BestHeight() {
    }

    public BestHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BestHeight that = (BestHeight) o;
        return height == that.height;
    }

    @Override
    public int hashCode() {

        return Objects.hash(height);
    }

    @Override
    public String toString() {
        return "BestHeight{" +
                "height=" + height +
                '}';
    }
}
