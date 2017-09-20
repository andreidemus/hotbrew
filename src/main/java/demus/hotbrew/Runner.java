package demus.hotbrew;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static demus.hotbrew.utils.ArrayUtils.concat;
import static demus.hotbrew.utils.CryptUtils.sha1hex;
import static demus.hotbrew.utils.ExceptionUtils.wrapThrowable;
import static demus.hotbrew.utils.FileUtils.slurp;
import static demus.hotbrew.utils.FileUtils.spit;
import static demus.hotbrew.utils.IOUtils.pipe;
import static java.io.File.pathSeparator;
import static java.io.File.separator;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class Runner {
    public void run(String p, String[] args) throws IOException, InterruptedException {
        final String path = new File(p).getAbsolutePath();
        final String home = System.getProperty("user.home");
        final String script = slurp(path);
        final String sha1 = sha1hex(script);
        final String workDirPath = home + separator + ".hotbrew" + separator + "caches" + separator + sha1;
        final File workDir = new File(workDirPath);

        if (!workDir.exists()) {
            if (!workDir.mkdir())
                throw new IllegalStateException("Failed to create work dir");
            resolveDependencies(script, workDir);
        }
        final String cpFilePath = workDirPath + separator + "cp.txt";
        final String cpFomMaven = new File(cpFilePath).exists()
                ? slurp(cpFilePath)
                : "";
        final String cp = "\"." + pathSeparator + workDirPath + pathSeparator + cpFomMaven + "\"";

        final String scriptName = new File(path).getName().replace(".java", "");
        if (!new File(workDirPath + separator + scriptName + ".class").exists())
            runCmd(new String[]{"javac", path, "-d", workDirPath, "-cp", cp}, null, workDir);

        runCmd(concat(new String[]{"java", "-cp", cp, scriptName}, args), null, null);
    }

    public int runCmd(String[] cmd, String[] env, File dir) throws IOException, InterruptedException {
        final Runtime runtime = Runtime.getRuntime();
        final Process pr = runtime.exec(cmd, env, dir);
        pipe(pr.getInputStream(), System.out);
        pipe(pr.getErrorStream(), System.err);
        return pr.waitFor();
    }

    protected void resolveDependencies(String script, File workDir) {
        getHeader(script)
                .flatMap(this::getDependencies)
                .map(this::parseDependencies)
                .map(this::dependenciesToXml)
                .map(this::generatePomBody)
                .map(pom -> {
                    spit(workDir.getPath() + separator + "pom.xml", pom);
                    return workDir.getPath() + separator + "cp.txt";
                })
                .ifPresent(generateCpFileFn(workDir));
    }

    protected Consumer<String> generateCpFileFn(File workDir) {
        return cpFile -> wrapThrowable(() ->
                runCmd(new String[]{"mvn", "dependency:build-classpath", "-Dmdep.outputFile=cp.txt"}, null, workDir));
    }

    protected Optional<String> getHeader(String script) {
        final Pattern p = Pattern.compile("(?:/\\*\\*\\*[\\n\\r])([^/]*)\\*/", Pattern.MULTILINE);
        final Matcher m = p.matcher(script);

        if (!m.find())
            return Optional.empty();

        final String str = Arrays.stream(m.group(1).split("\n"))
                .map(String::trim)
                .map(line -> line.replaceFirst("^\\*", ""))
                .collect(Collectors.joining())
                .trim();

        return Optional.of(str);
    }

    protected Optional<String> getDependencies(String header) {
        final Pattern p = Pattern.compile("dependencies\\s=\\s\\[([^\\]]*)\\]");
        final Matcher m = p.matcher(header);

        if (!m.find())
            return Optional.empty();

        return Optional.of(m.group(1));
    }

    protected List<List<String>> parseDependencies(String dependencies) {
        final Pattern p = Pattern.compile("\"([^\"]*)\"");
        final Matcher m = p.matcher(dependencies);

        final List<List<String>> deps = new ArrayList<>();
        while (m.find())
            deps.add(asList(m.group(1).split(":")));

        return deps;
    }

    protected String dependenciesToXml(List<List<String>> dependencies) {
        return dependencies.stream()
                .map(this::dependencyToXml)
                .collect(Collectors.joining());
    }

    protected String dependencyToXml(List<String> dependency) {
        return format("        <dependency>\n" +
                        "            <groupId>%s</groupId>\n" +
                        "            <artifactId>%s</artifactId>\n" +
                        "            <version>%s</version>\n" +
                        "        </dependency>\n",
                dependency.get(0), dependency.get(1), dependency.get(2));
    }

    protected String generatePomBody(String dependenciesXml) {
        return format("<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0\n" +
                "                      http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "    <groupId>hotbrew</groupId>\n" +
                "    <artifactId>a-script</artifactId>\n" +
                "    <version>1.0.0</version>\n" +
                "    <packaging>pom</packaging>\n" +
                "    <dependencies>\n" +
                "%s" +
                "    </dependencies>\n" +
                "</project>", dependenciesXml);
    }
}
