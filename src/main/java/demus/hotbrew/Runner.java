package demus.hotbrew;

import java.io.*;

import static demus.hotbrew.utils.CryptUtils.sha1hex;
import static demus.hotbrew.utils.FileUtils.slurp;
import static java.io.File.separator;

public class Runner {
    public void run(String path) throws IOException, InterruptedException {
        final String home = System.getProperty("user.home");
        final String script = slurp(path);
        final String sha1 = sha1hex(script);

        final String workDirPath = home + separator + ".hotbrew" + separator + "caches" + separator + sha1;
        final File workDir = new File(workDirPath);
        if (!workDir.exists() && !workDir.mkdir())
            throw new IllegalStateException("Failed to create work dir");

        final String scriptName = new File(path).getName().replace(".java", "");
        runCmd(new String[] {"javac", path, "-d", workDirPath}, null, workDir);
        runCmd(new String[] {"java", scriptName}, null, workDir);
    }

    private int runCmd(String[] cmd, String[] env, File dir) throws IOException, InterruptedException {
        final Runtime runtime = Runtime.getRuntime();
        final Process pr = runtime.exec(cmd, env, dir);
        final int exitCode = pr.waitFor();
        if (exitCode == 0) {
            pipe(pr.getInputStream(), System.out);
        } else {
            pipe(pr.getErrorStream(), System.err);
        }
        return exitCode;
    }

    private void pipe(InputStream in, PrintStream out) throws IOException {
        final BufferedReader r = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = r.readLine()) != null) {
            out.println(line);
        }
    }
}
