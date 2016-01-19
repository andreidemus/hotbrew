package demus.hotbrew.utils;

@FunctionalInterface
public interface SupplierThatThrows<T> {
    T get() throws Exception;
}
