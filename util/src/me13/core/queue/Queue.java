package me13.core.queue;

import arc.util.Log;
import org.jetbrains.annotations.Contract;
import java.util.function.Consumer;

public class Queue<T> {
    public final T value;

    @Contract(pure = true)
    public Queue(T value) {
        this.value = value;
    }

    public void process(Consumer<T> handler) {
        process(handler, Log::err);
    }

    public void process(Consumer<T> handler, Consumer<Throwable> fail) {
        try {
            handler.accept(value);
        } catch(Throwable throwable) {
            fail.accept(throwable);
        }
    }
}