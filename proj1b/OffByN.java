public class OffByN implements CharacterComparator {

    int dis;

    public OffByN (int N) {
        dis = N;
    }


    @Override
    public boolean equalChars(char x, char y) {
        if (Math.abs (x - y) == dis) {
            return true;
        }
        return false;
    }
}
