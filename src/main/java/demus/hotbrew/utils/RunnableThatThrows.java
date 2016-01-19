package demus.hotbrew.utils;

@FunctionalInterface
public interface RunnableThatThrows {
    void run() throws Throwable;
}
