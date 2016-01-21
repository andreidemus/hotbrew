package demus.hotbrew.utils;

import java.io.*;

public class IOUtils {
    public static void pipe(InputStream in, PrintStream out) throws IOException {
        final BufferedReader r = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = r.readLine()) != null)
            out.println(line);
    }
}
