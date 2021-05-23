import static org.junit.Assert.*;
import org.junit.Test;

public class TestArrayDequeGold {

    @Test
    public void testStudent () {

        StudentArrayDeque<Integer> sad = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ads = new ArrayDequeSolution<>();

        String message = "";

        for (int i = 0; i < 100; i++) {
            double randomNum = StdRandom.uniform();

            if (randomNum < 0.25) {
                Integer temp = StdRandom.uniform(100);
                sad.addLast(temp);
                ads.addLast(temp);
                message += "addLast(" + temp + ")\n";
            }

            else if (randomNum >= 0.25 && randomNum < 0.5) {

                Integer temp = StdRandom.uniform(100);
                sad.addFirst(temp);
                ads.addFirst(temp);
                message += "addFirst(" + temp + ")\n";
            }

            else if (randomNum >= 0.5 && randomNum < 0.75) {
                if (sad.isEmpty()) {
                    continue;
                }
                message += "removeFirst()\n";
                assertEquals (message, ads.removeFirst(), sad.removeFirst());
            }

            else {
                if (sad.isEmpty()) {
                    continue;
                }
                message += "removeLast()\n";
                assertEquals (message, ads.removeFirst(), sad.removeFirst());
            }
        }

        /* These are my references @ Lunaticf
        StudentArrayDeque<Integer> stdDeque = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> correctDeque = new ArrayDequeSolution<>();

        String message = "";

        // test 100 hundred times
        for (int i = 0; i < 100; i++) {
            int randomMethod = StdRandom.uniform(4);
            if (randomMethod == 0) {
                Integer temp = StdRandom.uniform(100);
                stdDeque.addFirst(temp);
                correctDeque.addFirst(temp);
                message += "addFirst(" + temp + ")\n";
            } else if (randomMethod == 1) {
                Integer temp = StdRandom.uniform(100);
                stdDeque.addLast(temp);
                correctDeque.addLast(temp);
                message += "addFirst(" + temp + ")\n";
            } else if (randomMethod == 2) {
                if (stdDeque.isEmpty()) {
                    continue;
                }
                message += "removeFirst()\n";
                assertEquals(message, correctDeque.removeFirst(), stdDeque.removeFirst());
            } else {
                if (stdDeque.isEmpty()) {
                    continue;
                }
                message += "removeLast()\n";
                assertEquals(message, correctDeque.removeLast(), stdDeque.removeLast());

            }

        }

        */
    }
}
