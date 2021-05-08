public class ArrayDeque<T> {
    private int size;
    private static final int RFACTOR = 2;
    private T[] items;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque () {
        size = 0;
        items = (T[]) new Object[8];
        nextFirst = 0;
        nextLast = 0;
    }

    public ArrayDeque (ArrayDeque other) {
        size = other.size();
        items = (T[]) new Object[size];
        for (int i = 0; i < size; i++) {
            items[i] = (T) other.get(i);
        }
        nextFirst = size - 1;
        nextLast = 0;
    }

    private void resize (int newSize) {
        if (newSize < size()) {
            return;
        }

        T[] temp = (T[]) new Object[newSize];
        for (int i = 0; i < size; i++) {
            int idx = (nextFirst + 1 + i) % items.length;
            temp[i] = items[idx];
        }
        items = temp;
        nextFirst = newSize - 1;
        nextLast = size;
    }

    public void addFirst (T item) {
        if (nextFirst == (nextFirst + size()) % items.length) {
            resize (size * RFACTOR);
        }
        items[nextFirst] = item;
        nextFirst--;
        if (nextFirst < 0) {
            nextFirst += items.length;
        }
        size++;
    }

    public void addLast (T item) {
        if (nextLast == (nextFirst + 1) % items.length) {
            resize (size * RFACTOR);
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;

        size++;
    }

    public boolean isEmpty () {
        return (size == 0);
    }

    public void printDeque () {
        for (int i = 0; i < size; i++) {
            int idx = (nextFirst + i + 1) % items.length;
            if (i != size - 1) {
                System.out.print(items[idx] + " ");
            }
            else {
                System.out.println(items[idx]);
            }
        }
    }

    public T removeFirst () {
        if (size == 0) {
            return null;
        }

        T item = get(0);
        nextFirst = (nextFirst + 1) % items.length;
        size--;

        double ratio = (double) size / items.length;
        if (ratio < 0.25) {
            resize (size / RFACTOR);
        }

        return item;
    }

    public T removeLast () {
        if (size == 0) {
            return null;
        }

        T item = get(size() - 1);
        nextLast--;
        if (nextLast < 0) {
            nextLast += items.length;
        }
        size--;
        return item;
    }

    public T get (int index) {
        if (index < 0 || index > size()) {
            return null;
        }

        return items[(nextFirst + 1 + index) % items.length];
    }

    public int size() {
        return size;
    }
}
