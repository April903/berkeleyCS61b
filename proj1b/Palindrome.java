public class Palindrome {

    public Deque<Character> wordToDeque (String word) {
        Deque<Character> d = new ArrayDeque<>();

        for (int i = 0; i < word.length(); i++) {
            d.addLast(word.charAt(i));
        }

        return d;
    }

    public boolean isPalindrome (String word) {

        return isPalindrome(wordToDeque(word));
    }

    private boolean isPalindrome (Deque<Character> d) {
        if (d.size() <= 1) {
            return true;
        }

        char a = d.removeFirst();
        char b = d.removeLast();

        return (a == b) && isPalindrome(d);
    }

    public boolean isPalindrome (String word, CharacterComparator cc) {
        Deque<Character> d = wordToDeque(word);
        return isPalindrome2 (d, cc);
    }

    private boolean isPalindrome2 (Deque<Character> d, CharacterComparator cc) {
        if (d.size() <= 1) {
            return true;
        }

        char a = d.removeFirst();
        char b = d.removeLast();

        return (cc.equalChars(a, b)) && isPalindrome2(d, cc);
    }
}
