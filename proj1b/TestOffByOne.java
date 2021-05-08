import org.junit.Test;
import static org.junit.Assert.*;

public class TestOffByOne {
    /*
    // You must use this CharacterComparator and not instantiate
    // new ones, or the autograder might be upset.
    static CharacterComparator offByOne = new OffByOne();

    // Your tests go here.
    Uncomment this class once you've created your CharacterComparator interface and OffByOne class. **/

    static OffByOne obo = new OffByOne();

    @Test
    public void TestOffByOne () {
        char x1 = 'a';
        char y1 = 'b';
        assertTrue (obo.equalChars(x1, y1));

        char x2 = 's';
        char y2 = 'j';
        assertFalse (obo.equalChars(x2, y2));
    }
}
