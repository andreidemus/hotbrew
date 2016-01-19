package demus.hotbrew.io;

import org.junit.Test;

import java.io.File;

import static demus.hotbrew.utils.FileUtils.slurp;
import static demus.hotbrew.utils.FileUtils.spit;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class FileUtilsTest {
    @Test
    public void simpleSpitSlurpTest() throws Exception {
        String test = "some text\n";
        File file = File.createTempFile("test", ".txt");
        String path = file.getPath();

        spit(path, test);
        String contents = slurp(path);
        file.delete();

        assertThat(contents, is(test));
    }
}