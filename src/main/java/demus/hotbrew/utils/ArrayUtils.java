package demus.hotbrew.utils;

import java.util.Arrays;
import java.util.stream.Stream;

public class ArrayUtils {
    @SuppressWarnings("unchecked")
    public static String[] concat(String[] ... arrs) {
        return Stream.of(arrs)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }
}
