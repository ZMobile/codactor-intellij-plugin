package com.translator.service.codactor.test;

public class Pair<T, U> {
    private final T first;
    private final U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return this.first;
    }

    public U getSecond() {
        return this.second;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Pair pair = (Pair)o;
            if (this.first != null) {
                if (!this.first.equals(pair.first)) {
                    return false;
                }
            } else if (pair.first != null) {
                return false;
            }

            if (this.second != null) {
                if (this.second.equals(pair.second)) {
                    return true;
                }
            } else if (pair.second == null) {
                return true;
            }

            return false;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.first != null ? this.first.hashCode() : 0;
        result = 31 * result + (this.second != null ? this.second.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Pair(" + this.first + "," + this.second + ")";
    }
}
