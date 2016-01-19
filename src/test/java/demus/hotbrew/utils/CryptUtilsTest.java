package demus.hotbrew.utils;

import org.junit.Test;

import static demus.hotbrew.utils.CryptUtils.sha1hex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CryptUtilsTest {

    @Test
    public void testSha1() throws Exception {
        assertThat(sha1hex("some text"), is("37aa63c77398d954473262e1a0057c1e632eda77"));
    }
}