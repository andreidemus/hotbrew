package demus.hotbrew;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class RunnerTest {

    @Test
    public void testRun() throws Exception {
        URL script = this.getClass().getResource("/TestScript.java");

        Runner runner = new Runner();
        runner.run(script.getFile());
    }
}