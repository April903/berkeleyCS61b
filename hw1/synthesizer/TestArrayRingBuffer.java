package synthesizer;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests the ArrayRingBuffer class.
 *  @author Josh Hug
 */

public class TestArrayRingBuffer {
    @Test
    public void someTest() {
        ArrayRingBuffer<Integer> arb = new ArrayRingBuffer(10);
        assertTrue(arb.isEmpty());
        arb.enqueue(1);
        arb.enqueue(2);
        arb.enqueue(3);
        arb.enqueue(4);

        assertEquals((Object) arb.peek(), 1);
        assertEquals((Object) arb.dequeue(), 1);

        arb.enqueue(5);
        arb.enqueue(6);
        assertEquals(arb.fillCount(), 5);

        arb.enqueue(7);
        arb.enqueue(8);
        arb.enqueue(9);
        assertEquals((Object) arb.peek(), 2);
        arb.enqueue(10);
        arb.enqueue(11);
        assertTrue(arb.isFull());
    }

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 
