import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class TestTest {
    @Before
    public void setUp() {
        System.out.println("Unit test before activated");
    }

    @Test
    public void test() {
        System.out.println("Unit test activated");
    }
}
