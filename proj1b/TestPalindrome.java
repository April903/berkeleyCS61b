import org.junit.Test;
import static org.junit.Assert.*;

public class TestPalindrome {
    // You must use this palindrome, and not instantiate
    // new Palindromes, or the autograder might be upset.
    static Palindrome palindrome = new Palindrome();
    static OffByOne obo = new OffByOne();

    @Test
    public void testWordToDeque() {
        Deque d = palindrome.wordToDeque("persiflage");
        String actual = "";
        for (int i = 0; i < "persiflage".length(); i++) {
            actual += d.removeFirst();
        }
        assertEquals("persiflage", actual);
    }

    @Test
    public void testIsPalindrome1 () {
        String s1 = "a";
        assertTrue(palindrome.isPalindrome(s1));

        String s2 = "racecar";
        assertTrue(palindrome.isPalindrome(s2));

        String s3 = "horse";
        assertFalse(palindrome.isPalindrome(s3));

        String s4 = "rancor";
        assertFalse(palindrome.isPalindrome(s4));
    }

    @Test
    public void testIsPalindrome2 () {
        String s1 = "flake";
        assertTrue(palindrome.isPalindrome(s1, obo));

        String s2 = "aeyxzdb";
        assertTrue(palindrome.isPalindrome(s2, obo));

        String s3 = "abwslkdng";
        assertFalse(palindrome.isPalindrome(s3, obo));
    }

}
