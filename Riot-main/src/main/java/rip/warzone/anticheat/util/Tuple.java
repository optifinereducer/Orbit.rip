package rip.warzone.anticheat.util;

public class Tuple<A, B> {
    private final A a;
    private final B b;

    public Tuple(A var1, B var2) {
        this.a=var1;
        this.b=var2;
    }

    public A a() {
        return this.a;
    }

    public B b() {
        return this.b;
    }
}

