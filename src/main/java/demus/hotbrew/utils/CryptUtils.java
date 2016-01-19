package demus.hotbrew.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static demus.hotbrew.utils.ExceptionUtils.wrapThrowable;
import static java.lang.String.format;

public class CryptUtils {
    public static String sha1hex(String text) {
        return sha1hex(text, StandardCharsets.UTF_8);
    }

    public static String sha1hex(String text, Charset charset) {
        return wrapThrowable(() -> {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(text.getBytes(charset));
            byte[] result = md.digest();

            String str = "";
            for (byte b : result) // wtf java does not have Arrays.stream for byte array ?!
                str += format("%02x", b & 0xFF);

            return str;
        });
    }
}
