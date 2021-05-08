import org.junit.Test;
import static org.junit.Assert.*;

public class TestOffByN {

    static OffByN obo5 = new OffByN(5);
    static OffByN obo3 = new OffByN(3);
    static OffByN obo10 = new OffByN(10);

    @Test
    public void testOffByN () {
        char x1 = 'c';
        char y1 = 'm';
        assertTrue(obo10.equalChars(x1, y1));

        char x2 = 'f';
        char y2 = 'k';
        assertTrue(obo5.equalChars(x2, y2));

        char x3 = 'x';
        char y3 = 'v';
        assertFalse(obo3.equalChars(x3, y3));
    }
}
