package demus.hotbrew.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import static demus.hotbrew.utils.ExceptionUtils.wrapThrowable;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;
import static java.nio.file.Paths.get;

public class FileUtils {
    public static String slurp(String path) {
        return slurp(path, StandardCharsets.UTF_8);
    }

    public static String slurp(String path, Charset charset) {
        return wrapThrowable(() ->
                new String(readAllBytes(get(path)), charset));
    }

    public static void spit(String path, String contents) {
        spit(path, contents, StandardOpenOption.WRITE);
    }

    public static void spit(String path, String contents, OpenOption... options) {
        wrapThrowable(() ->
                write(get(path), contents.getBytes()));
    }
}
