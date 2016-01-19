package demus.hotbrew.utils;

public class ExceptionUtils {
    public static <T> T wrapThrowable(SupplierThatThrows<T> s) {
        try {
            return s.get();
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static void wrapThrowable(RunnableThatThrows r) {
        try {
            r.run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
