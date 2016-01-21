package demus.hotbrew;

import org.junit.Test;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import static demus.hotbrew.utils.FileUtils.slurp;
import static java.util.Arrays.asList;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class RunnerTest {

    public static final String SCRIPT = "/***\n" +
            " * dependencies = [\"org.glassfish.jersey.core:jersey-client:2.22.1\",\n" +
            " * \"org.glassfish.jersey.core:jersey-core:2.22.1\"]\n" +
            " *\n" +
            " * somethingElse = [\"org.glassfish.jersey.core:jersey-client:2.23.1\",\n" +
            " * \"org.glassfish.jersey.core:jersey-core:2.22.2\"]\n" +
            " */\n" +
            "\n" +
            "public class TestScript {\n" +
            "    public static void main(String[] args) {\n" +
            "        System.out.println(\"It works!\");\n" +
            "    }\n" +
            "}\n";
    public static final String HEADER = "dependencies = [\"org.glassfish.jersey.core:jersey-client:2.22.1\"," +
            " \"org.glassfish.jersey.core:jersey-core:2.22.1\"]" +
            " somethingElse = [\"org.glassfish.jersey.core:jersey-client:2.23.1\"," +
            " \"org.glassfish.jersey.core:jersey-core:2.22.2\"]";
    public static final String DEPENDENCIES = "\"org.glassfish.jersey.core:jersey-client:2.22.1\", \"org.glassfish.jersey.core:jersey-core:2.22.1\"";
    public static final String DEPENDENCIES_XML = "        <dependency>\n" +
            "            <groupId>org.glassfish.jersey.core</groupId>\n" +
            "            <artifactId>jersey-client</artifactId>\n" +
            "            <version>2.22.1</version>\n" +
            "        </dependency>\n";

    @Test
    public void testRun() throws Exception {
        URL script = this.getClass().getResource("/TestScript.java");
        Runner r = new Runner();
        r.run(script.getPath(), new String[]{"a", "b", "12"});
    }

    @Test
    public void testGetHeader() throws Exception {
        Runner r = new Runner();
        Optional<String> header = r.getHeader(SCRIPT);

        assertThat(header.isPresent(), is(true));
        assertThat(header.get(), is(HEADER));
    }

    @Test
    public void testGetDependencies() throws Exception {
        Runner r = new Runner();
        Optional<String> deps = r.getDependencies(HEADER);

        assertThat(deps.isPresent(), is(true));
        assertThat(deps.get(), is(DEPENDENCIES));
    }

    @Test
    public void testParseDependencies() throws Exception {
        Runner r = new Runner();
        List<List<String>> deps = r.parseDependencies(DEPENDENCIES);

        assertThat(deps.size(), is(2));
        assertThat(deps.get(0), contains("org.glassfish.jersey.core", "jersey-client", "2.22.1"));
        assertThat(deps.get(1), contains("org.glassfish.jersey.core", "jersey-core", "2.22.1"));
    }

    @Test
    public void testDependencyToXml() throws Exception {
        Runner r = new Runner();
        List<String> dependency = asList("org.glassfish.jersey.core", "jersey-client", "2.22.1");
        String expectedXml = DEPENDENCIES_XML;

        String xml = r.dependencyToXml(dependency);
        assertThat(xml, is(expectedXml));
    }

    @Test
    public void testGeneratePomBody() throws Exception {
        Runner r = new Runner();
        String pom = r.generatePomBody(DEPENDENCIES_XML);
        String expectedPom = slurp(this.getClass().getResource("/expected_pom.xml").getPath());
        assertThat(pom, is(expectedPom));
    }
}