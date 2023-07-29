package me13.core.queue;

import arc.func.Cons;
import arc.util.Log;
import org.jetbrains.annotations.Contract;

public class Queue<T> {
    public final T value;

    @Contract(pure = true)
    public Queue(T value) {
        this.value = value;
    }

    public void process(Cons<T> handler) {
        process(handler, Log::err);
    }

    public void process(Cons<T> handler, Cons<Throwable> fail) {
        try {
            handler.get(value);
        } catch(Throwable throwable) {
            fail.get(throwable);
        }
    }
}